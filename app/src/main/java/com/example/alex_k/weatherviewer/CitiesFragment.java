// CitiesFragment.java
// Объект Fragment, отображающий список избранных городов.

package com.example.alex_k.weatherviewer;

//package com.deitel.weatherviewer;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CitiesFragment extends ListFragment
{
    private int currentCityIndex; // выделенная позиция текущего списка

    // ключ, использованный для сохранения выбранного списка в Bundle
    private static final String CURRENT_CITY_KEY = "current_city";

    public ArrayList<String> citiesArrayList; // перечень названий городов
    private CitiesListChangeListener citiesListChangeListener;
    private ArrayAdapter<String> citiesArrayAdapter;

    // интерфейс, описывающий слушателя изменений для выбранного
// города и предпочтительного города
    public interface CitiesListChangeListener
    {
        // выбранный город изменен
        public void onSelectedCityChanged(String cityNameString);

        // предпочтительный город изменен
        public void onPreferredCityChanged(String cityNameString);
    } // конец описания интерфейса CitiesListChangeListener

    // вызывается при создании родительского класса Activity
    @Override
    public void onActivityCreated(Bundle savedInstanceStateBundle)
    {
        super.onActivityCreated(savedInstanceStateBundle);

// данный объект Bundle включает информацию о состоянии
        if (savedInstanceStateBundle != null)
        {
// получает последний выбранный город из объекта Bundle
            currentCityIndex = savedInstanceStateBundle.getInt(CURRENT_CITY_KEY);
        } // конец блока if

// создание списка ArrayList, используемого для хранения
// названий городов
        citiesArrayList = new ArrayList<String>();

// настройка адаптера ListView класса Fragment
        setListAdapter(new CitiesArrayAdapter<String>(getActivity(), R.layout.city_list_item, citiesArrayList));

        ListView thisListView = getListView(); // получение ListView
// из Fragment
        citiesArrayAdapter = (ArrayAdapter<String>)getListAdapter();

// разрешить выбрать один город в данный момент времени
        thisListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        thisListView.setBackgroundColor(Color.WHITE); // выбор фонового цвета
        thisListView.setOnItemLongClickListener(
                citiesOnItemLongClickListener);
    } // конец определения метода onActivityCreated

    // настройка CitiesListChangeListener
    public void setCitiesListChangeListener(CitiesListChangeListener listener)
    {
        citiesListChangeListener = listener;
    } // конец определения метода setCitiesChangeListener

    // заказной адаптер ArrayAdapter для списка ListView из CitiesFragment
    private class CitiesArrayAdapter<T> extends ArrayAdapter<String>
    {
        private Context context; // объект Context для Activity
// объекта Fragment

        // общедоступный конструктор для CitiesArrayAdapter
        public CitiesArrayAdapter(Context context, int textViewResourceId, List<String> objects)
        {
            super(context, textViewResourceId, objects);
            this.context = context;
        } // конец описания конструктора CitiesArrayAdapter

        // получение элемента ListView для данной позиции
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
// получение компонента TextView, генерируемого методом
// getView из ArrayAdapter
            TextView listItemTextView = (TextView) super.getView(position, convertView, parent);

// если этот элемент является предпочтительным городом
            if (isPreferredCity(listItemTextView.getText().toString()))
            {
// отображение «звездочки» справа от первого элемента
// списка TextView
                listItemTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.btn_star_big_on, 0);
            } // конец блока if
            else
            {
// очистка любых составных графических элементов
// для элемента списка TextView
                listItemTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            } // конец блока else
            return listItemTextView;
        } // конец описания метода

        // является ли данный город предпочтительным?
        private boolean isPreferredCity(String cityString)
        {
// получение SharedPreferences для приложения
            SharedPreferences preferredCitySharedPreferences = context.getSharedPreferences(WeatherViewerActivity.SHARED_PREFERENCES_NAME,
                            Context.MODE_PRIVATE);

// возвращает true, если данное имя соответствует
// названию предпочтительного города
            return cityString.equals(preferredCitySharedPreferences.getString(WeatherViewerActivity.PREFERRED_CITY_NAME_KEY, null));
        } // конец определения метода isPreferredCity
    } // конец описания класса CitiesArrayAdapter

    // обработка событий, генерируемых в результате длительного
// нажатия элемента ListView
    private OnItemLongClickListener citiesOnItemLongClickListener = new OnItemLongClickListener()
            {
                // вызывается после длительного нажатия элемента ListView
                @Override
                public boolean onItemLongClick(AdapterView<?> listView, View view, int arg2, long arg3)
                {
// получение контекста для данного View
                    final Context context = view.getContext();

// получаем ресурсы для загрузки строк из файла в формате xml
                    final Resources resources = context.getResources();

// получение названия выбранного города
                    final String cityNameString = ((TextView) view).getText().toString();

// создание нового диалогового окна AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

// настройка сообщения AlertDialog
                    builder.setMessage(resources.getString(R.string.city_dialog_message_prefix) + cityNameString + resources.getString(R.string.city_dialog_message_postfix));

// настройка кнопки "+" в окне AlertDialog
                    builder.setPositiveButton(resources.getString(R.string.city_dialog_preferred),
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    citiesListChangeListener.onPreferredCityChanged(cityNameString);
                                    citiesArrayAdapter.notifyDataSetChanged();
                                } // конец описания метода onClick
                            });// конец описания DialogInterface.OnClickListener
// настройка нейтральной кнопки в AlertDialog
                    builder.setNeutralButton(resources.getString(R.string.city_dialog_delete),
                            new DialogInterface.OnClickListener()
                            {
                                // вызывается после щелчка на кнопке "Delete"
                                public void onClick(DialogInterface dialog, int id)
                                {
// если последний город в списке
                                    if (citiesArrayAdapter.getCount() == 1)
                                    {
// сообщение пользователю о невозможности
// удаления последнего города в списке
                                        Toast lastCityToast = Toast.makeText(context, resources.getString(R.string.last_city_warning), Toast.LENGTH_LONG);
                                        lastCityToast.setGravity(Gravity.CENTER, 0, 0);
                                        lastCityToast.show(); // сообщение Toast
                                        return; // конец описания метода
                                    } // конец блока if

// удаление города из списка
                                    citiesArrayAdapter.remove(cityNameString);

// получение общих настроек приложения
                                    SharedPreferences sharedPreferences = context.getSharedPreferences(WeatherViewerActivity.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

// удаление настроек для удаленного города
// из SharedPreferences
                                    Editor preferencesEditor = sharedPreferences.edit();
                                    preferencesEditor.remove(cityNameString);
                                    preferencesEditor.apply();

// получение текущего предпочтительного города
                                    String preferredCityString = sharedPreferences.getString(WeatherViewerActivity.PREFERRED_CITY_NAME_KEY, resources.getString(R.string.default_zipcode));

// если удален предпочтительный город
                                    if (cityNameString.equals(preferredCityString))
                                    {
// установка нового предпочтительного города
                                        citiesListChangeListener.onPreferredCityChanged(citiesArrayList.get(0));
                                    } // конец блока if
                                    else if (cityNameString.equals(citiesArrayList.get(currentCityIndex)))
                                    {
// загрузка прогноза для предпочтительного города
                                        citiesListChangeListener.onSelectedCityChanged(preferredCityString);
                                    } // конец блока else if
                                } // конец описания метода onClick
                            });// конец описания OnClickListener
// настройка кнопки отмены в окне AlertDialog
                    builder.setNegativeButton(resources.getString(R.string.city_dialog_cancel),
                            new DialogInterface.OnClickListener()
                            {
                                // вызывается после щелчка на кнопке "No"
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    dialog.cancel(); // скрытие окна AlertDialog
                                } // конец описания onClick
                            });// конец описания OnClickListener

                    builder.create().show(); // display the AlertDialog
                    return true;
                } // конец описания citiesOnItemLongClickListener
            }; // конец описаниия OnItemLongClickListener

    // сохранение состояния класса Fragment
    @Override
    public void onSaveInstanceState(Bundle outStateBundle)
    {
        super.onSaveInstanceState(outStateBundle);

// сохранение текущего выбранного города в Bundle
        outStateBundle.putInt(CURRENT_CITY_KEY, currentCityIndex);
    } // конец описания onSaveInstanceState

    // добавление нового города в список
    public void addCity(String cityNameString, boolean select)
    {
        citiesArrayAdapter.add(cityNameString);
        citiesArrayAdapter.sort(String.CASE_INSENSITIVE_ORDER);

        if (select) // если нужно выбрать новый город
        {
// информирование CitiesListChangeListener
            citiesListChangeListener.onSelectedCityChanged(cityNameString);
        } // конец блока if
    } // конец описания метода addCity

    // обработка щелчка на элементе ListView
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
// сообщить Activity об обновлении ForecastFragment
        citiesListChangeListener.onSelectedCityChanged(((TextView)v).getText().toString());
        currentCityIndex = position; // сохранение текущей
// выбранной позиции
    } // конец описания onListItemClick
} // конец описания класса CitiesFragment
