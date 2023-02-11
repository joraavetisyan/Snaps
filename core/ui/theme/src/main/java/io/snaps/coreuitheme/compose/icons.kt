package io.snaps.coreuitheme.compose

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoMode
import androidx.compose.material.icons.rounded.BrowseGallery
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.FlipCameraAndroid
import androidx.compose.material.icons.rounded.HeartBroken
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.NavigateNext
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Try
import androidx.compose.ui.graphics.vector.ImageVector
import io.snaps.corecommon.R
import io.snaps.corecommon.container.IconValue

object SpecificIcons {
    val back: IconValue = vector(Icons.Rounded.ArrowBack)
    val add: IconValue = vector(Icons.Rounded.Add)
    val account: IconValue = vector(Icons.Rounded.AccountCircle)
    val navigateNext: IconValue = vector(Icons.Rounded.NavigateNext)
    val creditCard: IconValue = vector(Icons.Rounded.CreditCard)
    val heart: IconValue = vector(Icons.Rounded.HeartBroken)
    val currencyExchange: IconValue = vector(Icons.Rounded.CurrencyExchange)
    val phone: IconValue = vector(Icons.Rounded.Phone)
    val error: IconValue = vector(Icons.Rounded.Error)
    val favoriteBorder: IconValue = vector(Icons.Rounded.FavoriteBorder)
    val favorite: IconValue = vector(Icons.Filled.Favorite)
    val close: IconValue = vector(Icons.Rounded.Close)
    val fingerprint: IconValue = vector(Icons.Rounded.Fingerprint)
    val delete: IconValue = vector(Icons.Rounded.Delete)
    val done: IconValue = vector(Icons.Rounded.Done)
    val play: IconValue = vector(Icons.Rounded.PlayArrow)
    val themeSystem: IconValue = vector(Icons.Rounded.AutoMode)
    val themeDark: IconValue = vector(Icons.Rounded.DarkMode)
    val themeLight: IconValue = vector(Icons.Rounded.LightMode)
    val flipCamera: IconValue = vector(Icons.Rounded.FlipCameraAndroid)
    val chooseImage: IconValue = vector(Icons.Rounded.BrowseGallery)
    val reload: IconValue = vector(Icons.Rounded.Try)
    val search: IconValue = vector(Icons.Rounded.Search)

    val camera: IconValue = resource(R.drawable.ic_camera)
    val cameraTimer: IconValue = resource(R.drawable.ic_camera_timer)
    val check: IconValue = resource(R.drawable.ic_check)
    val picture: IconValue = resource(R.drawable.ic_picture)
    val profile: IconValue = resource(R.drawable.ic_profile)
    val star: IconValue = resource(R.drawable.ic_star)
    val settings: IconValue = resource(R.drawable.ic_settings)
    val share: IconValue = resource(R.drawable.ic_share)
    val gallery: IconValue = resource(R.drawable.ic_gallery)
    val like: IconValue = resource(R.drawable.ic_like)
    val instagram: IconValue = resource(R.drawable.ic_instagram)
    val google: IconValue = resource(R.drawable.ic_google)
    val apple: IconValue = resource(R.drawable.ic_apple)
    val facebook: IconValue = resource(R.drawable.ic_facebook)
    val twitter: IconValue = resource(R.drawable.ic_twitter)
    val question: IconValue = resource(R.drawable.ic_question)
    val gem: IconValue = resource(R.drawable.ic_gem)
    val copy: IconValue = resource(R.drawable.ic_copy)
    val telegram: IconValue = resource(R.drawable.ic_telegram)
    val discord: IconValue = resource(R.drawable.ic_discord)
    val verified: IconValue = resource(R.drawable.ic_verified)
    val send: IconValue = resource(R.drawable.ic_send)
    val exchange: IconValue = resource(R.drawable.ic_exchange)
    val topUp: IconValue = resource(R.drawable.ic_topup)
    val withdraw: IconValue = resource(R.drawable.ic_withdraw)
    val bnbToken: IconValue = resource(R.drawable.ic_bnb_token)
    val comment: IconValue = resource(R.drawable.ic_comment)
    val addCircled: IconValue = resource(R.drawable.ic_add)
}

private fun vector(value: ImageVector) = IconValue.Vector(value)
private fun resource(@DrawableRes value: Int) = IconValue.ResVector(value)