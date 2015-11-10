package com.example.agorbacheva.againweather;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherFragment extends Fragment {

    ListView listview_forecast;
    ListView lvMain;


    Typeface weatherFont;

    Button changeCityButton;
    Button imageButtonCity;
    EditText editText;
    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTempField;
    TextView weatherIcon;
    String city;
    private ArrayAdapter<Weather> adapter;

    SharedPreferences sPref;

    Handler handler;

    public WeatherFragment(){
        handler = new Handler();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){

        View rootView = inflater.inflate(R.layout.content_weather,container,false);
        setHasOptionsMenu(true);
        listview_forecast = (ListView) rootView.findViewById(R.id.listView_forecast);

        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTempField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);
        listview_forecast = (ListView)rootView.findViewById(R.id.listView_forecast);

        weatherIcon.setTypeface(weatherFont);
        updateWeatherData(new CityPreference(getActivity()).getCity());

        listview_forecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent intent = new Intent(getActivity(), WeatherDetailActivity.class);
                Weather weather = adapter.getItem(position);
                intent.putExtra("WEEKDAY", weather.weekday);
                intent.putExtra("MAIN", weather.main);
                intent.putExtra("MAX", weather.max);
                intent.putExtra("MIN", weather.min);
                getActivity().startActivity(intent);
            }
        });


        //Button update weather
        ImageButton imageButton = (ImageButton) rootView.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateWeatherData(new CityPreference(getActivity()).getCity());
                Log.i(new CityPreference(getActivity()).getCity(), "city now");
            }

        });

        //Button change city
        ImageButton imageButtonCity = (ImageButton) rootView.findViewById(R.id.imageButtonCity);
        imageButtonCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Activity activity = getActivity();
                AlertDialog.Builder changeCityBuilder = new AlertDialog.Builder(activity);
                changeCityBuilder.setTitle("Настройки");
                changeCityBuilder.setMessage(getString(R.string.write_city_name));
                final EditText cityName = new EditText(activity);
                cityName.setSingleLine(true);
                changeCityBuilder.setView(cityName);

                changeCityBuilder.setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                city = cityName.getText().toString();
                                saveCity();
                                updateWeatherData(city);
                            }
                        });
                changeCityBuilder.setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                changeCityBuilder.show();
            }
        });

        return rootView;
    }

    private void saveCity() {
        Activity activity = getActivity();
        sPref = activity.getPreferences(activity.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("CITY", city);
        ed.commit();
    }

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(),"fonts/weather.ttf");
    }

    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON(getActivity(),city);

                if(json == null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),getActivity().getString(R.string.place_not_found),Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json){
        List<Weather> forecastData = new ArrayList<>();

        try{
            //City
            cityField.setText(json.getJSONObject("city").getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("city").getString("country"));

            SimpleDateFormat formantToday = new SimpleDateFormat("d MMMM", new Locale("ru", "RU"));
            SimpleDateFormat formatWeekDay = new SimpleDateFormat("EEEE", new Locale("ru", "RU"));
            JSONArray list = json.getJSONArray("list");

            for (int i = 0; i < list.length(); i++) {
                //Weekday
                JSONObject day = list.getJSONObject(i);
                long dt = day.getLong("dt");
                String weekday = formatWeekDay.format(new Date(dt * 1000)).toString();

                //Temperature Weekday
                JSONObject temp = day.getJSONObject("temp");
                int min = (int) Math.round(temp.getDouble("min"));
                int max = (int) Math.round(temp.getDouble("max"));

                JSONArray weather = day.getJSONArray("weather");
                String main = weather.getJSONObject(0).getString("main");

                //Today
                JSONObject dayToday = list.getJSONObject(0);
                long td = dayToday.getLong("dt");
                String today = formantToday.format(new Date(td * 1000)).toString();
                detailsField.setText(today);

                //Temperature today
                JSONObject todayTemp = dayToday.getJSONObject("temp");
                int todayMin = (int) Math.round(todayTemp.getDouble("min"));
                int todayMax = (int) Math.round(todayTemp.getDouble("max"));
                currentTempField.setText(todayMin + " / " + todayMax);

                forecastData.add(new Weather(weekday, main, max, min));

            }

        }catch (Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
        showInTheList(forecastData);
    }

    private void showInTheList(List<Weather> forecastData) {
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, forecastData);
        listview_forecast.setAdapter(adapter);
    }

//    private void setWeatherIcon(int actualId, long sunrise, long sunset){
//        int id = actualId/100;
//        String icon = "";
//        if(actualId == 800){
//            long currentTime = new Date().getTime();
//            if(currentTime >= sunrise && currentTime <= sunset){
//                icon = getActivity().getString(R.string.weather_sunny);
//            }else{
//                icon = getActivity().getString(R.string.weather_clear_night);
//            }
//        }else{
//            switch (id){
//                case 2: icon = getActivity().getString(R.string.weather_thunder);
//                    break;
//                case 3: icon = getActivity().getString(R.string.weather_drizzle);
//                    break;
//                case 7: icon = getActivity().getString(R.string.weather_foggy);
//                    break;
//                case 8: icon = getActivity().getString(R.string.weather_cloudy);
//                    break;
//                case 6: icon = getActivity().getString(R.string.weather_snowy);
//                    break;
//                case 5: icon = getActivity().getString(R.string.weather_rainy);
//                    break;
//            }
//        }
//        weatherIcon.setText(icon);
//    }

    public void changeCity(String city){
        updateWeatherData(city);
    }
}