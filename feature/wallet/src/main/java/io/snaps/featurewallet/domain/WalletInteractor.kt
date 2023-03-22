package io.snaps.featurewallet.domain

import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import javax.inject.Inject

internal object InsufficientBalanceError : Exception()

interface WalletInteractor {

    suspend fun claim(): Effect<Completable>
}

class WalletInteractorImpl @Inject constructor(
    private val walletRepository: WalletRepository,
    private val profileRepository: ProfileRepository,
) : WalletInteractor {

    override suspend fun claim(): Effect<Completable> {
        return profileRepository.updateBalance().flatMap {
            requireNotNull(profileRepository.balanceState.value.dataOrCache).let {
                if (it.unlocked > 0) {
                    walletRepository.claim(it.unlocked)
                } else {
                    Effect.error(AppError.Custom(cause = InsufficientBalanceError))
                }
            }
        }
    }
}