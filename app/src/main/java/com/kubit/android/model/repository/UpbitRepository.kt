package com.kubit.android.model.repository

import android.app.Application
import com.kubit.android.base.BaseNetworkRepository
import com.kubit.android.common.util.DLog
import com.kubit.android.common.util.JsonParserUtil
import com.kubit.android.model.data.coin.CoinSnapshotData
import com.kubit.android.model.data.coin.KubitCoinInfoData
import com.kubit.android.model.repository.thread.KubitTickerThread

class UpbitRepository(
    private val application: Application
) : BaseNetworkRepository(application, TAG) {

    private val jsonParserUtil: JsonParserUtil = JsonParserUtil()

    private var kubitTickerThread: KubitTickerThread? = null

    /**
     * 코인 시세를 주기적으로 가져오는 Thread를 생성하는 함수
     *
     * @param pCoinInfoDataList     코인 정보 데이터 리스트
     * @param onSuccessListener     데이터를 성공적으로 가져왔을 때, 호출되는 콜백 함수
     * @param onFailListener        API 호출에 실패했을 때, 호출되는 콜백 함수
     * @param onErrorListener       에러가 발생했을 때, 호출되는 콜백 함수
     */
    fun makeCoinTickerThread(
        pCoinInfoDataList: List<KubitCoinInfoData>,
        onSuccessListener: (snapshotDataList: List<CoinSnapshotData>) -> Unit,
        onFailListener: (failMsg: String) -> Unit,
        onErrorListener: (e: Exception) -> Unit
    ) {
        DLog.d(TAG, "makeCoinTickerThread() is called, pCoinInfoDataList=$pCoinInfoDataList")
        kubitTickerThread =
            KubitTickerThread(pCoinInfoDataList, onSuccessListener, onFailListener, onErrorListener)
        kubitTickerThread?.start()
    }

    /**
     * 코인 시세를 주기적으로 가져오는 Thread를 중단하는 함수
     */
    fun stopCoinTickerThread() {
        kubitTickerThread?.kill()
        kubitTickerThread = null
    }


    companion object {
        private const val TAG: String = "UpbitRepository"
    }

}