package io.snaps.corecrypto.core.security

import io.snaps.corecrypto.other.IEncryptionManager
import io.snaps.corecrypto.other.IKeyProvider

class EncryptionManager(private val keyProvider: IKeyProvider) : IEncryptionManager {

    @Synchronized
    override fun encrypt(data: String): String {
        return CipherWrapper().encrypt(data, keyProvider.getKey())
    }

    @Synchronized
    override fun decrypt(data: String): String {
        return CipherWrapper().decrypt(data, keyProvider.getKey())
    }
}
