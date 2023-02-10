package io.snaps.featureinitialization.data

import io.horizontalsystems.hdwalletkit.Language
import io.horizontalsystems.hdwalletkit.Mnemonic
import javax.inject.Inject

interface WordManager {

    fun validateChecksum(words: List<String>)

    fun validateChecksumStrict(words: List<String>)

    fun isWordValid(word: String): Boolean

    fun isWordPartiallyValid(word: String): Boolean

    fun generateWords(count: Int = 12): List<String>
}

class WordManagerImpl @Inject constructor() : WordManager {

    private val mnemonic = Mnemonic()

    @Throws
    override fun validateChecksum(words: List<String>) {
        mnemonic.validate(words)
    }

    @Throws
    override fun validateChecksumStrict(words: List<String>) {
        mnemonic.validateStrict(words)
    }

    override fun isWordValid(word: String): Boolean {
        return mnemonic.isWordValid(word, false)
    }

    override fun isWordPartiallyValid(word: String): Boolean {
        return mnemonic.isWordValid(word, true)
    }

    override fun generateWords(count: Int): List<String> {
        val strength = Mnemonic.EntropyStrength.fromWordCount(count)
        return mnemonic.generate(strength, Language.English)
    }
}