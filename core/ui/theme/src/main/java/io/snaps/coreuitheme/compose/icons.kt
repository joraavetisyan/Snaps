package io.snaps.coreuitheme.compose

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AssignmentTurnedIn
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.FlipCameraAndroid
import androidx.compose.material.icons.rounded.HeartBroken
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.NavigateNext
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Try
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material.icons.rounded.VolumeMute
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.ui.graphics.vector.ImageVector
import io.snaps.corecommon.R
import io.snaps.corecommon.container.IconValue

object SpecificIcons {
    val back: IconValue = vector(Icons.Rounded.ArrowBack)
    val forward: IconValue = vector(Icons.Rounded.ArrowForward)
    val add: IconValue = vector(Icons.Rounded.Add)
    val account: IconValue = vector(Icons.Rounded.AccountCircle)
    val navigateNext: IconValue = vector(Icons.Rounded.NavigateNext)
    val heart: IconValue = vector(Icons.Rounded.HeartBroken)
    val error: IconValue = vector(Icons.Rounded.Error)
    val favoriteBorder: IconValue = vector(Icons.Rounded.FavoriteBorder)
    val favorite: IconValue = vector(Icons.Filled.Favorite)
    val close: IconValue = vector(Icons.Rounded.Close)
    val fingerprint: IconValue = vector(Icons.Rounded.Fingerprint)
    val delete: IconValue = vector(Icons.Rounded.Delete)
    val done: IconValue = vector(Icons.Rounded.Done)
    val play: IconValue = vector(Icons.Outlined.PlayArrow)
    val pause: IconValue = vector(Icons.Outlined.Pause)
    val flipCamera: IconValue = vector(Icons.Rounded.FlipCameraAndroid)
    val reload: IconValue = vector(Icons.Rounded.Try)
    val search: IconValue = vector(Icons.Rounded.Search)
    val arrowDropDown: IconValue = vector(Icons.Filled.ArrowDropDown)
    val info: IconValue = vector(Icons.Outlined.Info)
    val infoRounded: IconValue = vector(Icons.Rounded.Info)
    val checkCircle: IconValue = vector(Icons.Rounded.CheckCircle)
    val moreVert: IconValue = vector(Icons.Rounded.MoreVert)
    val volumeDown: IconValue = vector(Icons.Rounded.VolumeMute)
    val volumeUp: IconValue = vector(Icons.Rounded.VolumeUp)

    // Bottom menu icons
    val camera: IconValue = vector(Icons.Rounded.Videocam)
    val star: IconValue = vector(Icons.Rounded.Star)
    val check: IconValue = vector(Icons.Rounded.AssignmentTurnedIn)
    val picture: IconValue = vector(Icons.Rounded.Collections)
    val profile: IconValue = vector(Icons.Rounded.Person)

    val instagram: IconValue = resource(R.drawable.ic_instagram)
    val google: IconValue = resource(R.drawable.ic_google)
    val facebook: IconValue = resource(R.drawable.ic_facebook)

    val cameraTimer: IconValue = resource(R.drawable.ic_camera_timer)
    val settings: IconValue = resource(R.drawable.ic_settings)
    val gallery: IconValue = resource(R.drawable.ic_gallery)
    val like: IconValue = resource(R.drawable.ic_like)
    val question: IconValue = resource(R.drawable.ic_question)
    val gem: IconValue = resource(R.drawable.ic_gem)
    val verified: IconValue = resource(R.drawable.ic_verified)
    val trophy: IconValue = resource(R.drawable.ic_trophy)

    val sendCircled: IconValue = resource(R.drawable.ic_send)
    val addCircled: IconValue = resource(R.drawable.ic_add)
    val comment: IconValue = resource(R.drawable.ic_comment)
    val share: IconValue = resource(R.drawable.ic_share)
    val copy: IconValue = resource(R.drawable.ic_copy)
    val exchange: IconValue = resource(R.drawable.ic_exchange)
    val topUp: IconValue = resource(R.drawable.ic_topup)
    val withdraw: IconValue = resource(R.drawable.ic_withdraw)

    val apple: IconValue = resource(R.drawable.ic_apple)
    val twitter: IconValue = resource(R.drawable.ic_twitter)
    val telegram: IconValue = resource(R.drawable.ic_telegram)
    val discord: IconValue = resource(R.drawable.ic_discord)
    val bnbToken: IconValue = resource(R.drawable.ic_bnb_token)
    val snpToken: IconValue = resource(R.drawable.ic_snp_token)
}

private fun vector(value: ImageVector) = IconValue.Vector(value)
private fun resource(@DrawableRes value: Int) = IconValue.ResVector(value)