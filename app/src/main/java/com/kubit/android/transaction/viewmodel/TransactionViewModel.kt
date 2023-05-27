package com.kubit.android.transaction.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kubit.android.base.BaseViewModel
import com.kubit.android.common.util.DLog
import com.kubit.android.model.data.chart.ChartDataWrapper
import com.kubit.android.model.data.chart.ChartUnit
import com.kubit.android.model.data.coin.CoinSnapshotData
import com.kubit.android.model.data.coin.KubitCoinInfoData
import com.kubit.android.model.data.orderbook.OrderBookData
import com.kubit.android.model.data.route.TransactionTabRouter
import com.kubit.android.model.data.transaction.TransactionMethod
import com.kubit.android.model.data.transaction.TransactionType
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

    /**
     * 매수 매도 여부
     */
    private val _transactionType: MutableLiveData<TransactionType> =
        MutableLiveData(TransactionType.BID)
    val transactionType: LiveData<TransactionType> get() = _transactionType

    /**
     * 지정가 거래인지 시장가 거래인지
     */
    private val _transactionMethod: MutableLiveData<TransactionMethod> =
        MutableLiveData(TransactionMethod.DESIGNATED_PRICE)
    val transactionMethod: LiveData<TransactionMethod> get() = _transactionMethod

    /**
     * 차트 단위
     */
    private val _chartUnit: MutableLiveData<ChartUnit> = MutableLiveData(unitMinute)
    val chartUnit: LiveData<ChartUnit> get() = _chartUnit

    /**
     * 분 단위
     */
    private var _unitMinute: ChartUnit = ChartUnit.MINUTE_3
    private val unitMinute: ChartUnit get() = _unitMinute

    /**
     * 차트 데이터 Wrapper
     */
    private val _chartDataWrapper: MutableLiveData<ChartDataWrapper?> = MutableLiveData(null)
    val chartDataWrapper: LiveData<ChartDataWrapper?> get() = _chartDataWrapper

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

    fun setTransactionType(pTransactionType: TransactionType) {
        if (transactionType.value != pTransactionType) {
            _transactionType.value = pTransactionType
        }
    }

    fun setTransactionMethod(pTransactionMethod: TransactionMethod) {
        if (transactionMethod.value != pTransactionMethod) {
            _transactionMethod.value = pTransactionMethod
        }
    }

    fun setChartUnitToMinute() {
        if (chartUnit.value != unitMinute) {
            _chartUnit.value = unitMinute
            transactionRepository.changeCoinChartUnit(unitMinute)
        }
    }

    fun setChartUnitToMinute(pUnitMinute: ChartUnit) {
        when (pUnitMinute) {
            ChartUnit.MINUTE_1,
            ChartUnit.MINUTE_3,
            ChartUnit.MINUTE_5,
            ChartUnit.MINUTE_10,
            ChartUnit.MINUTE_15,
            ChartUnit.MINUTE_30,
            ChartUnit.MINUTE_60,
            ChartUnit.MINUTE_240 -> {
                if (unitMinute != pUnitMinute) {
                    _unitMinute = pUnitMinute
                    _chartUnit.value = pUnitMinute
                    transactionRepository.changeCoinChartUnit(pUnitMinute)
                }
            }

            else -> {
                DLog.e(TAG, "$pUnitMinute is not Minute Unit!")
            }
        }
    }

    fun setChartUnitToDay() {
        if (chartUnit.value != ChartUnit.DAY) {
            _chartUnit.value = ChartUnit.DAY
            transactionRepository.changeCoinChartUnit(ChartUnit.DAY)
        }
    }

    fun setChartUnitToWeek() {
        if (chartUnit.value != ChartUnit.WEEK) {
            _chartUnit.value = ChartUnit.WEEK
            transactionRepository.changeCoinChartUnit(ChartUnit.WEEK)
        }
    }

    fun setChartUnitToMonth() {
        if (chartUnit.value != ChartUnit.MONTH) {
            _chartUnit.value = ChartUnit.MONTH
            transactionRepository.changeCoinChartUnit(ChartUnit.MONTH)
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

    fun requestCoinChart() {
        DLog.d(TAG, "requestCoinChart() is called!")
        viewModelScope.launch {
            transactionRepository.makeCoinChartThread(
                pSelectedCoinData = selectedCoinData,
                pChartUnit = chartUnit.value ?: ChartUnit.MINUTE_3,
                onSuccessListener = { chartDataWrapper ->
                    DLog.d(TAG, "chartDataWrapper=$chartDataWrapper")
                    _chartDataWrapper.postValue(chartDataWrapper)
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

    fun stopCoinChart() {
        DLog.d(TAG, "stopCoinChart() is called")
        viewModelScope.launch {
            transactionRepository.stopCoinChartThread()
        }
    }

    companion object {
        private const val TAG: String = "TransactionViewModel"
    }

}