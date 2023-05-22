package com.kubit.android.model.data.coin

/**
 * @param market            업비트에서 제공중인 시장 정보 ex) KRW-BTC
 * @param marketCode        market의 접두사로써, 마켓 코드 ex) KRW
 * @param nameKor           거래 대상 암호화폐 한글명
 * @param nameEng           거래 대상 암호화폐 영문명
 * @param marketWarning     유의 종목 여부
 */
data class KubitCoinInfoData(
    val market: String,
    val marketCode: String,
    val nameKor: String,
    val nameEng: String,
    val marketWarning: Boolean = false
) : java.io.Serializable {

    override fun toString(): String {
        return "KubitCoinInfoData{" +
                "market=$market, " +
                "marketCode=$marketCode, " +
                "nameKor=$nameKor, " +
                "nameEng=$nameEng, " +
                "marketWarning=$marketWarning}"
    }

}