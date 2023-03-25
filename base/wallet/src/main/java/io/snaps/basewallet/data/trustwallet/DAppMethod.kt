package io.snaps.basewallet.data.trustwallet

@Suppress("SpellCheckingInspection")
internal enum class DAppMethod {
    REQUESTACCOUNTS,
    SIGNTRANSACTION,
    UNKNOWN;

    companion object {

        fun fromValue(value: String): DAppMethod {
            return when (value) {
                "requestAccounts" -> REQUESTACCOUNTS
                "signTransaction" -> SIGNTRANSACTION
                else -> UNKNOWN
            }
        }
    }
}