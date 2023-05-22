package com.kubit.android.common.util

import android.content.Context
import android.util.TypedValue

object ConvertUtil {

    fun dp2px(context: Context, dp: Int): Int = dp2px(context, dp.toFloat()).toInt()

    fun dp2px(context: Context, dp: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)

    fun px2dp(context: Context, px: Int): Int = px2dp(context, px.toFloat()).toInt()

    fun px2dp(context: Context, px: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, context.resources.displayMetrics)

}