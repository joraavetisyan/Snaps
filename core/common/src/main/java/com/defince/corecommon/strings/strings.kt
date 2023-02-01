package com.defince.corecommon.strings

internal val strings = mapOf(
    key(StringKey.ConnectionSuccessMessage) to mapOf(
        SupportedLanguageKey.Ru to "Соединение восстановлено",
        SupportedLanguageKey.En to "Connection restored",
    ),
    key(StringKey.ConnectionErrorMessage) to mapOf(
        SupportedLanguageKey.Ru to "Отсутствует подключение к сети",
        SupportedLanguageKey.En to "No network connection",
    ),
    key(StringKey.UnknownErrorMessage) to mapOf(
        SupportedLanguageKey.Ru to "Произошла ошибка. Попробуйте повторить позднее",
        SupportedLanguageKey.En to "An error has occurred. Try again later",
    ),

    key(StringKey.NumPadForget) to mapOf(
        SupportedLanguageKey.Ru to "Забыл код",
        SupportedLanguageKey.En to "Forgot",
    ),

    key(StringKey.RegistrationTitle) to mapOf(
        SupportedLanguageKey.En to "Login / Registration",
    ),
    key(StringKey.RegistrationActionLoginWithApple) to mapOf(
        SupportedLanguageKey.En to "Login with Apple",
    ),
    key(StringKey.RegistrationActionLoginWithGoogle) to mapOf(
        SupportedLanguageKey.En to "Login with Google",
    ),
    key(StringKey.RegistrationActionLoginWithEmail) to mapOf(
        SupportedLanguageKey.En to "Login with Email",
    ),
    key(StringKey.RegistrationActionLoginWithTwitter) to mapOf(
        SupportedLanguageKey.En to "Login with Twitter",
    ),
    key(StringKey.RegistrationActionLoginWithFacebook) to mapOf(
        SupportedLanguageKey.En to "Login with Facebook",
    ),
    key(StringKey.RegistrationFieldOr) to mapOf(
        SupportedLanguageKey.En to "Or",
    ),
    key(StringKey.RegistrationActionSendCode) to mapOf(
        SupportedLanguageKey.En to "Send",
    ),
    key(StringKey.RegistrationMessagePrivacyPolicy) to mapOf(
        SupportedLanguageKey.En to "By creating an account, I consent to the processing of my data in accordance with the Privacy Policy and \nTerms of Use",
    ),
    key(StringKey.RegistrationActionPrivacyPolicy) to mapOf(
        SupportedLanguageKey.En to "PrivacyPolicy",
    ),
    key(StringKey.RegistrationActionTermsOfUse) to mapOf(
        SupportedLanguageKey.En to "TermsOfUse",
    ),
    key(StringKey.RegistrationHintEmailAddress) to mapOf(
        SupportedLanguageKey.En to "Enter your email address",
    ),
    key(StringKey.RegistrationHintConfirmationCode) to mapOf(
        SupportedLanguageKey.En to "Enter confirmation code",
    ),
    key(StringKey.RegistrationMessageEnterEmail) to mapOf(
        SupportedLanguageKey.En to "Enter your e-mail address and receive the code to the specified mail",
    ),

    key(StringKey.SubsActionSubscriptions) to mapOf(
        SupportedLanguageKey.En to "%s Subscriptions",
    ),
    key(StringKey.SubsActionSubscribers) to mapOf(
        SupportedLanguageKey.En to "%s Subscribers",
    ),
    key(StringKey.SubsActionFollow) to mapOf(
        SupportedLanguageKey.En to "Follow",
    ),
    key(StringKey.SubsActionFollowing) to mapOf(
        SupportedLanguageKey.En to "Following",
    ),

    key(StringKey.PopularVideosTitle) to mapOf(
        SupportedLanguageKey.En to "Popular video",
    ),
)