package io.snaps.basewallet.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.horizontalsystems.marketkit.models.BlockchainType
import io.horizontalsystems.marketkit.models.TokenQuery
import io.horizontalsystems.marketkit.models.TokenType
import io.snaps.basewallet.domain.Account
import io.snaps.basewallet.domain.AccountOrigin
import io.snaps.basewallet.domain.AccountType
import io.snaps.basewallet.domain.EnabledWallet
import io.snaps.basewallet.domain.Wallet
import io.snaps.basewallet.domain.normalizeNFKD
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.generateRequestId
import io.snaps.coredata.database.WalletStorage
import javax.inject.Inject

object InvalidMnemonicsException : Exception()

interface WalletRepository {

    fun createAccount(): List<String>

    fun importAccount(words: List<String>): Effect<Completable>

    fun saveLastCreatedAccount()
}

class WalletRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val wordManager: WordManager,
    private val walletStorage: WalletStorage,
) : WalletRepository {

    private var lastCreatedAccount: Account? = null

//    private val marketKit: MarketKit = MarketKit.getInstance(
//        context = context,
//        hsApiBaseUrl = hsApiBaseUrl,
//        hsApiKey = hsApiKey,
//        cryptoCompareApiKey = cryptoCompareApiKey,
//        defiYieldApiKey = defiYieldApiKey
//    )

    override fun createAccount(): List<String> {
        val id = generateRequestId()
        val accountType = mnemonicAccountType()
        lastCreatedAccount = Account(
            id = id,
            name = "accountName", // todo
            type = accountType,
            origin = AccountOrigin.Created,
            isBackedUp = false,
        )
        return accountType.words
    }

    override fun importAccount(words: List<String>): Effect<Completable> {
        val wordsNormed = words.map { it.normalizeNFKD() }

        try {
            wordManager.validateChecksumStrict(wordsNormed)
        } catch (e: Exception) {
            return Effect.error(AppError.Unknown(cause = InvalidMnemonicsException))
        }

        val id = generateRequestId()

        val accountType = AccountType.Mnemonic(wordsNormed, "".normalizeNFKD())

        lastCreatedAccount = Account(
            id = id,
            name = "accountName", // todo
            type = accountType,
            origin = AccountOrigin.Restored,
            isBackedUp = true,
        )

        saveLastCreatedAccount()

        return Effect.completable
    }

    private fun mnemonicAccountType(): AccountType.Mnemonic {
        val words = wordManager.generateWords(AccountType.Mnemonic.Kind.Mnemonic12.wordsCount).map {
            it.normalizeNFKD()
        }
        return AccountType.Mnemonic(words, "".normalizeNFKD())
    }

    override fun saveLastCreatedAccount() {
        val account = lastCreatedAccount ?: return
        walletStorage.wallet = account.type.words
        //        storage.save(account)// todo
        val tokenQueries = listOf(
            // BUSD
            TokenQuery(
                blockchainType = BlockchainType.BinanceSmartChain,
                tokenType = TokenType.Eip20("0xe9e7cea3dedca5984780bafc599bd69add087d56"),
            ),
        )
        activateWallets(account, tokenQueries)
    }

    private fun activateWallets(account: Account, tokenQueries: List<TokenQuery>) {
        val wallets = mutableListOf<Wallet>()

//        for (tokenQuery in tokenQueries) {
//            val token = marketKit.token(tokenQuery) ?: continue
//            wallets.add(Wallet(token, account))
//        }

        val enabledWallets = mutableListOf<EnabledWallet>()

        wallets.forEachIndexed { index, wallet ->

            enabledWallets.add(
                enabledWallet(wallet, index)
            )
        }

//        storage.save(enabledWallets) // todo
    }

    private fun enabledWallet(wallet: Wallet, index: Int? = null): EnabledWallet {
        return EnabledWallet(
            wallet.token.tokenQuery.id,
            wallet.coinSettings.id,
            wallet.account.id,
            index,
            wallet.coin.name,
            wallet.coin.code,
            wallet.decimal
        )
    }
}