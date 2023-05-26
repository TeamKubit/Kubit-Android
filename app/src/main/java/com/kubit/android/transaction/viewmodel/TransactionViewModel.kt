package com.kubit.android.transaction.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kubit.android.base.BaseViewModel
import com.kubit.android.common.util.DLog
import com.kubit.android.model.data.coin.CoinSnapshotData
import com.kubit.android.model.data.coin.KubitCoinInfoData
import com.kubit.android.model.data.orderbook.OrderBookData
import com.kubit.android.model.data.route.TransactionTabRouter
import com.kubit.android.model.repository.TransactionRepository
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val transactionRepository: TransactionRepository
) : BaseViewModel() {

    private lateinit var selectedCoinData: KubitCoinInfoData

    private val _tabRouter: MutableLiveData<TransactionTabRouter> = MutableLiveData()
    val tabRouter: LiveData<TransactionTabRouter> get() = _tabRouter

    private val _coinSnapshotData: MutableLiveData<CoinSnapshotData> = MutableLiveData()
    val coinSnapshotData: LiveData<CoinSnapshotData> get() = _coinSnapshotData

    /**
     * 코인 시가
     */
    private val _coinOpeningPrice: MutableLiveData<Double?> = MutableLiveData(null)
    val coinOpeningPrice: LiveData<Double?> get() = _coinOpeningPrice

    /**
     * 코인 호가 데이터
     */
    private val _orderBookData: MutableLiveData<OrderBookData?> = MutableLiveData(null)
    val orderBookData: LiveData<OrderBookData?> get() = _orderBookData

    fun initSelectedCoinData(pSelectedCoinData: KubitCoinInfoData) {
        setProgressFlag(true)
        selectedCoinData = pSelectedCoinData
        requestTickerData()
    }

    fun setTabRouter(pTabRouter: TransactionTabRouter) {
        if (tabRouter.value != pTabRouter) {
            _tabRouter.value = pTabRouter
        }
    }

    fun requestTickerData() {
        DLog.d(TAG, "requestTickerData() is called!")
        viewModelScope.launch {
            transactionRepository.makeCoinTickerThread(
                pSelectedCoinData = selectedCoinData,
                onSuccessListener = { snapshotDataList ->
                    DLog.d(TAG, "snapshotDataList=$snapshotDataList")
                    snapshotDataList.firstOrNull()?.let { snapshotData ->
                        _coinSnapshotData.postValue(snapshotData)

                        if (coinOpeningPrice.value == null) {
                            _coinOpeningPrice.postValue(snapshotData.openingPrice)
                        }
                    }
                },
                onFailListener = { failMsg ->
                    DLog.e(TAG, failMsg)
                    setApiFailMsg(failMsg)
                },
                onErrorListener = { e ->
                    DLog.e(TAG, e.message, e)
                    setExceptionData(e)
                }
            )
        }
    }

    fun stopTickerData() {
        DLog.d(TAG, "stopTickerData() is called")
        viewModelScope.launch {
            transactionRepository.stopCoinTickerThread()
        }
    }

    fun requestCoinOrderBook() {
        DLog.d(TAG, "requestCoinOrderBook() is called!")
        viewModelScope.launch {
            transactionRepository.makeCoinOrderBookThread(
                pSelectedCoinData = selectedCoinData,
                pOpeningPrice = coinOpeningPrice.value ?: 0.0,
                onSuccessListener = { orderBookData ->
                    DLog.d(TAG, "orderBookData=$orderBookData")
                    _orderBookData.postValue(orderBookData)
                },
                onFailListener = { failMsg ->
                    DLog.e(TAG, failMsg)
                    setApiFailMsg(failMsg)
                },
                onErrorListener = { e ->
                    DLog.e(TAG, e.message, e)
                    setExceptionData(e)
                }
            )
        }
    }

    fun stopCoinOrderBook() {
        DLog.d(TAG, "stopCoinOrderBook() is called")
        viewModelScope.launch {
            transactionRepository.stopCoinOrderBookThread()
        }
    }

    companion object {
        private const val TAG: String = "TransactionViewModel"
    }

}