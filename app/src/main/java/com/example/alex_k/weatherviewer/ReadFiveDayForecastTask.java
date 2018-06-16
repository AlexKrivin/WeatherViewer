// ReadFiveDayForecastTask.java
// Чтение следующего 5-дневного прогноза в фоновом процессе.

package com.example.alex_k.weatherviewer;
//package com.deitel.weatherviewer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

class ReadFiveDayForecastTask extends AsyncTask<Object, Object, String>
{
    private static final String TAG = "ReadFiveDayForecastTask";

    private String zipcodeString;
    private FiveDayForecastLoadedListener weatherFiveDayForecastListener;
    private Resources resources;
    private DailyForecast[] forecasts;
    private static final int NUMBER_OF_DAYS = 5;

    // интерфейс приемника сведений о погоде
    public interface FiveDayForecastLoadedListener
    {
        public void onForecastLoaded(DailyForecast[] forecasts);
    } // конец определения интерфейса FiveDayForecastLoadedListener

    // создает новый объект ReadForecastTask
    public ReadFiveDayForecastTask(String zipcodeString, FiveDayForecastLoadedListener listener, Context context)
    {
        this.zipcodeString = zipcodeString;
        this.weatherFiveDayForecastListener = listener;
        this.resources = context.getResources();
        this.forecasts = new DailyForecast[NUMBER_OF_DAYS];
    } // конец определения конструктора ReadFiveDayForecastTask

    @Override
    protected String doInBackground(Object... params)
    {
// url-ссылка для службы JSON WeatherBug
        try
        {
            URL webServiceURL = new URL("api.openweathermap.org/data/2.5/" + "weather?zip="+ zipcodeString + "&ht=t&ht=i&" + "nf=7&ht=cp&ht=fl &ht=h&api_key=10933d5bb18c21fdc952b1f8f02802af");

// создание Reader для потока на основе url-ссылки WeatherBug
            Reader forecastReader = new InputStreamReader(webServiceURL.openStream());

// создание JsonReader на основе Reader
            JsonReader forecastJsonReader = new JsonReader(forecastReader);

            forecastJsonReader.beginObject(); // чтение следующего Object

// получение следующего имени
            String name = forecastJsonReader.nextName();

// если его имя ожидается в информации ежедневного прогноза
            if (name.equals(resources.getString(R.string.forecast_list)))
            {
                forecastJsonReader.beginArray(); // начало чтения
// первого массива
                forecastJsonReader.skipValue(); // пропуск сегодняшнего
// прогноза

// чтение следующих пяти ежедневных прогнозов
                for (int i = 0; i < NUMBER_OF_DAYS; i++)
                {
// начало чтения следующего объекта
                    forecastJsonReader.beginObject();

// если есть больше данных
                    if (forecastJsonReader.hasNext())
                    {
// чтение следующего прогноза
                        forecasts[i] = readDailyForecast(forecastJsonReader);
                    } // конец блока if
                } // конец цикла for
            } // конец блока if

            forecastJsonReader.close(); // закрытие JsonReader

        } // конец блока try
        catch (MalformedURLException e)
        {
            Log.v(TAG, e.toString());
        } // конец блока catch
        catch (NotFoundException e)
        {
            Log.v(TAG, e.toString());
        } // конец блока catch
        catch (IOException e)
        {
            Log.v(TAG, e.toString());
        } // конец блока catch
        return null;
    } // конец описания метода doInBackground

    // чтение ежедневного прогноза
    private DailyForecast readDailyForecast(JsonReader forecastJsonReader)
    {
// создание массива для хранения информации о прогнозах
        String[] dailyForecast = new String[4];
        Bitmap iconBitmap = null; // хранение иллюстрации прогноза

        try
        {
// пока есть следующий элемент в текущем объекте
            while (forecastJsonReader.hasNext())
            {
                String name = forecastJsonReader.nextName(); // чтение
// следующего имени

                if (name.equals(resources.getString(R.string.day_of_week)))
                {
                    dailyForecast[DailyForecast.DAY_INDEX] = forecastJsonReader.nextString();
                } // конец блока if
                else if (name.equals(resources.getString(R.string.day_prediction)))
                {
                    dailyForecast[DailyForecast.PREDICTION_INDEX] = forecastJsonReader.nextString();
                } // конец блока end else if
                else if (name.equals(resources.getString(R.string.high)))
                {
                    dailyForecast[DailyForecast.HIGH_TEMP_INDEX] = forecastJsonReader.nextString();
                } // конец блока end else if
                else if (name.equals(resources.getString(R.string.low)))
                {
                    dailyForecast[DailyForecast.LOW_TEMP_INDEX] = forecastJsonReader.nextString();
                } // конец блока else if
// если следующий элемент — имя значка
                else if (name.equals(resources.getString(R.string.day_icon)))
                {
// чтение имени значка
                    iconBitmap = ReadForecastTask.getIconBitmap(forecastJsonReader.nextString(), resources, 0);
                } // конец блока else if
                else // если есть непредвиденный элемент
                {
                    forecastJsonReader.skipValue(); //пропуск
// следующего элемента
                } // конец блока else
            } // конец цикла while
            forecastJsonReader.endObject();
        } // конец блока try
        catch (IOException e)
        {
            Log.e(TAG, e.toString());
        } // конец блока catch

        return new DailyForecast(dailyForecast, iconBitmap);
    } // конец описания метода readDailyForecast

    // обновление UI в основном потоке
    protected void onPostExecute(String forecastString)
    {
        weatherFiveDayForecastListener.onForecastLoaded(forecasts);
    } // конец описания метода onPostExecute
} // конец описания класса ReadFiveDayForecastTask