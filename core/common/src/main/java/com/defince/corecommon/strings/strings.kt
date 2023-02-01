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
        SupportedLanguageKey.En to "By creating an account, I consent to the processing of my data in accordance with the Privacy Policy and Terms of Use",
    ),
    key(StringKey.RegistrationActionPrivacyPolicy) to mapOf(
        SupportedLanguageKey.En to "Privacy Policy",
    ),
    key(StringKey.RegistrationActionTermsOfUse) to mapOf(
        SupportedLanguageKey.En to "Terms of Use",
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

    key(StringKey.WalletImportTitle) to mapOf(
        SupportedLanguageKey.En to "Wallet import",
    ),
    key(StringKey.WalletImportMessageEnterPhrases) to mapOf(
        SupportedLanguageKey.En to "Enter your 12-word seed phrase to be able to use the wallet inside the app",
    ),
    key(StringKey.WalletImportTitle) to mapOf(
        SupportedLanguageKey.En to "Wallet import",
    ),

    key(StringKey.ConnectWalletActionCreate) to mapOf(
        SupportedLanguageKey.En to "Create Wallet",
    ),
    key(StringKey.ConnectWalletMessage) to mapOf(
        SupportedLanguageKey.En to "Copy your code or link and send to your friends!",
    ),
    key(StringKey.ConnectWalletTitle) to mapOf(
        SupportedLanguageKey.En to "Connect Wallet",
    ),
    key(StringKey.ConnectWalletActionImport) to mapOf(
        SupportedLanguageKey.En to "Wallet import",
    ),

    key(StringKey.CreateWalletAction) to mapOf(
        SupportedLanguageKey.En to "Continue",
    ),
    key(StringKey.CreateWalletMessage) to mapOf(
        SupportedLanguageKey.En to "If your device is lost or stolen, you can restore access to your funds using a seed phrase. If you lose access to your seed, this will result in a loss of funds.",
    ),
    key(StringKey.CreateWalletTitle) to mapOf(
        SupportedLanguageKey.En to "Write down your seed phrase",
    ),

    key(StringKey.PhraseListTitle) to mapOf(
        SupportedLanguageKey.En to "Your Seed Phrase",
    ),
    key(StringKey.PhraseListMessage) to mapOf(
        SupportedLanguageKey.En to "Please save these 12 words on a piece of paper. Mind their order when notings them down. Keep it in secure place.",
    ),
    key(StringKey.PhraseListActionContinue) to mapOf(
        SupportedLanguageKey.En to "Continue",
    ),

    key(StringKey.VerificationTitle) to mapOf(
        SupportedLanguageKey.En to "Verification",
    ),
    key(StringKey.VerificationMessage) to mapOf(
        SupportedLanguageKey.En to "Please select correct words based on their numbers (the order they were written on the previous screen)",
    ),
    key(StringKey.VerificationActionContinue) to mapOf(
        SupportedLanguageKey.En to "Continue",
    ),

    key(StringKey.CreatedWalletAction) to mapOf(
        SupportedLanguageKey.En to "Continue",
    ),
    key(StringKey.CreatedWalletMessage) to mapOf(
        SupportedLanguageKey.En to "Congratulations! Your wallet has been successfully created, now you can start using your account",
    ),
    key(StringKey.CreatedWalletTitle) to mapOf(
        SupportedLanguageKey.En to "Wallet created",
    ),
)