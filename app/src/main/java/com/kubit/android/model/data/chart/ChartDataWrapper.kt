package com.kubit.android.model.data.chart

import com.github.mikephil.charting.data.CandleEntry

data class ChartDataWrapper(
    val candleEntries: List<CandleEntry>
) {

    override fun toString(): String {
        return "$TAG{" +
                "candleEntries=$candleEntries}"
    }

    companion object {
        private const val TAG: String = "ChartDataWrapper"
    }

}