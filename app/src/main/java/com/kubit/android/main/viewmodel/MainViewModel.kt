package com.kubit.android.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kubit.android.base.BaseViewModel
import com.kubit.android.common.util.DLog
import com.kubit.android.model.data.coin.CoinSnapshotData
import com.kubit.android.model.data.market.KubitMarketCode
import com.kubit.android.model.data.market.KubitMarketData
import com.kubit.android.model.data.route.KubitTabRouter
import com.kubit.android.model.repository.UpbitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    fun initMarketData(pMarketData: KubitMarketData) {
        marketData = pMarketData
    }

    fun setTabRouter(pTabRouter: KubitTabRouter) {
        if (tabRouter.value != pTabRouter) {
            _tabRouter.value = pTabRouter
        }
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
        DLog.d(TAG, "stopTickerData() is called")
        viewModelScope.launch {
            upbitRepository.stopCoinTickerThread()
        }
    }

    companion object {
        private const val TAG: String = "MainViewModel"
    }

}