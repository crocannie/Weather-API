package com.example.agorbacheva.againweather;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteFetch {

    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/forecast/daily?q=%s&mode=json&units=%s&cnt=%s&appid=%s";

    public static JSONObject getJSON(Context context, String city){
        String units = "metric";
        Integer days = 7;
        String appid = "d3abcf9ca5e8519b1347a43175331f28";
        try{
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API,city, units, days, appid));

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.addRequestProperty("d3abcf9ca5e8519b1347a43175331f28",context.getString(R.string.open_weather_maps_app_id));

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";

            while((tmp=reader.readLine())!=null){
                json.append(tmp).append("\n");
            }
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            Log.i("Input json", json.toString());

            if(data.getInt("cod") != 200)
                return null;

            return data;
        }catch (Exception e){
            return null;
        }
    }

}