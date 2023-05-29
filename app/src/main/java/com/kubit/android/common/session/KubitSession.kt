package com.kubit.android.common.session

import android.content.Context
import android.content.SharedPreferences
import com.kubit.android.common.util.DLog

object KubitSession {

    private var _sharedPreferences: SharedPreferences? = null
    private val sharedPreferences: SharedPreferences get() = _sharedPreferences!!

    private var _editor: SharedPreferences.Editor? = null
    private val editor: SharedPreferences.Editor get() = _editor!!

    fun init(pContext: Context) {
        DLog.d(TAG, "init KubitSession!")
        _sharedPreferences = pContext.getSharedPreferences(LOGIN_PREF_NAME, Context.MODE_PRIVATE)
        _editor = sharedPreferences.edit()
    }

    /**
     * 로그인 세션을 만드는 함수
     *
     * 새로 로그인을 수행했을 때 반드시 호출해야 함!
     *
     * @param pUserID           사용자 계정 아이디
     * @param pUserPW           사용자 계정 비밀번호
     * @param pUserName         사용자 계정 닉네임
     * @param pAccessToken      AWS Access Token
     * @param pRefreshToken     AWS Refresh Token
     */
    fun createLoginSession(
        pUserID: String,
        pUserPW: String,
        pUserName: String,
        pAccessToken: String,
        pRefreshToken: String
    ) {
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(USER_ID, pUserID)
        editor.putString(USER_PW, pUserPW)
        editor.putString(USER_NAME, pUserName)
        editor.putString(ACCESS_TOKEN, pAccessToken)
        editor.putString(REFRESH_TOKEN, pRefreshToken)
        editor.commit()
    }

    /**
     * AccessToken이 만료되었을 때, 새로운 Token을 받아온 후에 저장할 때 사용하는 함수
     *
     * @param pAccessToken      AWS Access Token
     * @param pRefreshToken     AWS Refresh Token
     */
    fun updateLoginSession(
        pAccessToken: String,
        pRefreshToken: String
    ) {
        editor.putString(ACCESS_TOKEN, pAccessToken)
        editor.putString(REFRESH_TOKEN, pRefreshToken)
        editor.commit()
    }

    /**
     * 로그인 했는지 여부를 반환하는 함수
     */
    fun isLogin(): Boolean = sharedPreferences.getBoolean(IS_LOGIN, false)

    private const val TAG: String = "KubitSession"
    private const val LOGIN_PREF_NAME: String = "kubit"

    private const val IS_LOGIN: String = "is_login"
    private const val USER_ID: String = "user_id"
    private const val USER_PW: String = "user_pw"
    private const val USER_NAME: String = "user_name"
    private const val ACCESS_TOKEN: String = "access_token"
    private const val REFRESH_TOKEN: String = "refresh_token"

}