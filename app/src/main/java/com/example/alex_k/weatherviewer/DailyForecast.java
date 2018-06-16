// DailyForecast.java
// Представляет однодневный прогноз.

package com.example.alex_k.weatherviewer;
//package com.deitel.weatherviewer;

import android.graphics.Bitmap;

public class DailyForecast
{
    // индексы для всей информации о прогнозах
    public static final int DAY_INDEX = 0;
    public static final int PREDICTION_INDEX = 1;
    public static final int HIGH_TEMP_INDEX = 2;
    public static final int LOW_TEMP_INDEX = 3;

    final private String[] forecast; // массив информации
    // для всех прогнозов
    final private Bitmap iconBitmap; // изображение, представляющее
// прогноз

    // создание нового DailyForecast
    public DailyForecast(String[] forecast, Bitmap iconBitmap)
    {
        this.forecast = forecast;
        this.iconBitmap = iconBitmap;
    } // конец определения DailyForecast

    // получение иллюстрации для прогноза
    public Bitmap getIconBitmap()
    {
        return iconBitmap;
    } // конец определения метода getIconBitmap

    // получение дня недели прогноза
    public String getDay()
    {
        return forecast[DAY_INDEX];
    } // конец определения метода getDay

    // получение краткого описания прогноза
    public String getDescription()
    {
        return forecast[PREDICTION_INDEX];
    } // конец описания метода getDescription

    // возврат максимальной температуры прогноза
    public String getHighTemperature()
    {
        return forecast[HIGH_TEMP_INDEX];
    } // конец определения метода getHighTemperature

    // возврат минимальной температуры прогноза
    public String getLowTemperature()
    {
        return forecast[LOW_TEMP_INDEX];
    } // конец описания метода getLowTemperature
} // конец описания класса DailyForecast