package com.kubit.android.model.data.orderbook

import com.kubit.android.model.data.coin.PriceChangeType

data class OrderBookUnitData(
    val type: Type,
    val price: Double,
    val size: Double,
    val change: PriceChangeType,
    val changeRate: Double
) {

    override fun toString(): String {
        return "$TAG{" +
                "type=$type, " +
                "price=$price, " +
                "size=$size, " +
                "change=$change, " +
                "changeRate=$changeRate}"
    }

    enum class Type {
        /**
         * 매도
         */
        ASK,

        /**
         * 매수
         */
        BID
    }

    companion object {
        private const val TAG: String = "OrderBookUnitData"
    }

}