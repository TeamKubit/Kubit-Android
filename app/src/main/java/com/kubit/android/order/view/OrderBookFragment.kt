package com.kubit.android.order.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.kubit.android.R
import com.kubit.android.base.BaseFragment
import com.kubit.android.databinding.FragmentOrderBookBinding
import com.kubit.android.transaction.viewmodel.TransactionViewModel

class OrderBookFragment : BaseFragment() {

    private val model: TransactionViewModel by activityViewModels()
    private var _binding: FragmentOrderBookBinding? = null
    private val binding: FragmentOrderBookBinding get() = _binding!!

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
        _binding = FragmentOrderBookBinding.inflate(inflater, container, false)

        setObserver()
        init()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        model.requestCoinOrderBook()
    }

    override fun onStop() {
        super.onStop()
        model.stopCoinOrderBook()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // endregion Fragment LifeCycle

    private fun setObserver() {

    }

    private fun init() {

    }

    companion object {
        const val TAG: String = "OrderBookFragment"

        private var instance: OrderBookFragment? = null

        @JvmStatic
        fun getInstance(): OrderBookFragment {
            if (instance == null) {
                instance = OrderBookFragment()
            }

            return instance!!
        }
    }
}