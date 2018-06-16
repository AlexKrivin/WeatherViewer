// ReadForecastTask.java
// Чтение сведений о погоде за пределами главного потока.
package com.example.alex_k.weatherviewer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

class ReadForecastTask extends AsyncTask<Object, Object, String>
{
    private String zipcodeString; // почтовый индекс для
    // города с прогнозом погоды
    private Resources resources;

    // получает информацию о погоде
    private ForecastListener weatherForecastListener;
    private static final String TAG = "ReadForecastTask.java";

    private String temperatureString; // температура
    private String feelsLikeString; // температура «по ощущениям»
    private String humidityString; // влажность
    private String chanceOfPrecipitationString; // вероятность осадков
    private Bitmap iconBitmap; // иллюстрация облачности

    private int bitmapSampleSize = -1;

    // интерфейс получателя сведений о погоде
    public interface ForecastListener
    {
        public void onForecastLoaded(Bitmap image, String temperature, String feelsLike, String humidity, String precipitation);
    } // конец определения интерфейса ForecastListener

    // создается новый объект ReadForecastTask
    public ReadForecastTask(String zipcodeString, ForecastListener listener, Context context)
    {
        this.zipcodeString = zipcodeString;
        this.weatherForecastListener = listener;
        this.resources = context.getResources();
    } // конец определения конструктора ReadForecastTask

    // настройка размера выборки для Bitmap прогноза
    public void setSampleSize(int sampleSize)
    {
        this.bitmapSampleSize = sampleSize;
    } // конец определения метода setSampleSize

    // загрузка прогноза в фоновом потоке
    protected String doInBackground(Object... args)
    {
        try
        {
// url-ссылка для службы JSON WeatherBug
            URL webServiceURL = new URL(resources.getString(R.string.pre_zipcode_url) + zipcodeString + "&ht=t&ht=i&" + "ht=cp&ht=fl &ht=h&api_key=10933d5bb18c21fdc952b1f8f02802af");

// создание потока Reader на основе url-ссылки WeatherBug
            Reader forecastReader = new InputStreamReader(webServiceURL.openStream());

// создание JsonReader из Reader
            JsonReader forecastJsonReader = new JsonReader(forecastReader);

            forecastJsonReader.beginObject(); // чтение первого объекта

// получение следующего имени
            String name = forecastJsonReader.nextName();

// если это ожидаемое имя данных, относящихся
// к почасовому прогнозу погоды
            if (name.equals(resources.getString(R.string.hourly_forecast)))
            {
                readForecast(forecastJsonReader); // чтение прогноза
            } // конец блока if

            forecastJsonReader.close(); // закрыть JsonReader
        } // конец блока try
        catch (MalformedURLException e)
        {
            Log.v(TAG, e.toString());
        } // конец блока catch
        catch (IOException e)
        {
            Log.v(TAG, e.toString());
        } // конец блока catch
        catch (IllegalStateException e)
        {
            Log.v(TAG, e.toString() + zipcodeString);
        } // конец блока catch
        return null;
    } // конец определения метода doInBackground

    // обновление UI снова в основном потоке
    protected void onPostExecute(String forecastString)
    {
// передача информации ForecastListener
        weatherForecastListener.onForecastLoaded(iconBitmap,
                temperatureString, feelsLikeString, humidityString,
                chanceOfPrecipitationString);
    } // конец определения метода onPostExecute

    // получение иллюстрации Bitmap текущего состояния (облачности) неба
    public static Bitmap getIconBitmap(String conditionString,
                                       Resources resources, int bitmapSampleSize)
    {
        Bitmap iconBitmap = null; // создание объекта Bitmap
        try
        {
// создание URL-ссылки на изображение, находящееся
// на сайте WeatherBug
            URL weatherURL = new URL(resources.getString(R.string.pre_condition_url) + conditionString + resources.getString(R.string.post_condition_url));

            BitmapFactory.Options options = new BitmapFactory.Options();
            if (bitmapSampleSize != -1)
            {
                options.inSampleSize = bitmapSampleSize;
            } // конец блока if

// сохранение изображения как Bitmap
            iconBitmap = BitmapFactory.decodeStream(weatherURL.openStream(), null, options);
        } // конец блока try
        catch (MalformedURLException e)
        {
            Log.e(TAG, e.toString());
        } // конец блока catch
        catch (IOException e)
        {
            Log.e(TAG, e.toString());
        } // конец блока catch

        return iconBitmap; // возврат изображения
    } // конец определения метода getIconBitmap

    // чтение информации прогноза с помощью данного JsonReader
    private String readForecast(JsonReader reader)
    {
        try
        {
            reader.beginArray(); // начало чтения следующего массива
            reader.beginObject(); // начало чтения следующего объекта

// пока есть следующий элемент в текущем объекте
            while (reader.hasNext())
            {
                String name = reader.nextName(); // чтение следующего имени

// если этот элемент temperature
                if (name.equals(resources.getString(R.string.temperature)))
                {
// чтение элемента temperature (температура)
                    temperatureString = reader.nextString();
                } // конец блока if
// если этот элемент "feels-like" temperature
// (температура «по ощущениям»)
                else if (name.equals(resources.getString(R.string.feels_like)))
                {
// чтение элемента "feels-like" temperature
// (температура «по ощущениям»)
                    feelsLikeString = reader.nextString();
                } // конец блока else if
// если этот элемент humidity (влажность)
                else if (name.equals(resources.getString(R.string.humidity)))
                {
                    humidityString = reader.nextString(); // чтение humidity
                } // конец блока else if
// если следующий элемент chance of precipitation
// (вероятность осадков)
                else if (name.equals(resources.getString(R.string.chance_of_precipitation)))
                {
// чтение элемента chance of precipitation
                    chanceOfPrecipitationString = reader.nextString();
                } // конец блока else if
// если следующий элемент icon name (имя значка)
                else if (name.equals(resources.getString(R.string.icon)))
                {
// чтение элемента icon name
                    iconBitmap = getIconBitmap(reader.nextString(), resources, bitmapSampleSize);
                } // конец блока else if
                else // непредвиденный элемент
                {
                    reader.skipValue(); // пропуск следующего элемента
                } // конец блока else
            } // конец блока while
        } // конец блока try
        catch (IOException e)
        {
            Log.e(TAG, e.toString());
        } // конец блока catch
        return null;
    } // конец описания метода readForecast
} // конец описания ReadForecastTask

