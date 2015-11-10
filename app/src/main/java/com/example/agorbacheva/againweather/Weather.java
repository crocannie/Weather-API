package com.example.agorbacheva.againweather;

public class Weather {

    String weekday;
    String main;
    int max;
    int min;
    double speed;

    public Weather(String weekday, String main, int max, int min) {
        this.weekday = weekday;
        this.main = main;
        this.max = max;
        this.min = min;
    }
    @Override
    public String toString() {
        return weekday + ": " + main + " : " + max + '/' + min;
    }

}