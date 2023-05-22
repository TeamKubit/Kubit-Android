package com.kubit.android.intro.view

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kubit.android.R
import com.kubit.android.base.BaseActivity
import com.kubit.android.base.BaseViewModel
import com.kubit.android.common.dialog.MessageDialog
import com.kubit.android.common.util.NetworkUtil
import com.kubit.android.databinding.ActivityIntroBinding
import com.kubit.android.intro.viewmodel.IntroViewModel
import com.kubit.android.main.view.MainActivity

class IntroActivity : BaseActivity() {

    private val model: IntroViewModel by lazy {
        ViewModelProvider(
            this,
            BaseViewModel.Factory(application)
        )[IntroViewModel::class.java]
    }
    private val binding: ActivityIntroBinding by lazy {
        ActivityIntroBinding.inflate(layoutInflater)
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

        model.marketData.observe(this, Observer { marketData ->
            if (marketData != null) {
                val mainIntent = Intent(this, MainActivity::class.java).apply {
                    putExtra("market_data", marketData)
                }
                startActivity(mainIntent)
                finish()
            }
        })
    }

    private fun init() {
        if (NetworkUtil.checkNetworkEnable(this)) {
            model.requestMarketCode()
        } else {
            showNetworkDialog()
        }
    }

    // region Dialog
    private fun showNetworkDialog() {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment is MessageDialog) {
                return
            }
        }

        MessageDialog(resources.getString(R.string.dialog_msg_002)) { finish() }
            .show(supportFragmentManager, MessageDialog.TAG)
    }
    // endregion Dialog

    companion object {
        private const val TAG: String = "IntroActivity"
    }

}