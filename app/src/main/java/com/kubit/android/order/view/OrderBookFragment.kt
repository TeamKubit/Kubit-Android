package com.kubit.android.order.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.kubit.android.R
import com.kubit.android.base.BaseFragment
import com.kubit.android.common.dialog.EnterPriceDialog
import com.kubit.android.common.session.KubitSession
import com.kubit.android.common.util.ConvertUtil
import com.kubit.android.common.util.DLog
import com.kubit.android.databinding.FragmentOrderBookBinding
import com.kubit.android.model.data.transaction.TransactionMethod
import com.kubit.android.model.data.transaction.TransactionType
import com.kubit.android.transaction.viewmodel.TransactionViewModel

class OrderBookFragment : BaseFragment() {

    private val model: TransactionViewModel by activityViewModels()
    private var _binding: FragmentOrderBookBinding? = null
    private val binding: FragmentOrderBookBinding get() = _binding!!

    private val coinRedColor: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.coin_red)
    }
    private val coinBlueColor: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.coin_blue)
    }
    private val textColor: Int by lazy {
        ContextCompat.getColor(requireContext(), R.color.text)
    }

    // region Fragment LifeCycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        model.orderBookData.observe(viewLifecycleOwner, Observer { orderBookData ->
            if (orderBookData != null) {
                binding.cvOrderBook.update(orderBookData.unitDataList)
            }
        })

        model.transactionType.observe(viewLifecycleOwner, Observer { transactionType ->
            when (transactionType) {
                TransactionType.ASK -> {
                    setAskLayout()
                }

                TransactionType.BID -> {
                    setBidLayout()
                }

                else -> {
                    DLog.e(TAG, "Unrecognized TransactionType is $transactionType")
                }
            }
        })

        model.transactionMethod.observe(viewLifecycleOwner, Observer { transactionMethod ->
            when (transactionMethod) {
                TransactionMethod.DESIGNATED_PRICE -> {
                    setDesignatedPriceLayout()
                }

                TransactionMethod.MARKET_PRICE -> {
                    setMarketPriceLayout()
                }

                else -> {
                    DLog.e(TAG, "Unrecognized TransactionMethod is $transactionMethod")
                }
            }
        })

        model.coinTradePrice.observe(viewLifecycleOwner, Observer { tradePrice ->
            if (tradePrice != null) {
                model.setOrderUnitPrice(tradePrice)
            }
        })

        model.orderQuantity.observe(viewLifecycleOwner, Observer { quantity ->
            binding.tvOrderBookQuantity.text = ConvertUtil.coinQuantity2string(quantity)
        })

        model.orderUnitPrice.observe(viewLifecycleOwner, Observer { unitPrice ->
            val withDecimalPoint = model.coinTradePrice.value?.let { it < 100 } ?: false
            binding.tvOrderBookUnitPrice.text =
                ConvertUtil.tradePrice2string(unitPrice, pWithDecimalPoint = withDecimalPoint)
        })

        model.orderTotalPrice.observe(viewLifecycleOwner, Observer { totalPrice ->
            binding.tvOrderBookTotalPrice.text =
                ConvertUtil.price2krwString(totalPrice, pWithKRW = true)
        })
    }

    private fun init() {
        binding.apply {
            cvOrderBook.post {
                cvOrderBook.fitCenter()
            }
            tvOrderBookCanOrder.text =
                ConvertUtil.price2krwString(KubitSession.KRW, pWithKRW = true)

            tvOrderBookTabAsk.setOnClickListener {
                model.setTransactionType(TransactionType.ASK)
            }
            tvOrderBookTabBid.setOnClickListener {
                model.setTransactionType(TransactionType.BID)
            }
            ivOrderBookDesignatedPrice.setOnClickListener {
                model.setTransactionMethod(TransactionMethod.DESIGNATED_PRICE)
            }
            tvOrderBookDesignatedPrice.setOnClickListener {
                model.setTransactionMethod(TransactionMethod.DESIGNATED_PRICE)
            }
            ivOrderBookMarketPrice.setOnClickListener {
                model.setTransactionMethod(TransactionMethod.MARKET_PRICE)
            }
            tvOrderBookMarketPrice.setOnClickListener {
                model.setTransactionMethod(TransactionMethod.MARKET_PRICE)
            }

            clOrderBookQuantity.setOnClickListener {
                EnterPriceDialog(
                    priceType = EnterPriceDialog.Type.QUANTITY,
                    initUnitPrice = model.orderUnitPrice.value ?: 0.0,
                    initQuantity = model.orderQuantity.value ?: 0.0,
                    initTotalPrice = model.orderTotalPrice.value ?: 0.0
                ) { priceType, value ->
                    model.setOrderQuantity(value)
                }.show(childFragmentManager, null)
            }
            clOrderBookUnitPrice.setOnClickListener {
                EnterPriceDialog(
                    priceType = EnterPriceDialog.Type.UNIT_PRICE,
                    initUnitPrice = model.orderUnitPrice.value ?: 0.0,
                    initQuantity = model.orderQuantity.value ?: 0.0,
                    initTotalPrice = model.orderTotalPrice.value ?: 0.0
                ) { priceType, value ->
                    model.setOrderUnitPrice(value)
                }.show(childFragmentManager, null)
            }
            clOrderBookTotalPrice.setOnClickListener {
                EnterPriceDialog(
                    priceType = EnterPriceDialog.Type.TOTAL_PRICE,
                    initUnitPrice = model.orderUnitPrice.value ?: 0.0,
                    initQuantity = model.orderQuantity.value ?: 0.0,
                    initTotalPrice = model.orderTotalPrice.value ?: 0.0
                ) { priceType, value ->
                    model.setOrderTotalPrice(value)
                }.show(childFragmentManager, null)
            }

            tvOrderBookClear.setOnClickListener {

            }
            tvOrderBookConfirm.setOnClickListener {

            }
        }
    }

    /**
     * 매도 화면으로 전환
     */
    private fun setAskLayout() {
        binding.apply {
            tvOrderBookTabBid.setTextColor(textColor)
            tvOrderBookTabBid.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.border
                )
            )
            tvOrderBookTabAsk.setTextColor(coinBlueColor)
            tvOrderBookTabAsk.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.background
                )
            )

            tvOrderBookConfirm.text = getString(R.string.orderBook_ask)
            tvOrderBookConfirm.setBackgroundColor(coinBlueColor)
        }
    }

    /**
     * 매수 화면으로 전환
     */
    private fun setBidLayout() {
        binding.apply {
            tvOrderBookTabBid.setTextColor(coinRedColor)
            tvOrderBookTabBid.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.background
                )
            )
            tvOrderBookTabAsk.setTextColor(textColor)
            tvOrderBookTabAsk.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.border
                )
            )

            tvOrderBookConfirm.text = getString(R.string.orderBook_bid)
            tvOrderBookConfirm.setBackgroundColor(coinRedColor)
        }
    }

    /**
     * 지정가 거래 화면으로 전환
     */
    private fun setDesignatedPriceLayout() {
        binding.apply {
            ivOrderBookDesignatedPrice.setImageResource(R.drawable.icon_radio_selected)
            ivOrderBookMarketPrice.setImageResource(R.drawable.icon_radio_unselected)

            clOrderBookQuantity.visibility = View.VISIBLE
            clOrderBookUnitPrice.visibility = View.VISIBLE
        }
    }

    /**
     * 시장가 거래 화면으로 전환
     */
    private fun setMarketPriceLayout() {
        binding.apply {
            ivOrderBookDesignatedPrice.setImageResource(R.drawable.icon_radio_unselected)
            ivOrderBookMarketPrice.setImageResource(R.drawable.icon_radio_selected)

            clOrderBookQuantity.visibility = View.GONE
            clOrderBookUnitPrice.visibility = View.GONE
        }
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

        @JvmStatic
        fun clearInstance() {
            instance = null
        }
    }
}