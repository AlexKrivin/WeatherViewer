// ReadLocationTask.java
// Считывает информацию о местоположении в фоновом потоке.

package com.example.alex_k.weatherviewer;

//package com.deitel.weatherviewer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

// преобразует почтовый код в название города в фоновом потоке
class ReadLocationTask extends AsyncTask<Object, Object, String>
{
    private static final String TAG = "ReadLocatonTask.java";

    private String zipcodeString; // почтовый индекс для местоположения
    private Context context; // запуск объекта Context из Activity
    private Resources resources; // используется для поиска строк в коде xml

    // строки для каждого типа выбираемых данных
    private String cityString;
    private String stateString;
    private String countryString;

    // слушатель для выбранной информации
    private LocationLoadedListener weatherLocationLoadedListener;

    // интерфейс приемника информации о местоположении
    public interface LocationLoadedListener
    {
        public void onLocationLoaded(String cityString, String stateString, String countryString);
    } // конец определения интерфейса LocationLoadedListener

    // общедоступный конструктор
    public ReadLocationTask(String zipCodeString, Context context, LocationLoadedListener listener)
    {
        this.zipcodeString = zipCodeString;
        this.context = context;
        this.resources = context.getResources();
        this.weatherLocationLoadedListener = listener;
    } // конец определения конструктора ReadLocationTask

    // загрузка названия города в фоновый поток
    @Override
    protected String doInBackground(Object... params)
    {
        try
        {
// создание URL-ссылки на Weatherbug API
            URL url = new URL(resources.getString(R.string.location_url_pre_zipcode) + zipcodeString + "&api_key=10933d5bb18c21fdc952b1f8f02802af");

// создание InputStreamReader на основе URL-ссылки
            Reader forecastReader = new InputStreamReader(
                    url.openStream());

// создание JsonReader на основе Reader
            JsonReader forecastJsonReader = new JsonReader(forecastReader);
            forecastJsonReader.beginObject(); // чтение первого объекта

// получение следующего названия
            String name = forecastJsonReader.nextName();

// если имя показывает, что следующий элемент описывает
// местоположение почтового индекса
            if (name.equals(resources.getString(R.string.location)))
            {
// начало чтения следующего объекта JSON
                forecastJsonReader.beginObject();

                String nextNameString;

// если есть дополнительная информация для чтения
                while (forecastJsonReader.hasNext())
                {
                    nextNameString = forecastJsonReader.nextName();
// если имя показывает, что следующий элемент описывает
// название города, соответствующее почтовому индексу
                    if ((nextNameString).equals(resources.getString(R.string.city)))
                    {
// считывание названия города
                        cityString = forecastJsonReader.nextString();
                    } // конец блока if
                    else if ((nextNameString).equals(resources.getString(R.string.state)))
                    {
                        stateString = forecastJsonReader.nextString();
                    } // конец блока else if
                    else if ((nextNameString).equals(resources.getString(R.string.country)))
                    {
                        countryString = forecastJsonReader.nextString();
                    } // конец блока else if
                    else
                    {
                        forecastJsonReader.skipValue(); // пропуск
// неожиданного значения
                    } // конец блока else
                } // конец цикла while

                forecastJsonReader.close(); // закрыть JsonReader
            } // конец блока if
        } // конец блока try
        catch (MalformedURLException e)
        {
            Log.v(TAG, e.toString()); // вывод исключения в LogCat
        } // конец блока atch
        catch (IOException e)
        {
            Log.v(TAG, e.toString()); // вывод исключения в LogCat
        } // конец блока catch

        return null; // если название города не найдено, вернуть null
    } // конец определения метода doInBackground

    // выполняется снова в потоке UI после загрузки названия города
    protected void onPostExecute(String nameString)
    {
// если найден город, который соответствует почтовому индексу
        if (cityString != null)
        {
// передача информации обратно LocationLoadedListener
            weatherLocationLoadedListener.onLocationLoaded(cityString, stateString, countryString);
        } // конец блока if
        else
        {
// отображение сообщения Toast, включающего информацию
// о местоположении, не найдено
            Toast errorToast = Toast.makeText(context, resources.getString(R.string.invalid_zipcode_error), Toast.LENGTH_LONG);
            errorToast.setGravity(Gravity.CENTER, 0, 0); // центрирование Toast
            errorToast.show(); // показать Toast
        } // конец блока else
    } // конец описания метода onPostExecute
} // конец описания класса ReadLocationTask
