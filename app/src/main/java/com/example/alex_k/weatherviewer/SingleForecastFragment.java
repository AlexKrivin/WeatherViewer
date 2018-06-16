// SingleForecastFragment.java
// Отображение текущей погоды для одного города.

package com.example.alex_k.weatherviewer;

//package com.deitel.weatherviewer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex_k.weatherviewer.ReadForecastTask.ForecastListener;
import com.example.alex_k.weatherviewer.ReadLocationTask.LocationLoadedListener;

public class SingleForecastFragment extends ForecastFragment
{
    private String zipcodeString; // почтовый индекс для данного прогноза

    // поиск ключей для сохраненного состояния объекта Fragment
    private static final String LOCATION_KEY = "location";
    private static final String TEMPERATURE_KEY = "temperature";
    private static final String FEELS_LIKE_KEY = "feels_like";
    private static final String HUMIDITY_KEY = "humidity";
    private static final String PRECIPITATION_KEY = "chance_precipitation";
    private static final String IMAGE_KEY = "image";

    // применяется для выборки почтового индекса для сохраненного Bundle
    private static final String ZIP_CODE_KEY = "id_key";

    private View forecastView; // содержит все виды прогноза
    private TextView temperatureTextView; // отображение фактической
    // температуры
    private TextView feelsLikeTextView; // отображение температуры
    // "по ощущениям"
    private TextView humidityTextView; // отображение влажности

    private TextView locationTextView;

    // отображение процента вероятности прогноза
    private TextView chanceOfPrecipitationTextView;
    private ImageView conditionImageView; // изображение облачности
    private TextView loadingTextView;
    private Context context;
    private Bitmap conditionBitmap;

    // создает новый объект ForecastFragment для данного почтового индекса
    public static SingleForecastFragment newInstance(String zipcodeString)
    {
// создает новый ForecastFragment
        SingleForecastFragment newForecastFragment =
                new SingleForecastFragment();

        Bundle argumentsBundle = new Bundle(); // создание нового Bundle

// сохранение данной строки в Bundle
        argumentsBundle.putString(ZIP_CODE_KEY, zipcodeString);

// настройка аргументов Fragement
        newForecastFragment.setArguments(argumentsBundle);
        return newForecastFragment; // return the completed ForecastFragment
    } // конец определения метода newInstance

    // создание нового ForecastFragment на основе данного Bundle
    public static SingleForecastFragment newInstance(Bundle argumentsBundle)
    {
// получение почтового индекса для данного Bundle
        String zipcodeString = argumentsBundle.getString(ZIP_CODE_KEY);
        return newInstance(zipcodeString);//создание нового ForecastFragment
    } // конец определения метода newInstance

    // создание объекта Fragment на основе сохраненного состояния Bundle
    @Override
    public void onCreate(Bundle argumentsBundle)
    {
        super.onCreate(argumentsBundle);

// получение почтового индекса на основе данного Bundle
        this.zipcodeString = getArguments().getString(ZIP_CODE_KEY);
    } // конец определения метода onCreate

    // сохранение состояния объекта Fragment
    @Override
    public void onSaveInstanceState(Bundle savedInstanceStateBundle)
    {
        super.onSaveInstanceState(savedInstanceStateBundle);

// сохранение содержимого View в Bundle
        savedInstanceStateBundle.putString(LOCATION_KEY, locationTextView.getText().toString());
        savedInstanceStateBundle.putString(TEMPERATURE_KEY, temperatureTextView.getText().toString());
        savedInstanceStateBundle.putString(FEELS_LIKE_KEY, feelsLikeTextView.getText().toString());
        savedInstanceStateBundle.putString(HUMIDITY_KEY, humidityTextView.getText().toString());
        savedInstanceStateBundle.putString(PRECIPITATION_KEY, chanceOfPrecipitationTextView.getText().toString());
        savedInstanceStateBundle.putParcelable(IMAGE_KEY, conditionBitmap);
    } // конец определения метода onSaveInstanceState

    // общий доступ к почтовому индексу, соответствующему
// информации о прогнозе для данного объекта Fragment
    public String getZipcode()
    {
        return zipcodeString; // возвращение строки String,
// содержащей почтовый индекс
    } // конец определения метода getZIP code

    // "раздувание" разметки для данного Fragement из xml-файла
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
// использование данного LayoutInflator для "раздувания"
// разметки, находящейся в файле forecast_fragment_layout.xml
        View rootView = inflater.inflate(R.layout.forecast_fragment_layout,null);
// получение TextView в иерархии разметок Fragment
        forecastView = rootView.findViewById(R.id.forecast_layout);
        loadingTextView = (TextView) rootView.findViewById(R.id.loading_message);
        locationTextView = (TextView) rootView.findViewById(R.id.location);
        temperatureTextView = (TextView) rootView.findViewById(R.id.temperature);
        feelsLikeTextView = (TextView) rootView.findViewById(R.id.feels_like);
        humidityTextView = (TextView) rootView.findViewById(R.id.humidity);
        chanceOfPrecipitationTextView = (TextView) rootView.findViewById(R.id.chance_of_precipitation);
        conditionImageView = (ImageView) rootView.findViewById(R.id.forecast_image);

        context = rootView.getContext(); // сохранение Context

        return rootView; // возврат к "раздутой" версии View
    } // конец определения метода onCreateView

    // вызывается после создания родительского объекта Activity
    @Override
    public void onActivityCreated(Bundle savedInstanceStateBundle)
    {
        super.onActivityCreated(savedInstanceStateBundle);

        // если сохраненная информация отсутствует
        if (savedInstanceStateBundle == null)
        {
// скрытие прогноза и отображение сообщения о загрузке
            forecastView.setVisibility(View.GONE);
            loadingTextView.setVisibility(View.VISIBLE);

// загрузка информации о местоположении в фоновый поток
            new ReadLocationTask(zipcodeString, context,
                    new WeatherLocationLoadedListener(zipcodeString)).execute();
        } // конец блока if
        else
        {
// отображение информации о сохраненном состоянии Bundle
// с помощью компонентов View класса Fragment
            conditionImageView.setImageBitmap(
                    (Bitmap) savedInstanceStateBundle.getParcelable(IMAGE_KEY));
            locationTextView.setText(savedInstanceStateBundle.getString(
                    LOCATION_KEY));
            temperatureTextView.setText(savedInstanceStateBundle.getString(
                    TEMPERATURE_KEY));
            feelsLikeTextView.setText(savedInstanceStateBundle.getString(
                    FEELS_LIKE_KEY));
            humidityTextView.setText(savedInstanceStateBundle.getString(
                    HUMIDITY_KEY));
            chanceOfPrecipitationTextView.setText(
                    savedInstanceStateBundle.getString(PRECIPITATION_KEY));
        } // конец блока else
    } // конец определения метода onActivityCreated

    // получает информацию о погоде из AsyncTask
    ForecastListener weatherForecastListener = new ForecastListener()
    {
        // отображает сведения о прогнозе погоды
        @Override
        public void onForecastLoaded(Bitmap imageBitmap, String temperatureString, String feelsLikeString, String humidityString, String precipitationString)
        {
// если Fragment был отсоединен во время выполнения фонового процесса
            if (!SingleForecastFragment.this.isAdded())
            {
                return; // выйти из метода
            } // конец блока if
            else if (imageBitmap == null)
            {
                Toast errorToast = Toast.makeText(context, context.getResources().getString(R.string.null_data_toast), Toast.LENGTH_LONG);
                errorToast.setGravity(Gravity.CENTER, 0, 0);
                errorToast.show(); // show the Toast
                return; // выйти перед обновлением прогноза
            } // конец блока if

            Resources resources = SingleForecastFragment.this.getResources();

// отображение загруженной информации
            conditionImageView.setImageBitmap(imageBitmap);
            conditionBitmap = imageBitmap;
            temperatureTextView.setText(temperatureString + (char)0x00B0 + resources.getString(R.string.temperature_unit));
            feelsLikeTextView.setText(feelsLikeString + (char)0x00B0 + resources.getString(R.string.temperature_unit));
            humidityTextView.setText(humidityString + (char)0x0025);
            chanceOfPrecipitationTextView.setText(precipitationString + (char)0x0025);
            loadingTextView.setVisibility(View.GONE); // скрыть сообщение
// о загрузке
            forecastView.setVisibility(View.VISIBLE); // отображение прогноза
        } // конец определения метода onForecastLoaded
    }; // конец определения weatherForecastListener

    // получает информацию о местоположении от фоновой задачи
    private class WeatherLocationLoadedListener implements LocationLoadedListener
    {
        private String zipcodeString; // почтовый индекс для просмотра

        // создание нового интерфейса WeatherLocationLoadedListener
        public WeatherLocationLoadedListener(String zipcodeString)
        {
            this.zipcodeString = zipcodeString;
        } // конец определения интерфейса WeatherLocationLoadedListener

        // вызывается после загрузки информации о местоположении
        @Override
        public void onLocationLoaded(String cityString, String stateString, String countryString)
        {
            if (cityString == null) // если возвращаемые данные отсутствуют
            {
// отображение сообщения об ошибке
                Toast errorToast = Toast.makeText(context, context.getResources().getString(R.string.null_data_toast), Toast.LENGTH_LONG);
                errorToast.setGravity(Gravity.CENTER, 0, 0);
                errorToast.show(); // отображение сообщения Toast
                return; // выход перед обновлением прогноза
            } // конец блока if
// отображение информации, возвращенной в TextView
            locationTextView.setText(cityString + " " + stateString + ", " + zipcodeString + " " + countryString);
// загрузка прогноза в фоновый поток
            new ReadForecastTask(zipcodeString, weatherForecastListener, locationTextView.getContext()).execute();
        } // конец определения метода onLocationLoaded
    } // конец определения класса LocationLoadedListener
} // конец определения класса SingleForecastFragment