package io.snaps.basewallet.data

import io.horizontalsystems.marketkit.models.BlockchainType
import io.horizontalsystems.marketkit.models.TokenQuery
import io.horizontalsystems.marketkit.models.TokenType
import io.snaps.basewallet.data.model.WalletSaveRequestDto
import io.snaps.basewallet.domain.WalletModel
import io.snaps.corecommon.ext.log
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.WalletAddress
import io.snaps.corecrypto.core.CryptoKit
import io.snaps.corecrypto.core.IAccountFactory
import io.snaps.corecrypto.core.IAccountManager
import io.snaps.corecrypto.core.IWalletManager
import io.snaps.corecrypto.core.IWordsManager
import io.snaps.corecrypto.core.managers.WalletActivator
import io.snaps.corecrypto.core.providers.PredefinedBlockchainSettingsProvider
import io.snaps.corecrypto.entities.Account
import io.snaps.corecrypto.entities.AccountOrigin
import io.snaps.corecrypto.entities.AccountType
import io.snaps.corecrypto.entities.Wallet
import io.snaps.corecrypto.entities.normalizeNFKD
import io.snaps.coredata.coroutine.IoDispatcher
import io.snaps.coredata.network.apiCall
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

object InvalidMnemonicsException : Exception()

interface WalletRepository {

    fun createAccount(): List<String>

    suspend fun importAccount(words: List<String>): Effect<Completable>

    suspend fun saveLastConnectedAccount(): Effect<Completable>

    fun getActiveAccount(): Account?

    fun getActiveWallets(): List<WalletModel>

    fun getActiveWalletsReceiveAddresses(): List<WalletAddress>
}

class WalletRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val accountFactory: IAccountFactory,
    private val walletManager: IWalletManager,
    private val wordsManager: IWordsManager,
    private val accountManager: IAccountManager,
    private val walletActivator: WalletActivator,
    private val predefinedBlockchainSettingsProvider: PredefinedBlockchainSettingsProvider,
    private val walletApi: WalletApi,
) : WalletRepository {

    private var account: Account? = null

    override fun createAccount(): List<String> {
        val accountType = mnemonicAccountType(12)
        account = accountFactory.account(
            name = "Wallet",
            type = accountType,
            origin = AccountOrigin.Created,
            backedUp = false,
        )
        return accountType.words
    }

    private fun mnemonicAccountType(wordCount: Int): AccountType.Mnemonic {
        // A new account can be created only using an English wordlist and limited chars in the passphrase.
        // Despite it, we add text normalizing.
        // It is to avoid potential issues if we allow non-English wordlists on account creation.
        val words = wordsManager.generateWords(wordCount).map { it.normalizeNFKD() }
        return AccountType.Mnemonic(words, "".normalizeNFKD())
    }

    override suspend fun importAccount(words: List<String>): Effect<Completable> {
        return try {
            wordsManager.validateChecksumStrict(words)
            val accountType = AccountType.Mnemonic(words, "".normalizeNFKD())
            account = accountFactory.account(
                name = "Wallet",
                type = accountType,
                origin = AccountOrigin.Restored,
                backedUp = true,
            )
            saveLastConnectedAccount()
            Effect.completable
        } catch (checksumException: Exception) {
            Effect.error(AppError.Unknown(cause = InvalidMnemonicsException))
        }
    }

    override suspend fun saveLastConnectedAccount(): Effect<Completable> {
        val account = account ?: return Effect.error(AppError.Unknown("No account was created!"))
        accountManager.save(account)
        activateDefaultWallets(account)
        predefinedBlockchainSettingsProvider.prepareNew(account, BlockchainType.Zcash)
        this.account = null
        return getActiveWalletsReceiveAddresses().firstOrNull()?.let {
            apiCall(ioDispatcher) { walletApi.save(WalletSaveRequestDto(it)) }
        } ?: Effect.error(AppError.Unknown("No address!"))
    }

    override fun getActiveAccount(): Account? {
        return accountManager.activeAccount ?: run {
            log("No active account")
            null
        }
    }

    override fun getActiveWallets(): List<WalletModel> {
        return getWallets().toWalletModelList()
    }

    private fun getWallets(): List<Wallet> {
        val account = getActiveAccount() ?: return emptyList()
        return walletManager.getWallets(account)
    }

    override fun getActiveWalletsReceiveAddresses(): List<WalletAddress> {
        return getWallets().mapNotNull {
            CryptoKit.adapterManager.getReceiveAdapterForWallet(it)?.receiveAddress
        }
    }

    private fun activateDefaultWallets(account: Account) {
        val tokenQueries = listOf(
            // boxer
            "0x192E9321b6244D204D4301AfA507EB29CA84D9ef",
            // laflix
            "0x3e3bfa35e81e85be5c65b0c759fe1b6ed6525ec0",
            // wrapped bnb
            "0xbb4CdB9CBd36B01bD1cBaEBF2De08d9173bc095c",
            // BUSD
            "0xe9e7cea3dedca5984780bafc599bd69add087d56",
        ).map {
            TokenQuery(BlockchainType.BinanceSmartChain, TokenType.Eip20(it))
        }
        walletActivator.activateWallets(account, tokenQueries)
    }
}