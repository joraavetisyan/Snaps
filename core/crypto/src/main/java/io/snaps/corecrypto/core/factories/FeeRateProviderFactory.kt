package io.snaps.corecrypto.core.factories

import io.horizontalsystems.marketkit.models.BlockchainType
import io.snaps.corecrypto.core.CryptoKit
import io.snaps.corecrypto.core.IFeeRateProvider
import io.snaps.corecrypto.core.providers.BitcoinCashFeeRateProvider
import io.snaps.corecrypto.core.providers.BitcoinFeeRateProvider
import io.snaps.corecrypto.core.providers.DashFeeRateProvider
import io.snaps.corecrypto.core.providers.LitecoinFeeRateProvider

object FeeRateProviderFactory {
    fun provider(blockchainType: BlockchainType): IFeeRateProvider? {
        val feeRateProvider = CryptoKit.feeRateProvider

        return when (blockchainType) {
            is BlockchainType.Bitcoin -> BitcoinFeeRateProvider(feeRateProvider)
            is BlockchainType.Litecoin -> LitecoinFeeRateProvider(feeRateProvider)
            is BlockchainType.BitcoinCash -> BitcoinCashFeeRateProvider(feeRateProvider)
            is BlockchainType.Dash -> DashFeeRateProvider(feeRateProvider)
            else -> null
        }
    }

}
