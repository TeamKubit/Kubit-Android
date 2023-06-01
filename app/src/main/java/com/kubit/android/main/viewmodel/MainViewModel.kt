package com.kubit.android.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.PieEntry
import com.kubit.android.base.BaseViewModel
import com.kubit.android.common.session.KubitSession
import com.kubit.android.common.util.ConvertUtil
import com.kubit.android.common.util.DLog
import com.kubit.android.model.data.coin.CoinSnapshotData
import com.kubit.android.model.data.coin.KubitCoinInfoData
import com.kubit.android.model.data.investment.InvestmentAssetData
import com.kubit.android.model.data.investment.InvestmentData
import com.kubit.android.model.data.investment.InvestmentNotYetData
import com.kubit.android.model.data.investment.InvestmentRecordData
import com.kubit.android.model.data.investment.InvestmentWalletData
import com.kubit.android.model.data.market.KubitMarketCode
import com.kubit.android.model.data.market.KubitMarketData
import com.kubit.android.model.data.route.KubitTabRouter
import com.kubit.android.model.repository.UpbitRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val upbitRepository: UpbitRepository
) : BaseViewModel() {

    /**
     * 현재 선택된 탭이 어떤 탭인지
     */
    private val _tabRouter: MutableLiveData<KubitTabRouter> =
        MutableLiveData(KubitTabRouter.COIN_LIST)
    val tabRouter: LiveData<KubitTabRouter> get() = _tabRouter

    private lateinit var marketData: KubitMarketData

    private var _selectedCoinData: MutableLiveData<KubitCoinInfoData?> = MutableLiveData(null)
    val selectedCoinData: LiveData<KubitCoinInfoData?> get() = _selectedCoinData

    private var _searchQuery: String = ""
    private val searchQuery: String get() = _searchQuery

    /**
     * 코인 스냅샷 데이터 리스트
     */
    private val _coinSnapshotDataList: MutableLiveData<List<CoinSnapshotData>> =
        MutableLiveData(listOf())
    private val coinSnapshotDataList: LiveData<List<CoinSnapshotData>> = _coinSnapshotDataList

    /**
     * 검색어에 의해 필터링된 코인 스냅샷 데이터 리스트
     */
    private val _filterdCoinSnapshotDataList: MutableLiveData<List<CoinSnapshotData>> =
        MutableLiveData(listOf())
    val filteredCoinSnapshotDataList: LiveData<List<CoinSnapshotData>> get() = _filterdCoinSnapshotDataList

    /**
     * 보유자산 화면에 보여줄 데이터
     */
    private val _investmentAssetData: MutableLiveData<InvestmentData?> = MutableLiveData(null)
    val investmentAssetData: LiveData<InvestmentData?> get() = _investmentAssetData

    fun initMarketData(pMarketData: KubitMarketData) {
        marketData = pMarketData
    }

    fun setTabRouter(pTabRouter: KubitTabRouter) {
        if (tabRouter.value != pTabRouter) {
            _tabRouter.value = pTabRouter
        }
    }

    fun setSelectedCoinData(pSelectedCoinData: KubitCoinInfoData?) {
        _selectedCoinData.value = pSelectedCoinData
    }

    fun setSearchQuery(pQuery: String) {
        _searchQuery = pQuery
        coinSnapshotDataList.value?.let { coinSnapshotData ->
            setFilteredCoinSnapshotDataList(coinSnapshotData)
        }
    }

    private fun setFilteredCoinSnapshotDataList(
        pSnapshotDataList: List<CoinSnapshotData>
    ) {
        val filteredList = arrayListOf<CoinSnapshotData>()
        val query = searchQuery
        for (snapshot in pSnapshotDataList) {
            if (snapshot.contain(query)) {
                filteredList.add(snapshot)
            }
        }
        _filterdCoinSnapshotDataList.postValue(filteredList)
    }

    fun requestTickerData(
        pMarketCode: KubitMarketCode = KubitMarketCode.KRW
    ) {
        DLog.d(TAG, "requestTickerData() is called!")
        viewModelScope.launch {
            upbitRepository.makeCoinTickerThread(
                pCoinInfoDataList = marketData.getKubitCoinInfoDataList(pMarketCode),
                onSuccessListener = { snapshotDataList ->
                    DLog.d(TAG, "snapshotDataList=$snapshotDataList")
                    _coinSnapshotDataList.postValue(snapshotDataList)
                    setFilteredCoinSnapshotDataList(snapshotDataList)
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
        DLog.d(TAG, "stopTickerData() is called!")
        viewModelScope.launch {
            upbitRepository.stopCoinTickerThread()
        }
    }

    fun requestInvestmentTickerData() {
        DLog.d(TAG, "requestInvestmentTickerData() is called!")
        viewModelScope.launch {
            upbitRepository.makeInvestmentTickerThread(
                pWalletDataList = KubitSession.walletList,
                onSuccessListener = { snapshotDataList ->
                    DLog.d(TAG, "walletSnapshotDataList=$snapshotDataList")
                    val krw = KubitSession.KRW
                    val walletList = KubitSession.walletList

                    var totalAsset: Double = krw
                    var totalBidPrice: Double = 0.0
                    var totalValuation: Double = 0.0

                    val userWalletList: ArrayList<InvestmentWalletData> = arrayListOf()
                    for (idx in walletList.indices) {
                        if (idx in snapshotDataList.indices) {
                            val wallet = walletList[idx]
                            val snapshotData = snapshotDataList[idx]

                            val valuationPrice = wallet.quantity * snapshotData.tradePrice
                            val changeValuation = valuationPrice - wallet.totalPrice
                            val earningRate =
                                if (wallet.totalPrice > 0) changeValuation / wallet.totalPrice else 0.0

                            totalAsset += valuationPrice
                            totalValuation += valuationPrice
                            totalBidPrice += wallet.totalPrice

                            userWalletList.add(
                                InvestmentWalletData(
                                    market = snapshotData.market,
                                    nameKor = snapshotData.nameKor,
                                    nameEng = snapshotData.nameEng,
                                    changeValuation = changeValuation,
                                    earningRate = earningRate,
                                    quantity = wallet.quantity,
                                    bidAvgPrice = wallet.bidAvgPrice,
                                    valuationPrice = valuationPrice,
                                    askTotalPrice = wallet.totalPrice
                                )
                            )
                        }
                    }

                    val sortedUserWalletList = arrayListOf<InvestmentWalletData>().apply {
                        addAll(userWalletList)
                        add(
                            InvestmentWalletData(
                                market = "KRW",
                                nameKor = "",
                                nameEng = "",
                                changeValuation = 0.0,
                                earningRate = 0.0,
                                quantity = 0.0,
                                bidAvgPrice = 0.0,
                                valuationPrice = krw,
                                askTotalPrice = krw
                            )
                        )
                    }
                    sortedUserWalletList.sortWith { wallet1, wallet2 ->
                        wallet2.valuationPrice.compareTo(wallet1.valuationPrice)
                    }
                    val portfolioList: ArrayList<PieEntry> = arrayListOf()
                    var lastRatio: Double = -1.0
                    var lastLabel: String = "etc"
                    for (idx in sortedUserWalletList.indices) {
                        val wallet = sortedUserWalletList[idx]
                        val valuationPrice = wallet.valuationPrice
                        val ratio = valuationPrice / (totalValuation + krw)

                        if (portfolioList.size < 8) {
                            portfolioList.add(
                                PieEntry(
                                    ratio.toFloat(),
                                    ConvertUtil.ratio2pieChartLabel(ratio),
                                    wallet.market.split('-').getOrNull(1) ?: "KRW"
                                )
                            )
                        } else if (portfolioList.size < 9) {
                            lastRatio = ratio
                            lastLabel = wallet.market.split('-').getOrNull(1) ?: "KRW"
                        } else {
                            lastRatio += ratio
                            lastLabel = "etc"
                        }
                    }
                    if (lastRatio != -1.0) {
                        portfolioList.add(
                            PieEntry(
                                lastRatio.toFloat(),
                                ConvertUtil.ratio2pieChartLabel(lastRatio),
                                lastLabel
                            )
                        )
                    }

                    val changeValuation = totalValuation - totalBidPrice
                    val earningRate =
                        if (totalBidPrice > 0) changeValuation / totalBidPrice else 0.0

                    val assetData = InvestmentAssetData(
                        KRW = krw,
                        totalAsset = totalAsset,
                        totalBidPrice = totalBidPrice,
                        changeValuation = changeValuation,
                        totalValuation = totalValuation,
                        earningRate = earningRate,
                        userWalletList = userWalletList,
                        portfolioList = portfolioList
                    )
                    _investmentAssetData.postValue(assetData)
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

    fun requestInvestmentRecordData() {
        DLog.d(TAG, "requestInvestmentRecordData() is called!")
        setProgressFlag(true)
        viewModelScope.launch {

        }
    }

    fun requestInvestmentNotYetData() {
        DLog.d(TAG, "requestInvestmentNotYetData() is called!")
        setProgressFlag(true)
        viewModelScope.launch {

        }
    }

    fun requestReset() {
        DLog.d(TAG, "requestReset() is called!")
        viewModelScope.launch {

        }
    }

    fun requestLogout() {
        DLog.d(TAG, "requestLogout() is called!")
        KubitSession.logout()
    }

    companion object {
        private const val TAG: String = "MainViewModel"
    }

}