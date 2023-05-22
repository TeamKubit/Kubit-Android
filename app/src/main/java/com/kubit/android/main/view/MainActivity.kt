package com.kubit.android.main.view

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kubit.android.base.BaseActivity
import com.kubit.android.base.BaseViewModel
import com.kubit.android.databinding.ActivityMainBinding
import com.kubit.android.main.viewmodel.MainViewModel

class MainActivity : BaseActivity() {

    private val model: MainViewModel by lazy {
        ViewModelProvider(
            this,
            BaseViewModel.Factory(application)
        )[MainViewModel::class.java]
    }
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // region Activity LifeCycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setObserver()
        init()
    }
    // endregion Activity LifeCycle

    private fun setObserver() {
        model.progressFlag.observe(this, Observer { progressFlag ->
            if (progressFlag) {
                showProgress()
            } else {
                dismissProgress()
            }
        })

        model.apiFailMsg.observe(this, Observer { failMsg ->
            if (failMsg.isNotEmpty()) {
                model.setProgressFlag(false)
                showToastMsg(failMsg)
            }
        })

        model.exceptionData.observe(this, Observer { exception ->
            model.setProgressFlag(false)
            showErrorMsg()
        })
    }

    private fun init() {

    }
}