
// ForecastFragment.java
// Абстрактный класс, определяющий возможности класса Fragment
// по поддержке почтового индекса.
package com.example.alex_k.weatherviewer;

//package com.deitel.weatherviewer;

import android.app.Fragment;

public abstract class ForecastFragment extends Fragment
{
    public abstract String getZipcode();
} // конец определения класса ForecastFragment
