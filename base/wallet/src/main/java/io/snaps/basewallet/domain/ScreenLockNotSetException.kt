package io.snaps.basewallet.domain

sealed class WalletSecurityException(cause: Throwable) : Exception(cause)

class DeviceNotSecuredException(cause: Throwable) : WalletSecurityException(cause)
class ScreenLockNotSetException(cause: Throwable) : WalletSecurityException(cause)
class UserNotAuthenticatedRecentlyException(cause: Throwable) : WalletSecurityException(cause)