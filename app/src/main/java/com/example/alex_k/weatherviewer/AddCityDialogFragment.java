// AddCityDialogFragment.java
// DialogFragment, предназначенный для ввода пользователем
// почтового индекса нового города.

package com.example.alex_k.weatherviewer;

//package com.deitel.weatherviewer;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


public class AddCityDialogFragment extends DialogFragment implements OnClickListener
{
    // выслушивает результаты, полученные из AddCityDialog
    public interface DialogFinishedListener
    {
        // вызывается, если скрывается AddCityDialog
        void onDialogFinished(String zipcodeString, boolean preferred);
    } // конец описания интерфейса DialogFinishedListener

    EditText addCityEditText; // компонент EditText из DialogFragment
    CheckBox addCityCheckBox; // компонент CheckBox из DialogFragment

    // инициализация нового фрагмента DialogFragment
    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);

// обеспечение выхода пользователя с помощью клавиши back
        this.setCancelable(true);
    } // конец описания метода onCreate

    // "раздувает" разметку DialogFragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle argumentsBundle)
    {
// "раздувает" разметку, определенную в файле add_city_dialog.xml
        View rootView = inflater.inflate(R.layout.add_city_dialog, container, false);

// получает EditText
        addCityEditText = (EditText) rootView.findViewById(R.id.add_city_edit_text);

// получает CheckBox
        addCityCheckBox = (CheckBox) rootView.findViewById(R.id.add_city_checkbox);

        if (argumentsBundle != null) // если аргументы Bundle не пусты
        {
            addCityEditText.setText(argumentsBundle.getString(getResources().getString(R.string.add_city_dialog_bundle_key)));
        } // конец блока if

// настройка заголовка DialogFragment
        getDialog().setTitle(R.string.add_city_dialog_title);

// инициализация кнопки "+"
        Button okButton = (Button) rootView.findViewById(R.id.add_city_button);
        okButton.setOnClickListener(this);
        return rootView; // возврат корневого View из Fragment
    } // конец описания метода onCreateView

    // сохранение текущего состояния DialogFragment
    @Override
    public void onSaveInstanceState(Bundle argumentsBundle)
    {
// добавление текста EditText в аргументы Bundle
        argumentsBundle.putCharSequence(getResources().getString(R.string.add_city_dialog_bundle_key), addCityEditText.getText().toString());
        super.onSaveInstanceState(argumentsBundle);
    } // конец описания метода onSaveInstanceState

    // вызывается после щелчка на кнопке Add City Button
    @Override
    public void onClick(View clickedView)
    {
        if (clickedView.getId() == R.id.add_city_button)
        {
            DialogFinishedListener listener = (DialogFinishedListener) getActivity();
            listener.onDialogFinished(addCityEditText.getText().toString(), addCityCheckBox.isChecked() );
            dismiss(); // скрытие DialogFragment
        } // конец блока if
    } // конец описания метода onClick
} // конец описания класса AddCityDialogFragment
