package com.kubit.android.main.view

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kubit.android.R
import com.kubit.android.base.BaseActivity
import com.kubit.android.base.BaseViewModel
import com.kubit.android.coinlist.view.CoinListFragment
import com.kubit.android.common.util.DLog
import com.kubit.android.databinding.ActivityMainBinding
import com.kubit.android.investment.view.InvestmentFragment
import com.kubit.android.main.viewmodel.MainViewModel
import com.kubit.android.model.data.market.KubitMarketData
import com.kubit.android.model.data.route.KubitTabRouter

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

        val markeData = if (savedInstanceState != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                savedInstanceState.getSerializable("market_data", KubitMarketData::class.java)
            } else {
                savedInstanceState.getSerializable("market_data")
            } as KubitMarketData
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("market_data", KubitMarketData::class.java)
            } else {
                intent.getSerializableExtra("market_data")
            } as KubitMarketData
        }
        model.initMarketData(markeData)

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

        model.tabRouter.observe(this, Observer { router ->
            DLog.d(TAG, "tabRouter=$router")
            when (router) {
                KubitTabRouter.COIN_LIST -> {
                    setFragment(CoinListFragment.getInstance(), CoinListFragment.TAG)
                }

                KubitTabRouter.INVESTMENT -> {
                    setFragment(InvestmentFragment.getInstance(), InvestmentFragment.TAG)
                }

                KubitTabRouter.EXCHANGE -> {

                }

                KubitTabRouter.PROFILE -> {

                }

                else -> {
                    DLog.e(TAG, "Unrecognized TabRouter=$router")
                }
            }
        })
    }

    private fun init() {
        binding.apply {
            navMain.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_home -> {
                        model.setTabRouter(KubitTabRouter.COIN_LIST)
                        true
                    }

                    R.id.menu_investment -> {
                        model.setTabRouter(KubitTabRouter.INVESTMENT)
                        true
                    }

                    R.id.menu_exchange -> {
                        model.setTabRouter(KubitTabRouter.EXCHANGE)
                        true
                    }

                    R.id.menu_profile -> {
                        model.setTabRouter(KubitTabRouter.PROFILE)
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
        }
    }

    // region Fragment 관리
    private fun setFragment(fragment: Fragment, tag: String? = null) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_main, fragment, tag)
            .commit()
    }
    // endregion Fragment 관리

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus

            if (view is EditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)

                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.post {
                        view.clearFocus()
                        val imm: InputMethodManager =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    companion object {
        private const val TAG: String = "MainActivity"
    }

}