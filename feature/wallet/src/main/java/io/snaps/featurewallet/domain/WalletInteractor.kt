package io.snaps.featurewallet.domain

import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.basewallet.data.WalletRepository
import io.snaps.corecommon.ext.toStringValue
import io.snaps.corecommon.model.AppError
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Loading
import io.snaps.corecommon.model.State
import io.snaps.coredata.coroutine.ApplicationCoroutineScope
import io.snaps.coreui.viewmodel.likeStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal object InsufficientBalanceError : Exception()

interface WalletInteractor {

    val snpFiatState: StateFlow<State<String>>

    suspend fun claim(amount: Double): Effect<Completable>
}

class WalletInteractorImpl @Inject constructor(
    @ApplicationCoroutineScope private val scope: CoroutineScope,
    private val walletRepository: WalletRepository,
    private val profileRepository: ProfileRepository,
) : WalletInteractor {

    override val snpFiatState = profileRepository.balanceState.map {
        when (it) {
            is Loading -> Loading()
            is Effect -> when {
                it.isSuccess -> {
                    val snp = walletRepository.getSnpWalletModel()?.coinValueDouble
                    Effect.success(snp?.times(it.requireData.snpExchangeRate)?.toStringValue().orEmpty())
                }
                else -> Effect.error(requireNotNull(it.errorOrNull))
            }
        }
    }.likeStateFlow(scope, Loading())

    override suspend fun claim(amount: Double): Effect<Completable> {
        return profileRepository.updateBalance().flatMap {
            requireNotNull(profileRepository.balanceState.value.dataOrCache).let {
                if (it.unlocked > 0) {
                    walletRepository.claim(amount)
                } else {
                    Effect.error(AppError.Custom(cause = InsufficientBalanceError))
                }
            }
        }
    }
}