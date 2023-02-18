package io.snaps.corecrypto.core

import android.content.Intent
import android.os.Parcelable
import androidx.annotation.CheckResult
import io.horizontalsystems.ethereumkit.core.toRawHexString
import io.horizontalsystems.marketkit.models.Auditor
import io.horizontalsystems.marketkit.models.CoinCategory
import io.horizontalsystems.marketkit.models.CoinInvestment
import io.horizontalsystems.marketkit.models.CoinTreasury
import io.horizontalsystems.marketkit.models.FullCoin
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

val <T> Optional<T>.orNull: T?
    get() = when {
        isPresent -> get()
        else -> null
    }

val CoinCategory.imageUrl: String
    get() = "https://cdn.blocksdecoded.com/category-icons/$uid@3x.png"

val CoinInvestment.Fund.logoUrl: String
    get() = "https://cdn.blocksdecoded.com/fund-icons/$uid@3x.png"

val CoinTreasury.logoUrl: String
    get() = "https://cdn.blocksdecoded.com/treasury-icons/$fundUid@3x.png"

val Auditor.logoUrl: String
    get() = "https://cdn.blocksdecoded.com/auditor-icons/$name@3x.png"

fun List<FullCoin>.sortedByFilter(filter: String, enabled: (FullCoin) -> Boolean): List<FullCoin> {
    var comparator: Comparator<FullCoin> = compareByDescending {
        enabled.invoke(it)
    }
    if (filter.isNotBlank()) {
        val lowercasedFilter = filter.lowercase()
        comparator = comparator
            .thenByDescending {
                it.coin.code.lowercase() == lowercasedFilter
            }.thenByDescending {
                it.coin.code.lowercase().startsWith(lowercasedFilter)
            }.thenByDescending {
                it.coin.name.lowercase().startsWith(lowercasedFilter)
            }
    }
    comparator = comparator.thenBy {
        it.coin.marketCapRank ?: Int.MAX_VALUE
    }
    comparator = comparator.thenBy {
        it.coin.name.lowercase(Locale.ENGLISH)
    }

    return sortedWith(comparator)
}


// String

fun String.hexToByteArray(): ByteArray {
    return ByteArray(this.length / 2) {
        this.substring(it * 2, it * 2 + 2).toInt(16).toByte()
    }
}

// ByteArray

fun ByteArray.toRawHexString(): String {
    return this.joinToString(separator = "") {
        it.toInt().and(0xff).toString(16).padStart(2, '0')
    }
}

fun ByteArray?.toHexString(): String {
    val rawHex = this?.toRawHexString() ?: return ""
    return "0x$rawHex"
}

// Intent & Parcelable Enum
fun Intent.putParcelableExtra(key: String, value: Parcelable) {
    putExtra(key, value)
}

@CheckResult
fun <T> Observable<T>.subscribeIO(onNext: (t: T) -> Unit): Disposable {
    return this
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(onNext)
}

@CheckResult
fun <T> Observable<T>.subscribeIO(
    onSuccess: (t: T) -> Unit,
    onError: (e: Throwable) -> Unit
): Disposable {
    return this
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(onSuccess, onError)
}

@CheckResult
fun <T> Flowable<T>.subscribeIO(onNext: (t: T) -> Unit): Disposable {
    return this
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(onNext)
}

@CheckResult
fun <T> Single<T>.subscribeIO(
    onSuccess: (t: T) -> Unit,
    onError: (e: Throwable) -> Unit
): Disposable {
    return this
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(onSuccess, onError)
}

@CheckResult
fun <T> Single<T>.subscribeIO(onSuccess: (t: T) -> Unit): Disposable {
    return this
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(onSuccess)
}

fun String.shorten(): String {
    val prefixes = listOf("0x", "bc", "bnb", "ltc", "bitcoincash:")

    var prefix = ""
    for (p in prefixes) {
        if (this.startsWith(p)) {
            prefix = p
            break
        }
    }

    val withoutPrefix = this.removePrefix(prefix)

    val characters = 4
    return if (withoutPrefix.length > characters * 2)
        prefix + withoutPrefix.take(characters) + "..." + withoutPrefix.takeLast(characters)
    else this
}


