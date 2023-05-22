package com.kubit.android.intro.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kubit.android.base.BaseViewModel
import com.kubit.android.common.util.DLog
import com.kubit.android.model.data.market.KubitMarketData
import com.kubit.android.model.data.network.NetworkResult
import com.kubit.android.model.repository.IntroRepository
import kotlinx.coroutines.launch

class IntroViewModel(
    private val introRepository: IntroRepository
) : BaseViewModel() {

    private var _marketData: MutableLiveData<KubitMarketData?> = MutableLiveData(null)
    val marketData: LiveData<KubitMarketData?> get() = _marketData

    fun requestMarketCode() {
        DLog.d("${TAG}_requestMarketCode", "requestMarketCode() is called!")
        viewModelScope.launch {
            when (val result = introRepository.makeMarketCodeRequest()) {
                is NetworkResult.Success<KubitMarketData> -> {
                    DLog.d("${TAG}_requestMarketCode", "marketData=$result")
                    _marketData.value = result.data
                }

                is NetworkResult.Fail -> {
                    setApiFailMsg(result.failMsg)
                }

                is NetworkResult.Error -> {
                    setExceptionData(result.exception)
                }
            }
        }
    }

    companion object {
        private const val TAG: String = "IntroViewModel"
    }

}