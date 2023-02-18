package io.snaps.corecrypto.entities

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

enum class LaunchPage(@StringRes val titleRes: Int, @DrawableRes val iconRes: Int) {
    Auto(0, 0),
    Balance(0, 0),
    Market(0, 0),
    Watchlist(0, 0);


    companion object {
        private val map = values().associateBy(LaunchPage::name)

        fun fromString(type: String?): LaunchPage? = map[type]
    }
}
