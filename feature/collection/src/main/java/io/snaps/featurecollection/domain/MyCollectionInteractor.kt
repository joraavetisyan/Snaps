package io.snaps.featurecollection.domain

import io.snaps.basenft.data.NftRepository
import io.snaps.baseprofile.data.ProfileRepository
import io.snaps.baseprofile.data.model.PaymentsState
import io.snaps.corecommon.model.Completable
import io.snaps.corecommon.model.Effect
import io.snaps.corecommon.model.Uuid
import javax.inject.Inject

object NoEnoughSnpToRepair : Exception()

interface MyCollectionInteractor {

    suspend fun repairGlasses(glassesId: Uuid): Effect<Completable>
}

class MyCollectionInteractorImpl @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val nftRepository: NftRepository,
) : MyCollectionInteractor {

    override suspend fun repairGlasses(glassesId: Uuid): Effect<Completable> {
        return profileRepository.updateData().flatMap {
            if (it.paymentsState == PaymentsState.Blockchain) {
                nftRepository.repairGlassesOnBlockchain(glassesId)
            } else {
                nftRepository.repairGlasses(glassesId)
            }
        }
    }
}