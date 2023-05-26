package com.kubit.android.investment.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayout
import com.kubit.android.R
import com.kubit.android.base.BaseFragment
import com.kubit.android.coinlist.view.CoinListFragment
import com.kubit.android.databinding.FragmentInvestmentBinding
import com.kubit.android.main.viewmodel.MainViewModel

class InvestmentFragment : BaseFragment() {

    private val model: MainViewModel by activityViewModels()
    private var _binding: FragmentInvestmentBinding? = null
    private val binding: FragmentInvestmentBinding get() = _binding!!

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
        _binding = FragmentInvestmentBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // endregion Fragment LifeCycle

    private fun init() {
        binding.apply {
            tlInvestment.apply {
                addTab(
                    newTab().setId(R.id.investment_tab_asset)
                        .setText(getString(R.string.investment_tab_asset))
                )
                addTab(
                    newTab().setId(R.id.investment_tab_record)
                        .setText(getString(R.string.investment_tab_record))
                )
                addTab(
                    newTab().setId(R.id.investment_tab_not_yet)
                        .setText(getString(R.string.investment_tab_not_yet))
                )

                addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        when (tab?.id) {
                            R.id.investment_tab_asset -> {
                                tvInvestmentDeleteSelectOrder.visibility = View.GONE
                            }

                            R.id.investment_tab_record -> {
                                tvInvestmentDeleteSelectOrder.visibility = View.GONE
                            }

                            R.id.investment_tab_not_yet -> {
                                tvInvestmentDeleteSelectOrder.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {
                    }
                })
            }
        }
    }

    companion object {
        const val TAG: String = "InvestmentFragment"

        private var instance: InvestmentFragment? = null

        @JvmStatic
        fun getInstance(): InvestmentFragment {
            if (instance == null) {
                instance = InvestmentFragment()
            }

            return instance!!
        }

        @JvmStatic
        fun clearInstance() {
            instance = null
        }
    }
}