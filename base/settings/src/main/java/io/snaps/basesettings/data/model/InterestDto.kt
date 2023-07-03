package io.snaps.basesettings.data.model

import io.snaps.corecommon.container.TextValue
import io.snaps.corecommon.container.textValue
import io.snaps.corecommon.model.FullUrl
import io.snaps.corecommon.model.Uuid
import io.snaps.corecommon.strings.StringKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InterestDto(
    @SerialName("id") val id: Uuid,
    @SerialName("image") val image: FullUrl,
    @SerialName("name") val type: InterestType,
)

@Serializable
enum class InterestType() {
    @SerialName("Авто") Auto,
    @SerialName("Природа") Nature,
    @SerialName("Кино") Cinema,
    @SerialName("Футбол") Football,
    @SerialName("Кулинария") Cooking,
    @SerialName("Музыка") Music,
    @SerialName("Фитнес") Fitness,
    @SerialName("Лайф") Life,
    @SerialName("Юмор") Humor,
    @SerialName("Криптовалюта") Crypto,
    @SerialName("Snaps") Snaps,
    @SerialName("Новости") News,
    @SerialName("Искусство") Art,
    @SerialName("Travel") Travel,
    @SerialName("Мотивация") Motivation,
    @SerialName("Финансы") Finance,
    @SerialName("Бизнес") Business,
    @SerialName("Девушки") Girls;

    val label: TextValue
        get() = when (this) {
            Auto -> StringKey.InterestsSelectionTitleAuto.textValue()
            Nature -> StringKey.TransactionTitleSend.textValue()
            Cinema -> StringKey.InterestsSelectionTitleCinema.textValue()
            Football -> StringKey.InterestsSelectionTitleFootball.textValue()
            Cooking -> StringKey.InterestsSelectionTitleCooking.textValue()
            Music -> StringKey.InterestsSelectionTitleMusic.textValue()
            Fitness -> StringKey.InterestsSelectionTitleFitness.textValue()
            Life -> StringKey.InterestsSelectionTitleLife.textValue()
            Humor -> StringKey.InterestsSelectionTitleHumor.textValue()
            Crypto -> StringKey.InterestsSelectionTitleCrypto.textValue()
            Snaps -> StringKey.InterestsSelectionTitleSnaps.textValue()
            News -> StringKey.InterestsSelectionTitleNews.textValue()
            Art -> StringKey.InterestsSelectionTitleArt.textValue()
            Travel -> StringKey.InterestsSelectionTitleTravel.textValue()
            Motivation -> StringKey.InterestsSelectionTitleMotivation.textValue()
            Finance -> StringKey.InterestsSelectionTitleFinance.textValue()
            Girls -> StringKey.InterestsSelectionTitleGirls.textValue()
            Business -> StringKey.InterestsSelectionTitleBusiness.textValue()
        }
}