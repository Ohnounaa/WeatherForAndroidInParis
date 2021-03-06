package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import 	android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ActionBarContainer
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.DataModels.DailyWeatherInfo
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import java.util.*

class MultiDayWeatherForecastFragment: Fragment(),
    AdapterView.OnItemSelectedListener, View.OnTouchListener {

    lateinit var fragmentLayout: View
    var touched = false
    interface Callbacks{
        fun onDaySelected(weatherDt:Int?)
    }

    private var callbacks: Callbacks? = null

    lateinit var recyclerView: RecyclerView

    private val weatherDataViewModel:WeatherDataViewModel by lazy {
        ViewModelProvider(requireActivity()).get(WeatherDataViewModel::class.java)
    }

    override fun onAttach(context:Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        
         fragmentLayout = inflater.inflate(R.layout.multi_day_weather, container, false)

        recyclerView = fragmentLayout.findViewById(R.id.daily_weather_data_collection)
        recyclerView.layoutManager = LinearLayoutManager(context)
       // setUnitSelectorMenu()

        return fragmentLayout
    }

    override fun onViewCreated(view:View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherDataViewModel.weatherLiveData.observe(
            viewLifecycleOwner,
            { weatherInfo ->
                recyclerView.adapter = DailyWeatherRecyclerViewAdapter(weatherInfo, context, callbacks)
                setUnitSelectorMenu()
            },
        )
    }


    override fun onTouch(v: View, e: MotionEvent):Boolean {
        touched = true
        return false
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if ((recyclerView.adapter != null
                && recyclerView.adapter is DailyWeatherRecyclerViewAdapter
                && p1 != null) && touched
        ) {
            touched = false
            if ((p1 as TextView).text == "Fahrenheit") {
                weatherDataViewModel.weatherLiveData.value!!.forEach { it ->
                    it?.temp?.day = convertCelsiusToFahrenheit(it.temp.day)
                    it?.temp?.night = convertCelsiusToFahrenheit(it.temp.night)
                    it?.temp?.max = convertCelsiusToFahrenheit(it.temp.max)
                    it?.temp?.min = convertCelsiusToFahrenheit(it.temp.min)
                    it?.feels_like.day = convertCelsiusToFahrenheit(it.feels_like.day)
                }
            } else {
                weatherDataViewModel.weatherLiveData.value!!.forEach {
                    it?.temp?.day = convertFahrenheitToCelsius(it.temp.day)
                    it?.temp?.night = convertFahrenheitToCelsius(it.temp.night)
                    it?.temp?.max = convertFahrenheitToCelsius(it.temp.max)
                    it?.temp?.min = convertFahrenheitToCelsius(it.temp.min)
                    it?.feels_like.day = convertFahrenheitToCelsius(it.feels_like.day)
                }
            }
            recyclerView.adapter?.notifyItemRangeChanged(
                0,
                recyclerView.adapter?.itemCount?.minus(1) ?: 0
            )
        }
    }

    private fun setUnitSelectorMenu() {
        val unitsSelector: Spinner = fragmentLayout.findViewById(R.id.unit_selector)
        ArrayAdapter.createFromResource(
            getActivity()!!.getApplicationContext(),
            R.array.temperature_units_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            unitsSelector.adapter = adapter
        }
        unitsSelector.setOnTouchListener(this)
        unitsSelector.onItemSelectedListener = this
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    companion object{
        fun newInstance(): MultiDayWeatherForecastFragment {
            return MultiDayWeatherForecastFragment()
        }
    }

    private fun convertCelsiusToFahrenheit(celsiusTemp: Double): Double {
        return ("%.2f".format((1.8 * celsiusTemp) + 32)).toDouble();
    }

    private fun convertFahrenheitToCelsius(fahrenheitTemp: Double): Double {
        return ("%.2f".format(((5).toDouble() / 9) * (fahrenheitTemp - 32))).toDouble();
    }
}