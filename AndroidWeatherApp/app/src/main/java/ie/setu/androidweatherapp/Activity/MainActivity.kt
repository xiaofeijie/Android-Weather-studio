package ie.setu.androidweatherapp.Activity

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ie.setu.androidweatherapp.Adapter.HourlyAdapter
import ie.setu.androidweatherapp.Adapter.OtherCityAdapter
import ie.setu.androidweatherapp.Model.CityModel
import ie.setu.androidweatherapp.Model.HourlyModel
import ie.setu.androidweatherapp.R
import ie.setu.androidweatherapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chipNavigator.setItemSelected(R.id.home, true)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        initRecyclerviewHourly()
        initRecyclerOtherCity()

    }

    private fun initRecyclerOtherCity() {
        val items:ArrayList<CityModel> =ArrayList()

        items.add(CityModel("Paris",28,"cloudy",12,20,30))
        items.add(CityModel("Berlin",29,"sunny",5,22,12))
        items.add(CityModel("Rome",30,"windy",30,25,50))
        items.add(CityModel("London", 31, "cloudy_2",20,20,35))
        items.add(CityModel("NewYork", 10, "snowy",8,5,7))

        binding.view2.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.view2.adapter=OtherCityAdapter(items)
    }

    private fun initRecyclerviewHourly() {
        val items:ArrayList<HourlyModel> =ArrayList()

        items.add(HourlyModel("9 pm",28,"cloudy"))
        items.add(HourlyModel("10 pm",29,"sunny"))
        items.add(HourlyModel("11 pm",30,"windy"))
        items.add(HourlyModel("12 pm",31,"cloudy_2"))
        items.add(HourlyModel("1 am",10,"snowy"))

        binding.view1.setLayoutManager(
            LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false))
        binding.view1.adapter=HourlyAdapter(items)
    }


}