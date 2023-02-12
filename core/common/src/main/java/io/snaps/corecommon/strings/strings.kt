package io.snaps.corecommon.strings

internal val strings = mapOf(
    key(StringKey.ActionStart) to mapOf(
        SupportedLanguageKey.En to "Start",
    ),
    key(StringKey.ActionSave) to mapOf(
        SupportedLanguageKey.En to "Save",
    ),
    key(StringKey.ActionReply) to mapOf(
        SupportedLanguageKey.En to "Reply",
    ),
    key(StringKey.ActionReload) to mapOf(
        SupportedLanguageKey.En to "Reload",
    ),
    key(StringKey.ActionContinue) to mapOf(
        SupportedLanguageKey.En to "Continue",
    ),

    key(StringKey.ErrorLoadFail) to mapOf(
        SupportedLanguageKey.En to "Error while loading",
    ),

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
    key(StringKey.RegistrationMessagePrivacyPolicy) to mapOf(
        SupportedLanguageKey.En to "By creating an account, I consent to the processing of my data in accordance with the Privacy Policy and Terms of Use",
    ),
    key(StringKey.RegistrationActionPrivacyPolicy) to mapOf(
        SupportedLanguageKey.En to "Privacy Policy",
    ),
    key(StringKey.RegistrationActionTermsOfUse) to mapOf(
        SupportedLanguageKey.En to "Terms of Use",
    ),
    key(StringKey.RegistrationDialogSignInTitle) to mapOf(
        SupportedLanguageKey.En to "SignIn",
    ),
    key(StringKey.RegistrationDialogSignInMessage) to mapOf(
        SupportedLanguageKey.En to "Enter your e-mail address and password to login to SNAPS",
    ),
    key(StringKey.RegistrationDialogSignInHintEmail) to mapOf(
        SupportedLanguageKey.En to "Enter your email address",
    ),
    key(StringKey.RegistrationDialogSignInActionLogin) to mapOf(
        SupportedLanguageKey.En to "Sign in with email",
    ),
    key(StringKey.RegistrationDialogSignInActionRegistration) to mapOf(
        SupportedLanguageKey.En to "Don't have account yet? Sign up",
    ),
    key(StringKey.RegistrationDialogSignInHintPassword) to mapOf(
        SupportedLanguageKey.En to "Enter password",
    ),
    key(StringKey.RegistrationDialogVerificationTitle) to mapOf(
        SupportedLanguageKey.En to "Verify your email address",
    ),
    key(StringKey.RegistrationDialogVerificationMessage) to mapOf(
        SupportedLanguageKey.En to "Check your email for the verification link sent to account",
    ),
    key(StringKey.RegistrationDialogVerificationAction) to mapOf(
        SupportedLanguageKey.En to "Ok",
    ),
    key(StringKey.RegistrationDialogSignUpTitle) to mapOf(
        SupportedLanguageKey.En to "Sign up",
    ),
    key(StringKey.RegistrationDialogSignUpMessage) to mapOf(
        SupportedLanguageKey.En to "Enter your e-mail address and password to registration to SNAPS",
    ),
    key(StringKey.RegistrationDialogSignUpHintEmail) to mapOf(
        SupportedLanguageKey.En to "Enter your email address",
    ),
    key(StringKey.RegistrationDialogSignUpHintPassword) to mapOf(
        SupportedLanguageKey.En to "Enter password",
    ),
    key(StringKey.RegistrationDialogSignUpHintConfirmPassword) to mapOf(
        SupportedLanguageKey.En to "Confirm password",
    ),
    key(StringKey.RegistrationDialogSignUpActionRegistration) to mapOf(
        SupportedLanguageKey.En to "Sign up with email",
    ),
    key(StringKey.RegistrationDialogSignUpActionLogin) to mapOf(
        SupportedLanguageKey.En to "Already has account? Sign in",
    ),

    key(StringKey.WalletImportTitle) to mapOf(
        SupportedLanguageKey.En to "Wallet import",
    ),
    key(StringKey.WalletImportMessageEnterPhrases) to mapOf(
        SupportedLanguageKey.En to "Enter your 12-word seed phrase to be able to use the wallet inside the app",
    ),
    key(StringKey.WalletImportHint) to mapOf(
        SupportedLanguageKey.En to "%s. Enter here",
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

    key(StringKey.MnemonicsTitle) to mapOf(
        SupportedLanguageKey.En to "Your Seed Phrase",
    ),
    key(StringKey.MnemonicsMessage) to mapOf(
        SupportedLanguageKey.En to "Please save these 12 words on a piece of paper. Mind their order when notings them down. Keep it in secure place.",
    ),

    key(StringKey.VerificationTitle) to mapOf(
        SupportedLanguageKey.En to "Verification",
    ),
    key(StringKey.VerificationMessage) to mapOf(
        SupportedLanguageKey.En to "Please select correct words based on their numbers (the order they were written on the previous screen)",
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

    key(StringKey.CreateUserTitle) to mapOf(
        SupportedLanguageKey.En to "Your data",
    ),
    key(StringKey.CreateUserMessage) to mapOf(
        SupportedLanguageKey.En to "To start, you need to come up with a nickname and upload an avatar",
    ),
    key(StringKey.CreateUserHintNickname) to mapOf(
        SupportedLanguageKey.En to "Come up with a nickname",
    ),
    key(StringKey.CreateUserFieldPhotoUploaded) to mapOf(
        SupportedLanguageKey.En to "Photo uploaded",
    ),
    key(StringKey.CreateUserActionStart) to mapOf(
        SupportedLanguageKey.En to "Start",
    ),

    key(StringKey.PhotoDialogTitle) to mapOf(
        SupportedLanguageKey.En to "Select photo",
    ),
    key(StringKey.PhotoDialogActionTakePhoto) to mapOf(
        SupportedLanguageKey.En to "Camera",
    ),
    key(StringKey.PhotoDialogActionPickPhoto) to mapOf(
        SupportedLanguageKey.En to "Photo Gallery",
    ),
    key(StringKey.PhotoDialogActionCancel) to mapOf(
        SupportedLanguageKey.En to "Cancel",
    ),

    key(StringKey.ReferralProgramTitle) to mapOf(
        SupportedLanguageKey.En to "Referral program",
    ),

    key(StringKey.PopularVideosTitle) to mapOf(
        SupportedLanguageKey.En to "Popular video",
    ),
    key(StringKey.PopularVideosHint) to mapOf(
        SupportedLanguageKey.En to "Search",
    ),

    key(StringKey.ReferralProgramTitle) to mapOf(
        SupportedLanguageKey.En to "Referral program",
    ),
    key(StringKey.ReferralProgramTitleEnterCode) to mapOf(
        SupportedLanguageKey.En to "Enter ref. the code",
    ),
    key(StringKey.ReferralProgramMessageEnterCode) to mapOf(
        SupportedLanguageKey.En to "Enter a friend code \nto receive bonuses",
    ),
    key(StringKey.ReferralProgramActionEnterCode) to mapOf(
        SupportedLanguageKey.En to "Enter the code",
    ),
    key(StringKey.ReferralProgramHintCode) to mapOf(
        SupportedLanguageKey.En to "Your referral code",
    ),
    key(StringKey.ReferralProgramHintLink) to mapOf(
        SupportedLanguageKey.En to "Ref. link",
    ),
    key(StringKey.ReferralProgramTitleDirectReferral) to mapOf(
        SupportedLanguageKey.En to "Direct referral - %s%%",
    ),
    key(StringKey.ReferralProgramMessageDirectReferral) to mapOf(
        SupportedLanguageKey.En to "You will receive %s%% of all rewards earned by friends",
    ),
    key(StringKey.ReferralProgramActionInviteUser) to mapOf(
        SupportedLanguageKey.En to "Invite user",
    ),
    key(StringKey.ReferralProgramInviteDialogTitle) to mapOf(
        SupportedLanguageKey.En to "Invite friends and earn!",
    ),
    key(StringKey.ReferralProgramInviteDialogMessage) to mapOf(
        SupportedLanguageKey.En to "Copy your code or link and send to your friends!",
    ),
    key(StringKey.ReferralProgramCodeDialogTitle) to mapOf(
        SupportedLanguageKey.En to "Referral Code",
    ),
    key(StringKey.ReferralProgramCodeDialogMessage) to mapOf(
        SupportedLanguageKey.En to "Enter the referral code and you will receive various discounts and bonuses",
    ),
    key(StringKey.ReferralProgramCodeDialogActionInvite) to mapOf(
        SupportedLanguageKey.En to "Referral Code",
    ),
    key(StringKey.ReferralProgramCodeDialogHintEnterCode) to mapOf(
        SupportedLanguageKey.En to "Invite user",
    ),
    key(StringKey.ReferralProgramDialogActionClose) to mapOf(
        SupportedLanguageKey.En to "Close",
    ),

    key(StringKey.SettingsTitle) to mapOf(
        SupportedLanguageKey.En to "Settings",
    ),
    key(StringKey.SettingsTitleWallet) to mapOf(
        SupportedLanguageKey.En to "Wallet",
    ),
    key(StringKey.SettingsTitleReferralProgram) to mapOf(
        SupportedLanguageKey.En to "Referral program",
    ),
    key(StringKey.SettingsTitleSocialNetworks) to mapOf(
        SupportedLanguageKey.En to "Our social networks",
    ),
    key(StringKey.SettingsTitleAboutProject) to mapOf(
        SupportedLanguageKey.En to "About project",
    ),
    key(StringKey.SettingsActionDeleteAccount) to mapOf(
        SupportedLanguageKey.En to "Delete account",
    ),
    key(StringKey.SettingsActionLogout) to mapOf(
        SupportedLanguageKey.En to "Sign out",
    ),

    key(StringKey.SocialNetworksTitle) to mapOf(
        SupportedLanguageKey.En to "Our social networks",
    ),
    key(StringKey.SocialNetworksTitleDiscord) to mapOf(
        SupportedLanguageKey.En to "Discord",
    ),
    key(StringKey.SocialNetworksTitleTelegram) to mapOf(
        SupportedLanguageKey.En to "Telegram",
    ),

    key(StringKey.TasksTitleSlideCurrent) to mapOf(
        SupportedLanguageKey.En to "Current",
    ),
    key(StringKey.TasksTitleSlideHistory) to mapOf(
        SupportedLanguageKey.En to "History",
    ),
    key(StringKey.TasksTitleCurrent) to mapOf(
        SupportedLanguageKey.En to "Daily quests",
    ),
    key(StringKey.TasksTitleHistory) to mapOf(
        SupportedLanguageKey.En to "History",
    ),
    key(StringKey.TasksTitleMessageCurrent) to mapOf(
        SupportedLanguageKey.En to "Complete daily tasks to earn energy, which is converted into tokens",
    ),
    key(StringKey.TasksTitleMessageHistory) to mapOf(
        SupportedLanguageKey.En to "Here you can see the history of your tasks and your progress.",
    ),

    key(StringKey.TaskWatchVideoTitle) to mapOf(
        SupportedLanguageKey.En to "Watch short videos",
    ),

    key(StringKey.TaskLikeAndSubscribeTitle) to mapOf(
        SupportedLanguageKey.En to "Like and subscribe ",
    ),

    key(StringKey.TaskShareTitle) to mapOf(
        SupportedLanguageKey.En to "Share Template",
    ),

    key(StringKey.TaskFindPointsTitle) to mapOf(
        SupportedLanguageKey.En to "Find points",
    ),
    key(StringKey.TaskFindPointsTitleConnectInstagram) to mapOf(
        SupportedLanguageKey.En to "Connect instagram",
    ),
    key(StringKey.TaskFindPointsTitlePointId) to mapOf(
        SupportedLanguageKey.En to "PointID",
    ),
    key(StringKey.TaskFindPointsMessage) to mapOf(
        SupportedLanguageKey.En to "You need to find and enter the Sponsor, which can be found on social networks. For each successful match, you will receive 2 energy.",
    ),
    key(StringKey.TaskFindPointsActionVerify) to mapOf(
        SupportedLanguageKey.En to "Verify",
    ),
    key(StringKey.TaskFindPointsActionConnect) to mapOf(
        SupportedLanguageKey.En to "Connect",
    ),
    key(StringKey.TaskFindPointsActionNotFound) to mapOf(
        SupportedLanguageKey.En to "Points not found",
    ),

    key(StringKey.WalletSettingsTitle) to mapOf(
        SupportedLanguageKey.En to "Wallet settings",
    ),
    key(StringKey.WalletSettingsTitleBackup) to mapOf(
        SupportedLanguageKey.En to "Backup",
    ),
    key(StringKey.WalletSettingsDescriptionBackup) to mapOf(
        SupportedLanguageKey.En to "Export secret seed/private key for wallet recovery",
    ),

    key(StringKey.BackupWalletKeyTitle) to mapOf(
        SupportedLanguageKey.En to "Your Seed Phrase",
    ),
    key(StringKey.BackupWalletKeyMessage) to mapOf(
        SupportedLanguageKey.En to "Please save these 12 words on a piece of paper. Mind their order when notings them down. Keep it in secure place.",
    ),
    key(StringKey.BackupWalletKeyActionHold) to mapOf(
        SupportedLanguageKey.En to "Hold to reveal",
    ),

    key(StringKey.WalletSettingsBackupDialogTitle) to mapOf(
        SupportedLanguageKey.En to "Phrase / key for wallet recovery",
    ),
    key(StringKey.WalletSettingsBackupDialogMessage) to mapOf(
        SupportedLanguageKey.En to "Make sure no one can see your secret data. By opening them to someone, you risk losing your funds",
    ),
    key(StringKey.WalletSettingsBackupDialogAction) to mapOf(
        SupportedLanguageKey.En to "Look",
    ),

    key(StringKey.WalletTitle) to mapOf(
        SupportedLanguageKey.En to "Wallet",
    ),
    key(StringKey.WalletFieldTotal) to mapOf(
        SupportedLanguageKey.En to "Total",
    ),
    key(StringKey.WalletTitleTopUp) to mapOf(
        SupportedLanguageKey.En to "Top up",
    ),
    key(StringKey.WalletTitleWithdraw) to mapOf(
        SupportedLanguageKey.En to "Withdraw",
    ),
    key(StringKey.WalletTitleExchange) to mapOf(
        SupportedLanguageKey.En to "Exchange",
    ),
    key(StringKey.WalletTitleBalance) to mapOf(
        SupportedLanguageKey.En to "Balance",
    ),
    key(StringKey.WalletTitleSelectCurrency) to mapOf(
        SupportedLanguageKey.En to "Select currency",
    ),
    key(StringKey.WalletMessageTopUp) to mapOf(
        SupportedLanguageKey.En to "Make sure you are sending your tokens to the correct address and the correct BEP-20 network",
    ),

    key(StringKey.WithdrawTitle) to mapOf(
        SupportedLanguageKey.En to "Withdraw BNB",
    ),
    key(StringKey.WithdrawHintAddress) to mapOf(
        SupportedLanguageKey.En to "Enter address",
    ),
    key(StringKey.WithdrawHintAmount) to mapOf(
        SupportedLanguageKey.En to "Enter amount",
    ),
    key(StringKey.WithdrawFieldAvailable) to mapOf(
        SupportedLanguageKey.En to "Available: %s",
    ),
    key(StringKey.WithdrawFieldTransactionFee) to mapOf(
        SupportedLanguageKey.En to "Transaction fee",
    ),
    key(StringKey.WithdrawFieldTotal) to mapOf(
        SupportedLanguageKey.En to "Total",
    ),
    key(StringKey.WithdrawActionConfirmTransaction) to mapOf(
        SupportedLanguageKey.En to "Confirm transaction",
    ),

    key(StringKey.ProfileTitle) to mapOf(
        SupportedLanguageKey.En to "My Profile",
    ),
    key(StringKey.ProfileTitleLikes) to mapOf(
        SupportedLanguageKey.En to "Likes",
    ),
    key(StringKey.ProfileTitleSubscribers) to mapOf(
        SupportedLanguageKey.En to "Subscribers",
    ),
    key(StringKey.ProfileTitleSubscriptions) to mapOf(
        SupportedLanguageKey.En to "Subscriptions",
    ),
    key(StringKey.ProfileTitlePublication) to mapOf(
        SupportedLanguageKey.En to "Publication",
    ),

    key(StringKey.CommentsTitle) to mapOf(
        SupportedLanguageKey.En to "%s comments",
    ),
    key(StringKey.CommentsHint) to mapOf(
        SupportedLanguageKey.En to "Add comment...",
    ),

    key(StringKey.ConfirmUnsubscribeDialogMessage) to mapOf(
        SupportedLanguageKey.En to "If you change your mind, you'll have to request to follow %s again",
    ),
    key(StringKey.ConfirmUnsubscribeDialogActionCancel) to mapOf(
        SupportedLanguageKey.En to "Cancel",
    ),
    key(StringKey.ConfirmUnsubscribeDialogActionUnsubscribe) to mapOf(
        SupportedLanguageKey.En to "Unfollow",
    ),
)