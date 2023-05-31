package com.kubit.android.investment.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.kubit.android.common.util.ConvertUtil
import com.kubit.android.databinding.ItemInvestmentAssetBinding
import com.kubit.android.model.data.investment.InvestmentAssetData

class InvestmentAssetViewHolder(
    private val binding: ItemInvestmentAssetBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindData(pData: InvestmentAssetData) {
        val pos = bindingAdapterPosition

        binding.apply {
            // 보유 KRW
            tvInvestmentAssetItemKrw.text = ConvertUtil.tradePrice2string(pData.KRW)
            // 총 보유자산
            tvInvestmentAssetItemTotalAsset.text = ConvertUtil.tradePrice2string(pData.totalAsset)
            // 총매수
            tvInvestmentAssetItemTotalBidPrice.text =
                ConvertUtil.tradePrice2string(pData.totalBidPrice)
            // 평가손익
            tvInvestmentAssetItemChangeValuation.text =
                ConvertUtil.tradePrice2string(pData.changeValuation)
            // 총평가
            tvInvestmentAssetItemTotalValuation.text =
                ConvertUtil.tradePrice2string(pData.totalValuation)
            // 수익률
            tvInvestmentAssetItemEarningRate.text = ConvertUtil.changeRate2string(pData.earningRate)
        }
    }

}