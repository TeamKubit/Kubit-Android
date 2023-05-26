package com.kubit.android.chart.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.kubit.android.R
import com.kubit.android.base.BaseFragment
import com.kubit.android.databinding.FragmentChartBinding
import com.kubit.android.model.data.chart.ChartUnit
import com.kubit.android.order.view.OrderBookFragment
import com.kubit.android.transaction.viewmodel.TransactionViewModel

class ChartFragment : BaseFragment() {

    private val model: TransactionViewModel by activityViewModels()
    private var _binding: FragmentChartBinding? = null
    private val binding: FragmentChartBinding get() = _binding!!

    private val textColor: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.text)
    }
    private val secondaryColor: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.secondary)
    }

    // region Fragment LifeCycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false)

        setObserver()
        init()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // endregion Fragment LifeCycle

    private fun setObserver() {
        model.chartUnit.observe(viewLifecycleOwner, Observer { unit ->
            if (unit != null) {
                setUnitBtn(unit)
                when (unit) {
                    ChartUnit.MINUTE_1 -> {
                        binding.tvChartUnitMinute.text = getString(R.string.chart_unitMinute_1)
                    }

                    ChartUnit.MINUTE_3 -> {
                        binding.tvChartUnitMinute.text = getString(R.string.chart_unitMinute_3)
                    }

                    ChartUnit.MINUTE_5 -> {
                        binding.tvChartUnitMinute.text = getString(R.string.chart_unitMinute_5)
                    }

                    ChartUnit.MINUTE_10 -> {
                        binding.tvChartUnitMinute.text = getString(R.string.chart_unitMinute_10)
                    }

                    ChartUnit.MINUTE_15 -> {
                        binding.tvChartUnitMinute.text = getString(R.string.chart_unitMinute_15)
                    }

                    ChartUnit.MINUTE_30 -> {
                        binding.tvChartUnitMinute.text = getString(R.string.chart_unitMinute_30)
                    }

                    ChartUnit.MINUTE_60 -> {
                        binding.tvChartUnitMinute.text = getString(R.string.chart_unitMinute_60)
                    }

                    ChartUnit.MINUTE_240 -> {
                        binding.tvChartUnitMinute.text = getString(R.string.chart_unitMinute_240)
                    }

                    ChartUnit.DAY -> {

                    }

                    ChartUnit.WEEK -> {

                    }

                    ChartUnit.MONTH -> {

                    }

                    else -> {

                    }
                }
            }
        })
    }

    private fun init() {
        binding.apply {
            tvChartUnitMinute.setOnClickListener {
                model.chartUnit.value?.let { unit ->
                    when (unit) {
                        ChartUnit.MINUTE_1,
                        ChartUnit.MINUTE_3,
                        ChartUnit.MINUTE_5,
                        ChartUnit.MINUTE_10,
                        ChartUnit.MINUTE_15,
                        ChartUnit.MINUTE_30,
                        ChartUnit.MINUTE_60,
                        ChartUnit.MINUTE_240 -> {
                            // TODO: 분 단위 선택하는 PopupWindow 출력
                        }

                        else -> {
                            model.setChartUnitToMinute()
                        }
                    }
                }
            }
            tvChartUnitDay.setOnClickListener {
                model.setChartUnitToDay()
            }
            tvChartUnitWeek.setOnClickListener {
                model.setChartUnitToWeek()
            }
            tvChartUnitMonth.setOnClickListener {
                model.setChartUnitToMonth()
            }
        }
    }

    private fun setUnitBtn(pChartUnit: ChartUnit) {
        when (pChartUnit) {
            ChartUnit.MINUTE_1,
            ChartUnit.MINUTE_3,
            ChartUnit.MINUTE_5,
            ChartUnit.MINUTE_10,
            ChartUnit.MINUTE_15,
            ChartUnit.MINUTE_30,
            ChartUnit.MINUTE_60,
            ChartUnit.MINUTE_240 -> {
                binding.apply {
                    tvChartUnitMinute.setBackgroundResource(R.drawable.back_border_secondary)
                    tvChartUnitDay.setBackgroundResource(R.drawable.back_border)
                    tvChartUnitWeek.setBackgroundResource(R.drawable.back_border)
                    tvChartUnitMonth.setBackgroundResource(R.drawable.back_border)

                    tvChartUnitMinute.setTextColor(secondaryColor)
                    tvChartUnitDay.setTextColor(textColor)
                    tvChartUnitWeek.setTextColor(textColor)
                    tvChartUnitMonth.setTextColor(textColor)
                }
            }

            ChartUnit.DAY -> {
                binding.apply {
                    tvChartUnitMinute.setBackgroundResource(R.drawable.back_border)
                    tvChartUnitDay.setBackgroundResource(R.drawable.back_border_secondary)
                    tvChartUnitWeek.setBackgroundResource(R.drawable.back_border)
                    tvChartUnitMonth.setBackgroundResource(R.drawable.back_border)

                    tvChartUnitMinute.setTextColor(textColor)
                    tvChartUnitDay.setTextColor(secondaryColor)
                    tvChartUnitWeek.setTextColor(textColor)
                    tvChartUnitMonth.setTextColor(textColor)
                }
            }

            ChartUnit.WEEK -> {
                binding.apply {
                    tvChartUnitMinute.setBackgroundResource(R.drawable.back_border)
                    tvChartUnitDay.setBackgroundResource(R.drawable.back_border)
                    tvChartUnitWeek.setBackgroundResource(R.drawable.back_border_secondary)
                    tvChartUnitMonth.setBackgroundResource(R.drawable.back_border)

                    tvChartUnitMinute.setTextColor(textColor)
                    tvChartUnitDay.setTextColor(textColor)
                    tvChartUnitWeek.setTextColor(secondaryColor)
                    tvChartUnitMonth.setTextColor(textColor)
                }
            }

            ChartUnit.MONTH -> {
                binding.apply {
                    tvChartUnitMinute.setBackgroundResource(R.drawable.back_border)
                    tvChartUnitDay.setBackgroundResource(R.drawable.back_border)
                    tvChartUnitWeek.setBackgroundResource(R.drawable.back_border)
                    tvChartUnitMonth.setBackgroundResource(R.drawable.back_border_secondary)

                    tvChartUnitMinute.setTextColor(textColor)
                    tvChartUnitDay.setText(textColor)
                    tvChartUnitWeek.setText(textColor)
                    tvChartUnitMonth.setText(secondaryColor)
                }
            }
        }
    }

    companion object {
        const val TAG: String = "ChartFragment"

        private var instance: ChartFragment? = null

        @JvmStatic
        fun getInstance(): ChartFragment {
            if (instance == null) {
                instance = ChartFragment()
            }

            return instance!!
        }

        @JvmStatic
        fun clearInstance() {
            instance = null
        }
    }
}