package io.snaps.featurewallet.domain

import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.coredata.di.Bridged
import javax.inject.Inject

internal object InsufficientBalanceError : Exception() // todo to domain package

interface WalletInteractor {

    suspend fun claim(amount: Double): Effect<Completable>
}

class WalletInteractorImpl @Inject constructor(
    @Bridged private val walletRepository: WalletRepository,
) : WalletInteractor {

    override suspend fun claim(amount: Double): Effect<Completable> {
        return walletRepository.updateSnpsAccount().flatMap {
            requireNotNull(walletRepository.snpsAccountState.value.dataOrCache).let {
                if (it.unlocked.value > 0) {
                    walletRepository.claim(amount)
                } else {
                    Effect.error(AppError.Custom(cause = InsufficientBalanceError))
                }
            }
        }
    }
}