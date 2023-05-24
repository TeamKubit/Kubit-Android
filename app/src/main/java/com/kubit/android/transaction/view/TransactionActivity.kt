package com.kubit.android.transaction.view

import android.os.Bundle
import com.kubit.android.R
import com.kubit.android.base.BaseActivity
import com.kubit.android.databinding.ActivityTransactionBinding

class TransactionActivity : BaseActivity() {

    private val binding: ActivityTransactionBinding by lazy {
        ActivityTransactionBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setObserver()
        init()
    }

    private fun setObserver() {

    }

    private fun init() {

    }

    companion object {
        private const val TAG: String = "TransactionActivity"
    }

}