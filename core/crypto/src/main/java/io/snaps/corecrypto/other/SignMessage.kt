package io.snaps.corecrypto.other

sealed class SignMessage(val data: String) {
    class Message(data: String, val showLegacySignWarning: Boolean = false) : SignMessage(data)
    class PersonalMessage(data: String) : SignMessage(data)
    class TypedMessage(data: String, val domain: String? = null) : SignMessage(data)
}