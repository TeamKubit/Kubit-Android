package com.kubit.android.model.repository

import android.app.Application
import com.kubit.android.base.BaseNetworkRepository

class LoginRepository(
    private val application: Application
) : BaseNetworkRepository(application, TAG) {

    companion object {
        private const val TAG: String = "LoginRepository"
    }

}