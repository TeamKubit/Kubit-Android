package com.kubit.android.model.repository

import android.app.Application
import com.kubit.android.R
import com.kubit.android.base.BaseNetworkRepository
import com.kubit.android.common.util.JsonParserUtil
import com.kubit.android.model.data.login.LoginSessionData
import com.kubit.android.model.data.network.KubitNetworkResult
import com.kubit.android.model.data.network.NetworkResult
import com.kubit.android.model.data.wallet.WalletOverall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

class KubitRepository(
    private val application: Application
) : BaseNetworkRepository(application, TAG) {

    private val jsonParserUtil: JsonParserUtil = JsonParserUtil()

    suspend fun makeLoginRequest(
        pUserID: String,
        pUserPW: String
    ): NetworkResult<LoginSessionData> {
        return withContext(Dispatchers.IO) {
            val hsParams = HashMap<String, String>().apply {
                put("userId", pUserID)
                put("password", pUserPW)
            }
            val message = sendRequestToKubitServer(KUBIT_API_LOGIN_URL, hsParams, POST)

            if (message.isNotEmpty()) {
                try {
                    val jsonRoot = JSONObject(message)
                    jsonParserUtil.getLoginSessionData(jsonRoot)
                } catch (e: JSONException) {
                    NetworkResult.Error(e)
                }
            } else {
                NetworkResult.Fail(
                    application.getString(
                        R.string.api_connection_fail_msg
                    )
                )
            }
        }
    }

    suspend fun makeRefreshTokenRequest(
        pRefreshToken: String
    ): NetworkResult<LoginSessionData> {
        return withContext(Dispatchers.IO) {
            val hsParams = HashMap<String, String>().apply {
                put("refreshToken", pRefreshToken)
            }
            val message = sendRequestToKubitServer(KUBIT_API_REFRESH_TOKEN_URL, hsParams, POST)

            if (message.isNotEmpty()) {
                try {
                    val jsonRoot = JSONObject(message)
                    jsonParserUtil.getLoginSessionData(jsonRoot)
                } catch (e: JSONException) {
                    NetworkResult.Error(e)
                }
            } else {
                NetworkResult.Fail(
                    application.getString(
                        R.string.api_connection_fail_msg
                    )
                )
            }
        }
    }

    suspend fun makeWalletOverallRequest(
        pGrantType: String,
        pAccessToken: String
    ): KubitNetworkResult<WalletOverall> {
        return withContext(Dispatchers.IO) {
            val message = sendRequestToKubitServer(
                KUBIT_API_WALLET_OVERALL_URL,
                hashMapOf(),
                GET,
                "$pGrantType $pAccessToken"
            )

            if (message.isNotEmpty()) {
                try {
                    val jsonRoot = JSONObject(message)
                    jsonParserUtil.getWalletOverallData(jsonRoot)
                } catch (e: JSONException) {
                    KubitNetworkResult.Error(e)
                }
            } else {
                KubitNetworkResult.Fail(
                    application.getString(
                        R.string.api_connection_fail_msg
                    )
                )
            }
        }
    }

    companion object {
        private const val TAG: String = "LoginRepository"

        private const val KUBIT_API_LOGIN_URL: String = "${KUBIT_API_HOST_URL}user/login"
        private const val KUBIT_API_REFRESH_TOKEN_URL: String = "${KUBIT_API_HOST_URL}user/refresh"
        private const val KUBIT_API_WALLET_OVERALL_URL: String =
            "${KUBIT_API_HOST_URL}user/wallet_overall"
    }

}