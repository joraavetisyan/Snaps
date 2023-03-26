package io.snaps.corecrypto.core.factories

import io.snaps.corecommon.model.Uuid
import io.snaps.corecrypto.core.IAccountFactory
import io.snaps.corecrypto.core.IAccountManager
import io.snaps.corecrypto.entities.Account
import io.snaps.corecrypto.entities.AccountOrigin
import io.snaps.corecrypto.entities.AccountType

class AccountFactory(val accountManager: IAccountManager) : IAccountFactory {

    override fun account(
        id: Uuid,
        name: String,
        type: AccountType,
        origin: AccountOrigin,
        backedUp: Boolean,
    ): Account {
        return Account(
            id = id,
            name = name,
            type = type,
            origin = origin,
            isBackedUp = backedUp
        )
    }
}