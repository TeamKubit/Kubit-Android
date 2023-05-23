package com.kubit.android.investment.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.kubit.android.R
import com.kubit.android.base.BaseFragment
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
    }
}