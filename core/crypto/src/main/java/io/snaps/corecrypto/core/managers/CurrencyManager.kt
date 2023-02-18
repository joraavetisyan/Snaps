package io.snaps.corecrypto.core.managers

import io.snaps.corecrypto.core.ILocalStorage
import io.snaps.corecrypto.core.providers.AppConfigProvider
import io.snaps.corecrypto.entities.Currency
import io.reactivex.subjects.PublishSubject

class CurrencyManager(private val localStorage: ILocalStorage, private val appConfigProvider: AppConfigProvider) {

    var baseCurrency = getInitialCurrency()
        set(value) {
            field = value

            localStorage.baseCurrencyCode = value.code
            baseCurrencyUpdatedSignal.onNext(Unit)
        }

    private fun getInitialCurrency(): Currency {
        return localStorage.baseCurrencyCode?.let { code ->
            appConfigProvider.currencies.find { it.code == code }
        } ?: appConfigProvider.currencies.first { it.code == "USD" }
    }

    val currencies: List<Currency> = appConfigProvider.currencies

    val baseCurrencyUpdatedSignal = PublishSubject.create<Unit>()
}
