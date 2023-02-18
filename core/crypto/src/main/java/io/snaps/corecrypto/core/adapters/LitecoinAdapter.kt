package io.snaps.corecrypto.core.adapters

import io.snaps.corecrypto.core.AdapterErrorWrongParameters
import io.snaps.corecrypto.core.CryptoKit
import io.snaps.corecrypto.core.ISendBitcoinAdapter
import io.snaps.corecrypto.core.UnsupportedAccountException
import io.snaps.corecrypto.entities.AccountType
import io.snaps.corecrypto.entities.Wallet
import io.snaps.corecrypto.entities.transactionrecords.TransactionRecord
import io.horizontalsystems.bitcoincore.BitcoinCore
import io.horizontalsystems.bitcoincore.models.BalanceInfo
import io.horizontalsystems.bitcoincore.models.BlockInfo
import io.horizontalsystems.bitcoincore.models.TransactionInfo
import io.snaps.corecrypto.other.BackgroundManager
import io.horizontalsystems.litecoinkit.LitecoinKit
import io.horizontalsystems.litecoinkit.LitecoinKit.NetworkType
import io.horizontalsystems.marketkit.models.BlockchainType
import java.math.BigDecimal

class LitecoinAdapter(
        override val kit: LitecoinKit,
        syncMode: BitcoinCore.SyncMode,
        backgroundManager: BackgroundManager,
        wallet: Wallet,
        testMode: Boolean
) : BitcoinBaseAdapter(kit, syncMode, backgroundManager, wallet, testMode), LitecoinKit.Listener, ISendBitcoinAdapter {

    constructor(wallet: Wallet, syncMode: BitcoinCore.SyncMode, testMode: Boolean, backgroundManager: BackgroundManager) : this(createKit(wallet, syncMode, testMode), syncMode, backgroundManager, wallet, testMode)

    init {
        kit.listener = this
    }

    //
    // BitcoinBaseAdapter
    //

    override val satoshisInBitcoin: BigDecimal = BigDecimal.valueOf(Math.pow(10.0, decimal.toDouble()))

    //
    // LitecoinKit Listener
    //

    override val explorerTitle: String
        get() = "blockchair.com"


    override fun getTransactionUrl(transactionHash: String): String? =
        if (testMode) null else "https://blockchair.com/litecoin/transaction/$transactionHash"

    override fun onBalanceUpdate(balance: BalanceInfo) {
        balanceUpdatedSubject.onNext(Unit)
    }

    override fun onLastBlockInfoUpdate(blockInfo: BlockInfo) {
        lastBlockUpdatedSubject.onNext(Unit)
    }

    override fun onKitStateUpdate(state: BitcoinCore.KitState) {
        setState(state)
    }

    override fun onTransactionsUpdate(inserted: List<TransactionInfo>, updated: List<TransactionInfo>) {
        val records = mutableListOf<TransactionRecord>()

        for (info in inserted) {
            records.add(transactionRecord(info))
        }

        for (info in updated) {
            records.add(transactionRecord(info))
        }

        transactionRecordsSubject.onNext(records)
    }

    override fun onTransactionsDelete(hashes: List<String>) {
        // ignored for now
    }

    override val blockchainType = BlockchainType.Litecoin


    companion object {

        private fun getNetworkType(testMode: Boolean) =
                if (testMode) NetworkType.TestNet else NetworkType.MainNet

        private fun createKit(wallet: Wallet, syncMode: BitcoinCore.SyncMode, testMode: Boolean): LitecoinKit {
            val account = wallet.account
            val networkType = getNetworkType(testMode)

            when (val accountType = account.type) {
                is AccountType.HdExtendedKey -> {
                    return LitecoinKit(
                        context = CryptoKit.instance,
                        extendedKey = accountType.hdExtendedKey,
                        walletId = account.id,
                        syncMode = syncMode,
                        networkType = networkType,
                        confirmationsThreshold = confirmationsThreshold,
                    )
                }
                is AccountType.Mnemonic -> {
                    val derivation = wallet.coinSettings.derivation ?: throw AdapterErrorWrongParameters("Derivation not set")
                    return LitecoinKit(
                        context = CryptoKit.instance,
                        words = accountType.words,
                        passphrase = accountType.passphrase,
                        walletId = account.id,
                        syncMode = syncMode,
                        networkType = networkType,
                        confirmationsThreshold = confirmationsThreshold,
                        purpose = getPurpose(derivation)
                    )
                }
                else -> throw UnsupportedAccountException()
            }
        }

        fun clear(walletId: String, testMode: Boolean) {
            LitecoinKit.clear(CryptoKit.instance, getNetworkType(testMode), walletId)
        }
    }
}
