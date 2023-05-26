package com.kubit.android.exchange.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.kubit.android.R
import com.kubit.android.base.BaseFragment
import com.kubit.android.coinlist.view.CoinListFragment
import com.kubit.android.databinding.FragmentExchangeBinding
import com.kubit.android.main.viewmodel.MainViewModel

class ExchangeFragment : BaseFragment() {

    private val model: MainViewModel by activityViewModels()
    private var _binding: FragmentExchangeBinding? = null
    private val binding: FragmentExchangeBinding get() = _binding!!

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
        _binding = FragmentExchangeBinding.inflate(inflater, container, false)

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
        const val TAG: String = "ExchangeFragment"

        private var instance: ExchangeFragment? = null

        @JvmStatic
        fun getInstance(): ExchangeFragment {
            if (instance == null) {
                instance = ExchangeFragment()
            }

            return instance!!
        }

        @JvmStatic
        fun clearInstance() {
            instance = null
        }
    }
}