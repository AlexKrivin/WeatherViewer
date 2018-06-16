// WeatherProvider.java
// Обновления виджета приложения Weather
//package com.deitel.weatherviewer;

package com.example.alex_k.weatherviewer;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.alex_k.weatherviewer.ReadForecastTask.ForecastListener;
import com.example.alex_k.weatherviewer.ReadLocationTask.LocationLoadedListener;

public class WeatherProvider extends AppWidgetProvider
{
    // размер выборки для изображения прогноза Bitmap
    private static final int BITMAP_SAMPLE_SIZE = 4;

    // обновляет все установленные виджеты Weather App
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        startUpdateService(context); // запуск новой службы WeatherService
    } // конец определения метода onUpdate

    // получение сохраненного почтового индекса для этого виджета приложения
    private String getZipcode(Context context)
    {
// получение SharedPreferences для приложения
        SharedPreferences preferredCitySharedPreferences = context.getSharedPreferences(WeatherViewerActivity.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

// получение почтового индекса для предпочтительного города
// из SharedPreferences
        String zipcodeString = preferredCitySharedPreferences.getString(WeatherViewerActivity.PREFERRED_CITY_ZIPCODE_KEY, context.getResources().getString(R.string.default_zipcode));
        return zipcodeString; // return the ZIP code string
    } // конец определения метода getZipcode

    // вызывается, если данный AppWidgetProvider получает
// широковещателный Intent
    @Override
    public void onReceive(Context context, Intent intent)
    {
// если предпочтительный город был изменен в приложении
        if (intent.getAction().equals(
                WeatherViewerActivity.WIDGET_UPDATE_BROADCAST_ACTION))
        {
            startUpdateService(context); // прогноз для нового города
        } // конец блока if
        super.onReceive(context, intent);
    } // конец описания метода onReceive

    // запуск новой службы WeatherService, обновляющей
// прогноз, отображаемый виджетом приложения
    private void startUpdateService(Context context)
    {
// создание нового объекта Intent, запускающего
// службу WeatherService
        Intent startServiceIntent;
        startServiceIntent = new Intent(context, WeatherService.class);

// включение почтового индекса в качестве дополнения Intent
        startServiceIntent.putExtra(context.getResources().getString(
                R.string.zipcode_extra), getZipcode(context));
        context.startService(startServiceIntent);
    } // конец определения метода startUpdateService

    // обновляет виджет приложения Weather Viewer
    public static class WeatherService extends IntentService implements ForecastListener
    {
        public WeatherService()
        {
            super(WeatherService.class.toString());
        } // конец определения конструктора WeatherService

        private Resources resources; // ресурсы приложения
        private String zipcodeString; // почтовый индекс
        // предпочтительного города
        private String locationString; // местоположение предпочтительного
// города

        @Override
        protected void onHandleIntent(Intent intent)
        {
            resources = getApplicationContext().getResources();
            zipcodeString = intent.getStringExtra(resources.getString(R.string.zipcode_extra));

// загрузка в фоновый поток информации о местоположении
           new ReadLocationTask(zipcodeString, this, new WeatherServiceLocationLoadedListener(zipcodeString)).execute();
        } // конец определения метода onHandleIntent

        // получает информацию о погоде из ReadForecastTask
        @Override
        public void onForecastLoaded(Bitmap image, String temperature, String feelsLike, String humidity, String precipitation)
        {
            Context context = getApplicationContext();

            if (image == null) // отсутствуют возвращаемые данные
            {
                Toast.makeText(context, context.getResources().getString(R.string.null_data_toast), Toast.LENGTH_LONG);
                return; // выход перед обновлением прогноза
            } // конец блока if

// создание объекта PendingIntent, используемого
// для запуска WeatherViewerActivity
            Intent intent = new Intent(context, WeatherViewerActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);

// получение компонентов RemoteViews для виджета приложения
            RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.weather_app_widget_layout);

// настройка PendingIntent для запуска виджета приложения
// после щелчка пользователя
            remoteView.setOnClickPendingIntent(R.id.containerLinearLayout, pendingIntent);

// отображение информации о местоположении
            remoteView.setTextViewText(R.id.location, locationString);

// отображение температуры
            remoteView.setTextViewText(R.id.temperatureTextView, temperature + (char)0x00B0 + resources.getString(R.string.temperature_unit));

// отображение температуры "по ощущениям"
            remoteView.setTextViewText(R.id.feels_likeTextView, feelsLike + (char)0x00B0 + resources.getString(R.string.temperature_unit));

// отображение влажности воздуха
            remoteView.setTextViewText(R.id.humidityTextView, humidity + (char)0x0025);

// отображение вероятности осадков
            remoteView.setTextViewText(R.id.precipitationTextView, precipitation + (char)0x0025);

// отображение картинки прогноза
            remoteView.setImageViewBitmap(R.id.weatherImageView, image);

// получение Component Name для идентификации обновляемого виджета
            ComponentName widgetComponentName = new ComponentName(this, WeatherProvider.class);

// получение глобального AppWidgetManager
            AppWidgetManager manager = AppWidgetManager.getInstance(this);

// обновление AppWdiget Weather
            manager.updateAppWidget(widgetComponentName, remoteView);
        } // конец определения метода onForecastLoaded

        // получает сведения о местоположении от фоновой задачи
        private class WeatherServiceLocationLoadedListener implements LocationLoadedListener
        {
            private String zipcodeString; // почтовый индекс для просмотра

            // создание нового класса WeatherLocationLoadedListener
            public WeatherServiceLocationLoadedListener(String zipcodeString)
            {
                this.zipcodeString = zipcodeString;
            } // конец определения класса WeatherLocationLoadedListener

            // вызывается после загрузки информации о местоположении
            @Override
            public void onLocationLoaded(String cityString, String stateString, String countryString)
            {
                Context context = getApplicationContext();

                if (cityString == null) // если нет возвращаемых данных
                {
                    Toast.makeText(context, context.getResources().getString(R.string.null_data_toast), Toast.LENGTH_LONG);
                    return; // выход перед обновлением прогноза
                } // конец блока if

// отображение возвращаемой информации в TextView
                locationString = cityString + " " + stateString + ", " + zipcodeString + " " + countryString;

// запуск новой ReadForecastTask
                ReadForecastTask readForecastTask = new ReadForecastTask(zipcodeString, (ForecastListener) WeatherService.this, WeatherService.this);

// ограничение размера Bitmap
                readForecastTask.setSampleSize(BITMAP_SAMPLE_SIZE);
                readForecastTask.execute();
            } // конец определения метода onLocationLoaded
        }// конец определения класса WeatherServiceLocationLoadedListener
    } // конец определения класса WeatherService
} // конец определения WeatherProvider

