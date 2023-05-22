package com.kubit.android.base

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kubit.android.R
import com.kubit.android.common.dialog.ProgressDialog
import com.kubit.android.common.util.VibrateManager

open class BaseActivity : AppCompatActivity() {

    private lateinit var mProgressDialog: ProgressDialog

    protected fun showProgress(pMsg: String = "") {
        if (!this::mProgressDialog.isInitialized) {
            mProgressDialog = ProgressDialog(this, "")
        }

        mProgressDialog.setMessage(pMsg)
        mProgressDialog.show()
    }

    protected fun dismissProgress() {
        if (this::mProgressDialog.isInitialized) {
            mProgressDialog.dismiss()
        }
    }

    protected fun showToastMsg(pMsg: String) {
        Toast.makeText(this, pMsg, Toast.LENGTH_SHORT).show()
    }

    protected fun showErrorMsg() {
        Toast.makeText(
            this,
            resources.getText(R.string.toast_msg_error_001),
            Toast.LENGTH_SHORT
        ).show()
    }

    protected fun vibrate() {
        VibrateManager.requestVibrate(this, VibrateManager.VibrationType.TICK)
    }


}