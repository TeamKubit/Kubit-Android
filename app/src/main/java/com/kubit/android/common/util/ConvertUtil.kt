package com.kubit.android.common.util

import android.content.Context
import android.util.TypedValue
import java.math.RoundingMode
import java.text.DecimalFormat

object ConvertUtil {

    private val priceFormatterOver100 = DecimalFormat("###,###,###").apply {
        roundingMode = RoundingMode.DOWN
    }
    private val priceFormatterUnder100 = DecimalFormat("##.00######").apply {
        roundingMode = RoundingMode.DOWN
    }

    private val changeRateFormatter = DecimalFormat("#0.00").apply {
        roundingMode = RoundingMode.DOWN
    }

    fun dp2px(context: Context, dp: Int): Int = dp2px(context, dp.toFloat()).toInt()

    fun dp2px(context: Context, dp: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)

    fun px2dp(context: Context, px: Int): Int = px2dp(context, px.toFloat()).toInt()

    fun px2dp(context: Context, px: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, context.resources.displayMetrics)


    /**
     * double 타입의 가격을 문자열로 변환하는 함수
     *
     * @param pTradePrice   가격
     */
    fun tradePrice2string(pTradePrice: Double): String {
        return if (pTradePrice < 100) {
            priceFormatterUnder100.format(pTradePrice)
        } else {
            priceFormatterOver100.format(pTradePrice)
        }
    }

    /**
     * double 타입의 부호가 있는 변화율을 문자열로 변환하는 함수
     *
     * @param pSignedChangeRate 부호가 있는 변화율
     */
    fun changeRate2string(pSignedChangeRate: Double): String {
        return "${changeRateFormatter.format(pSignedChangeRate * 100)}%"
    }

    /**
     * 24시간 누적 거래대금을 문자열로 변환하는 함수
     *
     * @param pAccTradePrice24H 24시간 누적 거래대금
     */
    fun accTradePrice24H2string(pAccTradePrice24H: Double): String {
        val volumeUnitMillion = pAccTradePrice24H.div(1000000).toInt()
        return if (volumeUnitMillion < 100) {
            "${priceFormatterUnder100.format(volumeUnitMillion)}백만"
        } else {
            "${priceFormatterOver100.format(volumeUnitMillion)}백만"
        }
    }

}