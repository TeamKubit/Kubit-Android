package com.kubit.android.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kubit.android.base.BaseViewModel
import com.kubit.android.model.data.route.KubitTabRouter

class MainViewModel : BaseViewModel() {

    /**
     * 현재 선택된 탭이 어떤 탭인지
     */
    private val _tabRouter: MutableLiveData<KubitTabRouter> =
        MutableLiveData(KubitTabRouter.COIN_LIST)
    val tabRouter: LiveData<KubitTabRouter> get() = _tabRouter

    fun setTabRouter(pTabRouter: KubitTabRouter) {
        if (tabRouter.value != pTabRouter) {
            _tabRouter.value = pTabRouter
        }
    }

    companion object {
        private const val TAG: String = "MainViewModel"
    }

}