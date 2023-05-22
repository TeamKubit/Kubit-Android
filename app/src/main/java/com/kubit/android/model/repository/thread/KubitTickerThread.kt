package com.kubit.android.model.repository.thread

import android.os.Handler
import android.os.Looper
import com.kubit.android.common.util.DLog
import com.kubit.android.common.util.JsonParserUtil
import com.kubit.android.model.data.coin.CoinSnapshotData
import com.kubit.android.model.data.coin.KubitCoinInfoData
import org.json.JSONArray
import org.json.JSONException

class KubitTickerThread(
    private val coinInfoDataList: List<KubitCoinInfoData>,
    private val onSuccessListener: (snapshotDataList: List<CoinSnapshotData>) -> Unit,
    private val onFailListener: (failMsg: String) -> Unit,
    private val onErrorListener: (e: Exception) -> Unit
) : BaseNetworkThread(TAG) {

    private val mHandler = Handler(Looper.getMainLooper())
    private val jsonParserUtil: JsonParserUtil = JsonParserUtil()

    private var _isActive: Boolean = true
    val isActive: Boolean get() = _isActive

    fun kill() {
        _isActive = false
    }

    override fun run() {
        val sb = StringBuilder()
        for (coinInfo in coinInfoDataList) {
            if (sb.isNotEmpty()) sb.append(", ")
            sb.append(coinInfo.market)
        }

        val hsParams = HashMap<String, String>().apply {
            put("markets", sb.toString())
        }

        if (hsParams.isNotEmpty()) {
            while (isActive) {
                val data = sendRequest(UPBIT_API_TICKER_URL, hsParams, GET)

                try {
                    val jsonRoot = JSONArray(data)
                    val result = jsonParserUtil.getCoinSnapshotDataList(jsonRoot, coinInfoDataList)

                    if (result.isNotEmpty()) {
                        onSuccessListener(result)
                    } else {
                        onFailListener("Fail to fetch Coin Snapshot Data!")
                    }
                } catch (e: JSONException) {
                    mHandler.post {
                        onErrorListener(e)
                    }
                }

                sleep(SLEEP_TIME)
            }
        } else {
            DLog.e(TAG, "coinInfoDataList=$coinInfoDataList is empty!")
        }
    }

    companion object {
        private const val TAG: String = "KubitTickerThread"

        private const val UPBIT_API_TICKER_URL = "${UPBIT_API_HOST_URL}ticker"
        private const val SLEEP_TIME: Long = 500
    }

}