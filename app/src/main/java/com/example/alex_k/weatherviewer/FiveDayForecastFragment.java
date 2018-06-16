// FiveDayForecastFragment.java
// Отображает пятидневный прогноз для одного города.

package com.example.alex_k.weatherviewer;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex_k.weatherviewer.ReadFiveDayForecastTask.FiveDayForecastLoadedListener;
import com.example.alex_k.weatherviewer.ReadLocationTask.LocationLoadedListener;

public class FiveDayForecastFragment extends ForecastFragment
{
    // используется для выборки почтового индекса для сохраненного Bundle
    private static final String ZIP_CODE_KEY = "id_key";
    private static final int NUMBER_DAILY_FORECASTS = 5;

    private String zipcodeString; // почтовый индекс для этого прогноза
    private View[] dailyForecastViews = new View[NUMBER_DAILY_FORECASTS];

    private TextView locationTextView;

    // создает новый FiveDayForecastFragment для данного почтового индекса
    public static FiveDayForecastFragment newInstance(String zipcodeString)
    {
// создается новый ForecastFragment
        FiveDayForecastFragment newFiveDayForecastFragment = new FiveDayForecastFragment();

        Bundle argumentsBundle = new Bundle(); // создание нового
// объекта Bundle

// сохранение данной строки String в Bundle
        argumentsBundle.putString(ZIP_CODE_KEY, zipcodeString);

// настройка аргументов класса Fragement
        newFiveDayForecastFragment.setArguments(argumentsBundle);
        return newFiveDayForecastFragment;// возврат к завершенному Fragment
    } // конец определения метода newInstance

    // создание объекта FiveDayForecastFragment с помощью данного Bundle
    public static FiveDayForecastFragment newInstance(Bundle argumentsBundle)
    {
// получение почтового индекса из данного Bundle
        String zipcodeString = argumentsBundle.getString(ZIP_CODE_KEY);
        return newInstance(zipcodeString); // создание нового Fragment
    } // конец определения метода newInstance

    // создание объекта Fragment на Bundle для сохраненного состояния
    @Override
    public void onCreate(Bundle argumentsBundle)
    {
        super.onCreate(argumentsBundle);

// получение почтового индекса из текущего Bundle
        this.zipcodeString = getArguments().getString(ZIP_CODE_KEY);

    } // конец описания метода onCreate

    // общий доступ к почтовому индексу для информации
// о прогнозе для данного объекта Fragment
    public String getZipcode()
    {
        return zipcodeString; // возврат почтового индекса типа String
    } // конец описания метода getZipcode

    // «раздувает» разметку объекта Fragement из xml-файла
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
// «раздувает» макет для пятидневного прогноза
        View rootView = inflater.inflate(R.layout.five_day_forecast_layout, null);
// получение TextView, отображающего информацию о местоположении
        locationTextView = (TextView) rootView.findViewById(R.id.location);

// получение ViewGroup, включающего разметки
// для ежедневного прогноза
        LinearLayout containerLinearLayout = (LinearLayout) rootView.findViewById(R.id.containerLinearLayout);

        int id; // идентификатор int для разметки ежедневного прогноза

// если выбрана альбомная ориентация
        if (container.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            id = R.layout.single_forecast_layout_landscape;
        } // конец блока if
else // портретная ориентация
        {
            id = R.layout.single_forecast_layout_portrait;
            containerLinearLayout.setOrientation(LinearLayout.VERTICAL);
        } // конец блока else

// загрузка пятидневных прогнозов погоды
        View forecastView;
        for (int i = 0; i < NUMBER_DAILY_FORECASTS; i++)
        {
            forecastView = inflater.inflate(id, null); // «раздувание»
// нового View

// добавление нового View в контейнер LinearLayout
            containerLinearLayout.addView(forecastView);
            dailyForecastViews[i] = forecastView;
        } // конец цикла for

// загрузка сведений о местоположении в фоновый поток
        new ReadLocationTask(zipcodeString, rootView.getContext(), new WeatherLocationLoadedListener(zipcodeString, rootView.getContext())).execute();

        return rootView;
    } // конец определения метода onCreateView

    // получает сведения о местоположении от фоновой задачи
    private class WeatherLocationLoadedListener implements LocationLoadedListener
    {
        private String zipcodeString; // просматриваемый почтовый индекс
        private Context context;

        // создание нового слушателя WeatherLocationLoadedListener
        public WeatherLocationLoadedListener(String zipcodeString, Context context)
        {
            this.zipcodeString = zipcodeString;
            this.context = context;
        } // конец определения WeatherLocationLoadedListener

        // вызывается, если загружена информация о местоположении
        @Override
        public void onLocationLoaded(String cityString, String stateString, String countryString)
        {
            if (cityString == null) // если нет возвращаемых данных
            {
// отображение сообщения об ошибке
                Toast errorToast = Toast.makeText(context, context.getResources().getString(R.string.null_data_toast), Toast.LENGTH_LONG);
                errorToast.setGravity(Gravity.CENTER, 0, 0);
                errorToast.show(); // показать Toast
                return; // выход перед обновлением прогноза
            } // конец блока if

// отображение информации, возвращаемой TextView
            locationTextView.setText(cityString + " " + stateString + ", " + zipcodeString + " " + countryString);

// загрузка прогноза в фоновом потоке
            //error was in zip code string missing parameter
            new ReadFiveDayForecastTask(zipcodeString, weatherForecastListener, locationTextView.getContext()).execute();
        } // конец описания метода onLocationLoaded
    } // конец описания класса WeatherLocationLoadedListener

    // получает информацию о погоде от AsyncTask
    FiveDayForecastLoadedListener weatherForecastListener = new FiveDayForecastLoadedListener()
            {
                // если завершена фоновая задача по поиску
// сведений о местоположении
                @Override
                public void onForecastLoaded(DailyForecast[] forecasts)
                {
// отображение пятидневных прогнозов
                    for (int i = 0; i < NUMBER_DAILY_FORECASTS; i++)
                    {
// отображение информации о прогнозе
                        loadForecastIntoView(dailyForecastViews[i], forecasts[i]);
                    } // конец цикла for
                } // конец описания метода onForecastLoaded
            }; // конец описания FiveDayForecastLoadedListener

    // отображение данной информации прогноза в данном View
    private void loadForecastIntoView(View view, DailyForecast dailyForecast)
    {
// если Fragment отсоединен во время выполнения фонового процесса
        if (!FiveDayForecastFragment.this.isAdded())
        {
            return; // оставить метод
        } // конец блока if
// если отсутствуют возвращенные данные
        else if (dailyForecast == null || dailyForecast.getIconBitmap() == null)
        {
// отображение сообщения об ошибке
            Toast errorToast = Toast.makeText(view.getContext(), view.getContext().getResources().getString(R.string.null_data_toast), Toast.LENGTH_LONG);
            errorToast.setGravity(Gravity.CENTER, 0, 0);
            errorToast.show(); // отобразить Toast
            return; // выход перед обновлением прогноза
        } // конец блока else if

// получение всех дочерних View
        ImageView forecastImageView = (ImageView) view.findViewById(R.id.daily_forecast_bitmap);
        TextView dayOfWeekTextView = (TextView) view.findViewById(R.id.day_of_week);
        TextView descriptionTextView = (TextView) view.findViewById(R.id.daily_forecast_description);
        TextView highTemperatureTextView = (TextView) view.findViewById(R.id.high_temperature);
        TextView lowTemperatureTextView = (TextView) view.findViewById(R.id.low_temperature);

// отображение сведений о прогнозе погоды в выбранных View
        forecastImageView.setImageBitmap(dailyForecast.getIconBitmap());
        dayOfWeekTextView.setText(dailyForecast.getDay());
        descriptionTextView.setText(dailyForecast.getDescription());
        highTemperatureTextView.setText(dailyForecast.getHighTemperature());
        lowTemperatureTextView.setText(dailyForecast.getLowTemperature());
    } // конец определения метода loadForecastIntoView
} // конец определения класса FiveDayForecastFragment
