package com.juanantbuit.weatherproject.framework.ui.dailyDetails

import android.app.Dialog
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.juanantbuit.weatherproject.R
import com.juanantbuit.weatherproject.databinding.FragmentDailyDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DailyDetailsFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentDailyDetailsBinding

    private lateinit var lineList: ArrayList<Entry>
    private lateinit var lineDataSet: LineDataSet
    private lateinit var lineData: LineData
    private lateinit var xAxis: XAxis

    companion object {
        const val TAG = "dailyDetailsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDailyDetailsBinding.inflate(inflater, container, false)

        val imageBitmap = BitmapFactory.decodeStream(context?.openFileInput("dayImage"))

        val temperatures = arguments?.getDoubleArray("temperatures")
        binding.dayName.text = arguments?.getString("dayName")
        binding.averageTemperature.text =
            getString(R.string.temperature, arguments?.getInt("averageTemp"))
        binding.lowestTemp.text = getString(R.string.temperature, arguments?.getInt("lowestTemp"))
        binding.highestTemp.text = getString(R.string.temperature, arguments?.getInt("highestTemp"))
        binding.dayImage.setImageBitmap(imageBitmap)


        setLineChartView()
        setLineListData(temperatures)
        setLineDataSet()

        lineData = LineData(lineDataSet)
        binding.lineChart.data = lineData

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        var styleResId: Int = R.style.ModalBottomSheetDialog

        // It is necessary to change the backgroundColor of the dialog via styles to preserve
        // the rounded corners.
        when (arguments?.getInt("dayNumber")) {
            0 -> styleResId = R.style.ModalBottomSheetDialog1
            1 -> styleResId = R.style.ModalBottomSheetDialog2
            2 -> styleResId = R.style.ModalBottomSheetDialog3
            3 -> styleResId = R.style.ModalBottomSheetDialog4
        }
        setStyle(DialogFragment.STYLE_NORMAL, styleResId)

        return BottomSheetDialog(requireContext(), styleResId)
    }

    private fun setLineChartView() {
        binding.lineChart.setScaleEnabled(false)
        binding.lineChart.legend.isEnabled = false
        binding.lineChart.description.isEnabled = false

        binding.lineChart.axisLeft.setDrawGridLines(false)
        binding.lineChart.axisLeft.setDrawAxisLine(false)
        binding.lineChart.axisLeft.setDrawLabels(false)

        binding.lineChart.axisRight.setDrawGridLines(false)
        binding.lineChart.axisRight.setDrawAxisLine(false)
        binding.lineChart.axisRight.setDrawLabels(false)

        xAxis = binding.lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
    }

    private fun setLineListData(temperatures: DoubleArray?) {
        lineList = ArrayList()
        for (i in 0 until temperatures!!.size) {
            lineList.add(Entry(i.toFloat() + 1, temperatures[i].toFloat()))
        }
    }

    private fun setLineDataSet() {
        lineDataSet = LineDataSet(lineList, "")

        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                var label = ""
                when (value) {
                    1f -> label = "00:00"
                    2f -> label = "03:00"
                    3f -> label = "06:00"
                    4f -> label = "09:00"
                    5f -> label = "12:00"
                    6f -> label = "15:00"
                    7f -> label = "18:00"
                    8f -> label = "21:00"
                }
                return label
            }
        }

        lineDataSet.color = Color.BLACK
        lineDataSet.valueTextColor = Color.BLUE
        lineDataSet.valueTextSize = 13f
        lineDataSet.setDrawCircles(true)

        lineDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() + "º"
            }
        }

        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
    }

    override fun onDestroyView() {
        binding.dayImage.setImageBitmap(null)
        binding.dayName.text = null
        binding.averageTemperature.text = null
        binding.lowestTemp.text = null
        binding.highestTemp.text = null
        binding.lineChart.data = null
        lineList.clear()

        super.onDestroyView()
    }
}
