package com.kubit.android.login.viewmodel

import com.kubit.android.base.BaseViewModel
import com.kubit.android.model.repository.LoginRepository

class LoginViewModel(
    private val loginRepository: LoginRepository
) : BaseViewModel() {

    companion object {
        private const val TAG: String = "LoginViewModel"
    }

}