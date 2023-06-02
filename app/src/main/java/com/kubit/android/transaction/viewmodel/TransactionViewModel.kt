package com.kubit.android.transaction.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kubit.android.base.BaseViewModel
import com.kubit.android.common.util.DLog
import com.kubit.android.model.data.chart.ChartDataWrapper
import com.kubit.android.model.data.chart.ChartMainIndicator
import com.kubit.android.model.data.chart.ChartUnit
import com.kubit.android.model.data.coin.CoinSnapshotData
import com.kubit.android.model.data.coin.KubitCoinInfoData
import com.kubit.android.model.data.orderbook.OrderBookData
import com.kubit.android.model.data.route.TransactionTabRouter
import com.kubit.android.model.data.transaction.TransactionMethod
import com.kubit.android.model.data.transaction.TransactionType
import com.kubit.android.model.repository.KubitRepository
import com.kubit.android.model.repository.TransactionRepository
import kotlinx.coroutines.launch
import kotlin.math.abs

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val kubitRepository: KubitRepository
) : BaseViewModel() {

    private lateinit var selectedCoinData: KubitCoinInfoData

    private val _tabRouter: MutableLiveData<TransactionTabRouter> = MutableLiveData()
    val tabRouter: LiveData<TransactionTabRouter> get() = _tabRouter

    // region 호가창 화면 관련 변수
    private val _coinSnapshotData: MutableLiveData<CoinSnapshotData> = MutableLiveData()
    val coinSnapshotData: LiveData<CoinSnapshotData> get() = _coinSnapshotData

    /**
     * 코인 시가
     */
    private val _coinOpeningPrice: MutableLiveData<Double?> = MutableLiveData(null)
    val coinOpeningPrice: LiveData<Double?> get() = _coinOpeningPrice

    /**
     * 코인 현재가 -> 호가창 화면 초기화 목적으로 사용
     */
    private val _coinTradePrice: MutableLiveData<Double?> = MutableLiveData(null)
    val coinTradePrice: LiveData<Double?> get() = _coinTradePrice

    /**
     * 사용자가 입력한 코인의 주문 수량
     */
    private var _orderQuantity: MutableLiveData<Double> = MutableLiveData(0.0)
    val orderQuantity: LiveData<Double> get() = _orderQuantity

    /**
     * 사용자가 입력한 코인 1개당 가격
     */
    private var _orderUnitPrice: MutableLiveData<Double> = MutableLiveData(0.0)
    val orderUnitPrice: LiveData<Double> get() = _orderUnitPrice

    /**
     * 지정가 거래에서의 총액
     */
    private val _orderTotalPrice: MutableLiveData<Double> = MutableLiveData(0.0)
    val orderTotalPrice: LiveData<Double> get() = _orderTotalPrice

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
    // endregion 호가창 화면 관련 변수

    // region 차트 화면 관련 변수
    /**
     * 가격 차트 메인 지표
     */
    private val _chartMainIndicator: MutableLiveData<ChartMainIndicator> =
        MutableLiveData(ChartMainIndicator.MOVING_AVERAGE)
    val chartMainIndicator: LiveData<ChartMainIndicator> get() = _chartMainIndicator

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
    // endregion 차트 화면 관련 변수

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

    /**
     * 지정가 거래에서의 주문 수량을 설정하는 함수
     *
     * @param pOrderQuantity    주문 수량
     */
    fun setOrderQuantity(pOrderQuantity: Double) {
        DLog.d(TAG, "setOrderQuantity(), pOrderQuantity=$pOrderQuantity")
        _orderQuantity.postValue(pOrderQuantity)
        _orderTotalPrice.postValue(pOrderQuantity * (orderUnitPrice.value ?: 0.0))
    }

    /**
     * 지정가 거래에서의 코인 1개당 가격을 설정하는 함수
     *
     * @param pOrderUnitPrice   코인 1개당 가격
     */
    fun setOrderUnitPrice(pOrderUnitPrice: Double) {
        DLog.d(TAG, "setOrderUnitPrice(), pOrderUnitPrice=$pOrderUnitPrice")
        _orderUnitPrice.postValue(pOrderUnitPrice)
        _orderTotalPrice.postValue((orderQuantity.value ?: 0.0) * pOrderUnitPrice)
    }

    /**
     * 총 주문 금액을 설정하는 함수
     *
     * @param pOrderTotalPrice  총 주문 금액
     */
    fun setOrderTotalPrice(pOrderTotalPrice: Double) {
        DLog.d(TAG, "setOrderTotalPrice(), pOrderTotalPrice=$pOrderTotalPrice")
        _orderTotalPrice.postValue(pOrderTotalPrice)

        orderUnitPrice.value?.let { unitPrice ->
            if (unitPrice > 0) {
                val quantity = (pOrderTotalPrice / unitPrice).toInt().toDouble()
                _orderQuantity.postValue(quantity)
            } else {
                val newUnitPrice = coinTradePrice.value!!
                _orderUnitPrice.postValue(newUnitPrice)
                val quantity = (pOrderTotalPrice / newUnitPrice).toInt().toDouble()
                _orderQuantity.postValue(quantity)
            }
        }
    }

    fun setChartMainIndicator(pChartMainIndicator: ChartMainIndicator) {
        if (chartMainIndicator.value != pChartMainIndicator) {
            _chartMainIndicator.value = pChartMainIndicator
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
                    snapshotDataList.firstOrNull()?.let { snapshotData ->
                        _coinSnapshotData.postValue(snapshotData)

                        if (coinOpeningPrice.value == null) {
                            _coinOpeningPrice.postValue(snapshotData.openingPrice)
                        }
                        if (coinTradePrice.value == null) {
                            _coinTradePrice.postValue(snapshotData.tradePrice)
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