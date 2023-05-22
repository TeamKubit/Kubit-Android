package com.kubit.android.base

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kubit.android.R
import com.kubit.android.common.util.VibrateManager

open class BaseFragment : Fragment() {

    protected fun showToastMsg(pMsg: String) {
        Toast.makeText(requireContext(), pMsg, Toast.LENGTH_SHORT).show()
    }

    protected fun showErrorMsg() {
        Toast.makeText(
            requireContext(),
            resources.getText(R.string.toast_msg_error_001),
            Toast.LENGTH_SHORT
        ).show()
    }

    protected fun vibrate() {
        VibrateManager.requestVibrate(requireContext(), VibrateManager.VibrationType.TICK)
    }

}