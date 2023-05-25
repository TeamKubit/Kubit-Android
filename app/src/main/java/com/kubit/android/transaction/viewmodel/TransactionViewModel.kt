package com.kubit.android.transaction.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kubit.android.base.BaseViewModel
import com.kubit.android.common.util.DLog
import com.kubit.android.model.data.coin.CoinSnapshotData
import com.kubit.android.model.data.coin.KubitCoinInfoData
import com.kubit.android.model.data.route.TransactionTabRouter
import com.kubit.android.model.repository.TransactionRepository
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val transactionRepository: TransactionRepository
) : BaseViewModel() {

    private lateinit var selectedCoinData: KubitCoinInfoData

    private val _tabRouter: MutableLiveData<TransactionTabRouter> =
        MutableLiveData(TransactionTabRouter.ORDER_BOOK)
    val tabRouter: LiveData<TransactionTabRouter> get() = _tabRouter

    private val _coinSnapshotData: MutableLiveData<CoinSnapshotData> = MutableLiveData()
    val coinSnapshotData: LiveData<CoinSnapshotData> get() = _coinSnapshotData

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
        viewModelScope.launch {

        }
    }

    fun stopCoinOrderBook() {

    }

    companion object {
        private const val TAG: String = "TransactionViewModel"
    }

}