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
        SupportedLanguageKey.Ru to "Продолжить",
    ),
    key(StringKey.ActionSend) to mapOf(
        SupportedLanguageKey.En to "Send",
        SupportedLanguageKey.Ru to "Отправить",
    ),
    key(StringKey.ActionConfirm) to mapOf(
        SupportedLanguageKey.En to "Confirm",
    ),
    key(StringKey.ActionCancel) to mapOf(
        SupportedLanguageKey.En to "Cancel",
    ),
    key(StringKey.ActionClose) to mapOf(
        SupportedLanguageKey.En to "Close",
    ),
    key(StringKey.ActionDelete) to mapOf(
        SupportedLanguageKey.En to "Delete",
    ),
    key(StringKey.ActionHowItWorks) to mapOf(
        SupportedLanguageKey.En to "How it works?",
    ),
    key(StringKey.ActionOk) to mapOf(
        SupportedLanguageKey.En to "Ok",
    ),

    key(StringKey.MessageSuccess) to mapOf(
        SupportedLanguageKey.Ru to "Успешно!",
        SupportedLanguageKey.En to "Success!",
    ),
    key(StringKey.MessageCopySuccess) to mapOf(
        SupportedLanguageKey.Ru to "Скопировано!",
        SupportedLanguageKey.En to "Copied!",
    ),
    key(StringKey.MessageConnectionSuccess) to mapOf(
        SupportedLanguageKey.Ru to "Соединение восстановлено",
        SupportedLanguageKey.En to "Connection restored",
    ),
    key(StringKey.MessageEmptyVideoFeed) to mapOf(
        SupportedLanguageKey.Ru to "Eще нет видео",
        SupportedLanguageKey.En to "No videos yet",
    ),
    key(StringKey.MessageNothingFound) to mapOf(
        SupportedLanguageKey.Ru to "Ничего не найдено",
        SupportedLanguageKey.En to "Nothing found",
    ),

    key(StringKey.FieldMinutes) to mapOf(
        SupportedLanguageKey.Ru to "%s мин",
        SupportedLanguageKey.En to "%s min",
    ),
    key(StringKey.FieldMinutesShort) to mapOf(
        SupportedLanguageKey.Ru to "%sм",
        SupportedLanguageKey.En to "%sm",
    ),
    key(StringKey.FieldSeconds) to mapOf(
        SupportedLanguageKey.Ru to "%s сек",
        SupportedLanguageKey.En to "%s sec",
    ),
    key(StringKey.FieldSecondsShort) to mapOf(
        SupportedLanguageKey.Ru to "%sс",
        SupportedLanguageKey.En to "%ss",
    ),

    key(StringKey.Error) to mapOf(
        // keep it short
        SupportedLanguageKey.En to "Error",
        SupportedLanguageKey.Ru to "Ошибка",
    ),
    key(StringKey.ErrorLoadFail) to mapOf(
        SupportedLanguageKey.En to "Error while loading",
    ),
    key(StringKey.ErrorConnection) to mapOf(
        SupportedLanguageKey.Ru to "Отсутствует подключение к сети",
        SupportedLanguageKey.En to "No network connection",
    ),
    key(StringKey.ErrorUnknown) to mapOf(
        SupportedLanguageKey.Ru to "Произошла ошибка. Попробуйте повторить позднее",
        SupportedLanguageKey.En to "An error has occurred. Try again later",
    ),

    key(StringKey.OnboardingRankTitle) to mapOf(
        SupportedLanguageKey.Ru to "Добро пожаловать в Snaps!",
        SupportedLanguageKey.En to "Welcome to Snaps!",
    ),
    key(StringKey.OnboardingRankMessage) to mapOf(
        SupportedLanguageKey.Ru to "Для того, чтобы начать получать награду за каждое действие в приложении выберите себе свои первые NFT очки!",
        SupportedLanguageKey.En to "In order to start getting a reward for every action in the app, choose your first NFT points!",
    ),
    key(StringKey.OnboardingRankAction) to mapOf(
        SupportedLanguageKey.Ru to "Выбрать NFT очки",
        SupportedLanguageKey.En to "Select NFT points",
    ),
    key(StringKey.OnboardingPopularTitle) to mapOf(
        SupportedLanguageKey.Ru to "Популярное",
        SupportedLanguageKey.En to "Popular",
    ),
    key(StringKey.OnboardingPopularMessage) to mapOf(
        SupportedLanguageKey.Ru to "Здесь вы найдете видео самых популярных авторов в Snaps! Посмотрите, какие видео сейчас в тренде!",
        SupportedLanguageKey.En to "Here you'll find the videos of the most popular creators on Snaps! See what videos are trending right now!",
    ),
    key(StringKey.OnboardingPopularAction) to mapOf(
        SupportedLanguageKey.Ru to "Продолжить",
        SupportedLanguageKey.En to "Continue",
    ),
    key(StringKey.OnboardingTasksTitle) to mapOf(
        SupportedLanguageKey.Ru to "Задания",
        SupportedLanguageKey.En to "Tasks",
    ),
    key(StringKey.OnboardingTasksMessage) to mapOf(
        SupportedLanguageKey.Ru to "Для того, чтобы получать награды в Snaps Вам необходимо успешно завершать эти задания.\n" +
                "Но перед началом, узнайте подробнее о каждом задании!",
        SupportedLanguageKey.En to "In order to receive rewards in Snaps, you need to successfully complete these tasks.\n" +
                "But before you start, learn more about each task!",
    ),
    key(StringKey.OnboardingTasksAction) to mapOf(
        SupportedLanguageKey.Ru to "Узнать про задания",
        SupportedLanguageKey.En to "Learn about tasks",
    ),
    key(StringKey.OnboardingNftTitle) to mapOf(
        SupportedLanguageKey.Ru to "NFT",
        SupportedLanguageKey.En to "NFT",
    ),
    key(StringKey.OnboardingNftText) to mapOf(
        SupportedLanguageKey.Ru to "Здесь хранятся все ваши NFT очки. Для начала достаточно NFT очков ранга Free. Это позволит получить первые награды и попробовать себя в экосистеме Snaps!",
        SupportedLanguageKey.En to "All your NFT are stored here. To get started, NFT of the Free rank are enough. This will allow you to get your first rewards and try yourself in the Snaps ecosystem!",
    ),
    key(StringKey.OnboardingNftAction) to mapOf(
        SupportedLanguageKey.Ru to "Повысить ранг NFT",
        SupportedLanguageKey.En to "Raise NFT rank",
    ),
    key(StringKey.OnboardingReferralTitle) to mapOf(
        SupportedLanguageKey.Ru to "Реферальная программа",
        SupportedLanguageKey.En to "Referral program",
    ),
    key(StringKey.OnboardingReferralMessage) to mapOf(
        SupportedLanguageKey.Ru to "Вы можете отправить другу Вашу пригласительную ссылку или Ваш реферальный код. Или же если у Вас уже есть реферальный код, то вы можете ввести его в соответсвующем поле. ",
        SupportedLanguageKey.En to "You can send your invite link or your referral code to a friend. Or if you already have a referral code, then you can enter it in the corresponding field.",
    ),
    key(StringKey.OnboardingReferralAction) to mapOf(
        SupportedLanguageKey.Ru to "Узнать подробнее",
        SupportedLanguageKey.En to "Learn more",
    ),
    key(StringKey.OnboardingWalletTitle) to mapOf(
        SupportedLanguageKey.Ru to "Мой кошелек",
        SupportedLanguageKey.En to "My wallet",
    ),
    key(StringKey.OnboardingWalletText) to mapOf(
        SupportedLanguageKey.Ru to "Это ваш криптовалютный кошелек. Он полностью безопасен, а ключи от него знаете только Вы! Проверьте записали ли Вы Вашу секретную фразу!",
        SupportedLanguageKey.En to "This is your cryptocurrency wallet. It is completely safe, and only you know the keys to it! Check if you wrote down your secret phrase!",
    ),
    key(StringKey.OnboardingWalletAction) to mapOf(
        SupportedLanguageKey.Ru to "ОК",
        SupportedLanguageKey.En to "ОК",
    ),
    key(StringKey.OnboardingRewardsTitle) to mapOf(
        SupportedLanguageKey.Ru to "Мои Награды",
        SupportedLanguageKey.En to "My Rewards",
    ),
    key(StringKey.OnboardingRewardsMessage) to mapOf(
        SupportedLanguageKey.Ru to "Здесь хранятся все Ваши награды! В верхнем кошельке вы увидите награды, которые можете получить прямо сейчас. В заблокированном кошельке награды, которые вы можете забирать постепенно каждый день!",
        SupportedLanguageKey.En to "All your rewards are stored here! In the upper wallet you will see the rewards that you can get right now. There are rewards in the locked wallet that you can collect gradually every day!",
    ),
    key(StringKey.OnboardingRewardsAction) to mapOf(
        SupportedLanguageKey.Ru to "ОК",
        SupportedLanguageKey.En to "ОК",
    ),

    key(StringKey.BottomBarTitleFeed) to mapOf(
        SupportedLanguageKey.Ru to "Лента",
        SupportedLanguageKey.En to "Feed",
    ),
    key(StringKey.BottomBarTitleSearch) to mapOf(
        SupportedLanguageKey.Ru to "Поиск",
        SupportedLanguageKey.En to "Search",
    ),
    key(StringKey.BottomBarTitleTasks) to mapOf(
        SupportedLanguageKey.Ru to "Задания",
        SupportedLanguageKey.En to "Tasks",
    ),
    key(StringKey.BottomBarTitleNft) to mapOf(
        SupportedLanguageKey.Ru to "Мои NFT",
        SupportedLanguageKey.En to "My NFTs",
    ),
    key(StringKey.BottomBarTitleReferrals) to mapOf(
        SupportedLanguageKey.Ru to "Рефералы",
        SupportedLanguageKey.En to "Referrals",
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
        SupportedLanguageKey.En to "Sign in",
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
    key(StringKey.RegistrationDialogSignInActionForgotPassword) to mapOf(
        SupportedLanguageKey.En to "Forgot password?",
    ),
    key(StringKey.RegistrationDialogResetPasswordTitle) to mapOf(
        SupportedLanguageKey.En to "Reset password",
    ),
    key(StringKey.RegistrationDialogResetPasswordAction) to mapOf(
        SupportedLanguageKey.En to "Reset password",
    ),
    key(StringKey.RegistrationDialogResetPasswordFieldEnterEmail) to mapOf(
        SupportedLanguageKey.En to "Enter your e-mail address",
    ),
    key(StringKey.RegistrationDialogResetPasswordHintEmail) to mapOf(
        SupportedLanguageKey.En to "Email address",
    ),
    key(StringKey.RegistrationDialogResetPasswordInstructionsMessage) to mapOf(
        SupportedLanguageKey.En to "Instructions were send to email",
    ),

    key(StringKey.WalletImportTitle) to mapOf(
        SupportedLanguageKey.En to "Wallet import",
    ),
    key(StringKey.WalletImportMessageEnterPhrase) to mapOf(
        SupportedLanguageKey.En to "Enter your 12-word seed phrase to be able to use the wallet inside the app",
    ),
    key(StringKey.WalletImportMessagePhraseExplanation) to mapOf(
        SupportedLanguageKey.En to "Usually 12 (sometimes 24) words separated by on space",
    ),
    key(StringKey.WalletImportHint) to mapOf(
        SupportedLanguageKey.En to "Secret phrase...",
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
        SupportedLanguageKey.Ru to "Продолжить",
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
    key(StringKey.DeviceNotSecuredDialogTitle) to mapOf(
        SupportedLanguageKey.En to "Security error",
    ),
    key(StringKey.DeviceNotSecuredDialogMessage) to mapOf(
        SupportedLanguageKey.En to "Please set lock screen on your device to protect your seed phrase",
    ),

    key(StringKey.VerificationTitle) to mapOf(
        SupportedLanguageKey.En to "Verification",
    ),
    key(StringKey.VerificationMessage) to mapOf(
        SupportedLanguageKey.En to "Please select correct words based on their numbers (the order they were written on the previous screen)",
    ),

    key(StringKey.CreatedWalletAction) to mapOf(
        SupportedLanguageKey.En to "Continue",
        SupportedLanguageKey.Ru to "Продолжить",
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
        SupportedLanguageKey.En to "Subscribe",
        SupportedLanguageKey.Ru to "Подписаться",
    ),
    key(StringKey.SubsActionFollowing) to mapOf(
        SupportedLanguageKey.En to "Subscribed",
        SupportedLanguageKey.En to "Вы подписаны",
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
    key(StringKey.CreateUserActionUploadPhoto) to mapOf(
        SupportedLanguageKey.En to "Upload a photo",
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

    key(StringKey.ReferralProgramTitleFootnoteMain) to mapOf(
        SupportedLanguageKey.En to "Referral program",
        SupportedLanguageKey.Ru to "Реферальная программа",
    ),

    key(StringKey.SearchVideosTitle) to mapOf(
        SupportedLanguageKey.En to "Videos",
    ),
    key(StringKey.SearchProfilesTitle) to mapOf(
        SupportedLanguageKey.En to "Profiles",
    ),
    key(StringKey.SearchHint) to mapOf(
        SupportedLanguageKey.En to "Search",
        SupportedLanguageKey.Ru to "Поиск",
    ),

    key(StringKey.ReferralProgramTitleSliderMain) to mapOf(
        SupportedLanguageKey.En to "Main",
        SupportedLanguageKey.Ru to "Главная",
    ),
    key(StringKey.ReferralProgramTitleSliderMyReferrals) to mapOf(
        SupportedLanguageKey.En to "My referrals",
        SupportedLanguageKey.Ru to "Мои рефералы",
    ),
    key(StringKey.ReferralProgramTitleFootnoteMain) to mapOf(
        SupportedLanguageKey.En to "Referral program",
        SupportedLanguageKey.Ru to "Реферальная программа",
    ),
    key(StringKey.ReferralProgramTitleFootnoteMyReferrals) to mapOf(
        SupportedLanguageKey.En to "Invited referrals",
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
        SupportedLanguageKey.En to "Direct referral - %s",
    ),
    key(StringKey.ReferralProgramMessageDirectReferral) to mapOf(
        SupportedLanguageKey.En to "You will receive %s of all rewards earned by friends",
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
    key(StringKey.ReferralProgramMessageReferralCodeCopied) to mapOf(
        SupportedLanguageKey.En to "Referral code copied!",
    ),
    key(StringKey.ReferralProgramMessageReferralLinkCopied) to mapOf(
        SupportedLanguageKey.En to "Referral link copied!",
    ),
    key(StringKey.ReferralProgramMessageFootnoteMain) to mapOf(
        SupportedLanguageKey.En to "Invite new users and earn even more rewards every day!",
    ),
    key(StringKey.ReferralProgramMessageFootnoteMyReferrals) to mapOf(
        SupportedLanguageKey.En to "Here you can see all invited referrals. View their activity, the rank and number of their NFTs, and their income growth through the referral program.",
    ),
    key(StringKey.ReferralProgramTitleNoReferrals) to mapOf(
        SupportedLanguageKey.En to "No referrals yet",
        SupportedLanguageKey.Ru to "У вас пока нет рефералов",
    ),
    key(StringKey.ReferralProgramMessageNoReferrals) to mapOf(
        SupportedLanguageKey.En to "You haven't invited any user yet.",
    ),
    key(StringKey.ReferralProgramDialogTitleFootnoteMain1) to mapOf(
        SupportedLanguageKey.En to "Share your referral code/link",
    ),
    key(StringKey.ReferralProgramDialogMessageFootnoteMain1) to mapOf(
        SupportedLanguageKey.En to "Use your referral code or referral link to invite friends to Snaps. The more referrals you have, the more you earn every day! For a direct referral, you get 5%% of the level of his rewards every day! For an indirect referral, you get 3%% of the level of his rewards every day!",
    ),
    key(StringKey.ReferralProgramDialogTitleFootnoteMain2) to mapOf(
        SupportedLanguageKey.En to "Get an increase in daily earnings",
    ),
    key(StringKey.ReferralProgramDialogMessageFootnoteMain2) to mapOf(
        SupportedLanguageKey.En to "As soon as you have an impressive number of referrals, you will receive a tangible increase in your daily income in addition to the increase from the earnings of the referrals themselves.",
    ),
    key(StringKey.ReferralProgramDialogActionFootnoteMain2) to mapOf(
        SupportedLanguageKey.En to "Referral program",
        SupportedLanguageKey.Ru to "Реферальная программа",
    ),
    key(StringKey.ReferralProgramDialogTitleFootnoteMyReferrals) to mapOf(
        SupportedLanguageKey.En to "Get a boost in rewards for referred friends.",
    ),
    key(StringKey.ReferralProgramDialogMessageFootnoteMyReferrals) to mapOf(
        SupportedLanguageKey.En to "Invite friends and get an increase in rewards daily. After achieving such results - your daily income will be increased",
    ),
    key(StringKey.ReferralProgramDialogMessageFootnoteMyReferralsDisclaimer) to mapOf(
        SupportedLanguageKey.En to "The increase in referral rewards is valid only for active referrals who log into Snaps every day and collect at least 60 energy points.",
    ),
    key(StringKey.ReferralProgramDialogTitleFootnoteMyReferralsLevel1) to mapOf(
        SupportedLanguageKey.En to "20 Invited Friends",
    ),
    key(StringKey.ReferralProgramDialogMessageFootnoteMyReferralsLevel1) to mapOf(
        SupportedLanguageKey.En to "Daily increase - 1%%",
    ),
    key(StringKey.ReferralProgramDialogTitleFootnoteMyReferralsLevel2) to mapOf(
        SupportedLanguageKey.En to "50 Invited Friends",
    ),
    key(StringKey.ReferralProgramDialogMessageFootnoteMyReferralsLevel2) to mapOf(
        SupportedLanguageKey.En to "Daily increase - 2%%",
    ),
    key(StringKey.ReferralProgramDialogTitleFootnoteMyReferralsLevel3) to mapOf(
        SupportedLanguageKey.En to "100 Invited Friends",
    ),
    key(StringKey.ReferralProgramDialogMessageFootnoteMyReferralsLevel3) to mapOf(
        SupportedLanguageKey.En to "Daily increase - 3%%",
    ),

    key(StringKey.SettingsTitle) to mapOf(
        SupportedLanguageKey.En to "Settings",
    ),
    key(StringKey.SettingsTitleWallet) to mapOf(
        SupportedLanguageKey.En to "Wallet",
        SupportedLanguageKey.Ru to "Кошелек",
    ),
    key(StringKey.SettingsTitleReferralProgram) to mapOf(
        SupportedLanguageKey.En to "Referral program",
        SupportedLanguageKey.Ru to "Реферальная программа",
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
    key(StringKey.SettingsDialogLogoutTitle) to mapOf(
        SupportedLanguageKey.En to "Are you sure you want to logout?",
    ),
    key(StringKey.SettingsDialogLogoutMessage) to mapOf(
        SupportedLanguageKey.En to "Please ensure you saved your wallet so you won't lose it after logout",
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
        SupportedLanguageKey.Ru to "Текущие",
    ),
    key(StringKey.TasksTitleSlideHistory) to mapOf(
        SupportedLanguageKey.En to "History",
        SupportedLanguageKey.Ru to "История",
    ),
    key(StringKey.TasksTitleFootnoteCurrent) to mapOf(
        SupportedLanguageKey.En to "Complete the task",
    ),
    key(StringKey.TasksTitleFootnoteHistory) to mapOf(
        SupportedLanguageKey.En to "History",
        SupportedLanguageKey.Ru to "История",
    ),
    key(StringKey.TasksTitleRemainingTime) to mapOf(
        SupportedLanguageKey.En to "Until the end of the round",
    ),
    key(StringKey.TaskFieldJobCompleted) to mapOf(
        SupportedLanguageKey.En to "Job completed successfully",
    ),
    key(StringKey.TasksMessageFootnoteCurrent) to mapOf(
        SupportedLanguageKey.En to "To get the maximum reward, you need to complete tasks and collect 100 energy points.",
    ),
    key(StringKey.TasksMessageFootnoteHistory) to mapOf(
        SupportedLanguageKey.En to "Here you can see the history of your tasks and your progress.",
    ),
    key(StringKey.TasksMessageTaskCounted) to mapOf(
        SupportedLanguageKey.En to "Assignment scored",
        SupportedLanguageKey.Ru to "Задание засчитано",
    ),
    key(StringKey.TasksMessageTaskInProgress) to mapOf(
        SupportedLanguageKey.En to "Assignment will be credited if completed in full",
        SupportedLanguageKey.Ru to "Задание будет засчитано, если выполнено полностью",
    ),
    key(StringKey.TasksTitleWatchVideo) to mapOf(
        SupportedLanguageKey.En to "View 20 video",
        SupportedLanguageKey.Ru to "Просмотреть 20 видео",
    ),
    key(StringKey.TasksMessageWatchVideo) to mapOf(
        SupportedLanguageKey.En to "View at least 20 videos from the feed",
        SupportedLanguageKey.Ru to "Просмотреть минимум 20 видео из ленты",
    ),
    key(StringKey.TasksTitleLike) to mapOf(
        SupportedLanguageKey.En to "Like",
        SupportedLanguageKey.Ru to "Поставить лайки",
    ),
    key(StringKey.TasksMessageLike) to mapOf(
        SupportedLanguageKey.En to "Like videos you like",
        SupportedLanguageKey.Ru to "Поставить лайки на понравившиеся ролики",
    ),
    key(StringKey.TasksTitlePublishVideo) to mapOf(
        SupportedLanguageKey.En to "Upload video",
        SupportedLanguageKey.Ru to "Выложить видео",
    ),
    key(StringKey.TasksMessagePublishVideo) to mapOf(
        SupportedLanguageKey.En to "Post a short video to your profile",
        SupportedLanguageKey.Ru to "Выложить короткое видео в свой профиль",
    ),
    key(StringKey.TasksTitleSubscribe) to mapOf(
        SupportedLanguageKey.En to "Follow Authors",
        SupportedLanguageKey.Ru to "Подписаться на авторов",
    ),
    key(StringKey.TasksMessageSubscribe) to mapOf(
        SupportedLanguageKey.En to "Follow your favorite authors",
        SupportedLanguageKey.Ru to "Подписаться на любимых авторов",
    ),
    key(StringKey.TasksTitleSocialPost) to mapOf(
        SupportedLanguageKey.En to "Talk about Snaps",
        SupportedLanguageKey.Ru to "Рассказать о Snaps",
    ),
    key(StringKey.TasksMessageSocialPost) to mapOf(
        SupportedLanguageKey.En to "Share Snaps on Instagram",
        SupportedLanguageKey.Ru to "Рассказать о Snaps в Instagram",
    ),
    key(StringKey.TasksMessageSocialPostNotPosted) to mapOf(
        SupportedLanguageKey.En to "Post not yet published",
        SupportedLanguageKey.Ru to "Пост еще не опубликован",
    ),
    key(StringKey.TasksMessageSocialPostNotSendToVerify) to mapOf(
        SupportedLanguageKey.En to "Post not send for verification",
    ),
    key(StringKey.TasksMessageSocialPostReview) to mapOf(
        SupportedLanguageKey.En to "Assignment for review",
        SupportedLanguageKey.Ru to "Задание на проверке",
    ),
    key(StringKey.TasksMessageSocialPostRejected) to mapOf(
        SupportedLanguageKey.En to "Assignment rejected",
        SupportedLanguageKey.Ru to "Задание отклонено",
    ),
    key(StringKey.TasksDialogTitleFootnote1) to mapOf(
        SupportedLanguageKey.En to "Energy",
    ),
    key(StringKey.TasksDialogMessageFootnote1) to mapOf(
        SupportedLanguageKey.En to "You have 24 hours to collect the maximum level of Energy by completing tasks before the end of the timer countdown. Opposite each task, you can see how many energy points you will receive for completing it. If you reach 100 energy points, then for this day you will receive the maximum the number of rewards in accordance with the rank of your NFT points. Every day when the timer is reset, the energy level is also reset. The next day, you need to collect energy points again.",
    ),
    key(StringKey.TasksDialogTitleFootnote2) to mapOf(
        SupportedLanguageKey.En to "Claim your reward",
    ),
    key(StringKey.TasksDialogMessageFootnote2) to mapOf(
        SupportedLanguageKey.En to "After the timer reaches 00:00, your rewards will be available to receive. Rewards will be awarded according to how many energy points you managed to collect in a day. If there were less than 60 energy points, then no rewards will be awarded for this day. Try to collect the maximum amount of energy (100).",
    ),
    key(StringKey.TasksDialogTitleFootnote3) to mapOf(
        SupportedLanguageKey.En to "Increase your income many times over",
    ),
    key(StringKey.TasksDialogMessageFootnote3) to mapOf(
        SupportedLanguageKey.En to "In order to earn more rewards, you need to upgrade your NFT points. With each completed task, you earn XP for your NFT points. The higher the level, the more rewards. Also, increase the rank of your NFT points in order to multiply number of rewards.Create more NFT points to increase the number of rewards.Also try to make your videos popular to get instant rewards for views/subscribers.",
    ),

    key(StringKey.TaskWatchVideoTitle) to mapOf(
        SupportedLanguageKey.En to "View 20 video",
        SupportedLanguageKey.Ru to "Просмотреть 20 видео",
    ),
    key(StringKey.TaskWatchVideoMessage) to mapOf(
        SupportedLanguageKey.En to "View at least 20 videos of the most popular videos. Attention retention must be at least 70 percent. If the video is 1 minute long, you must watch it for at least 42 seconds to successfully complete the task.",
        SupportedLanguageKey.Ru to "Просмотреть минимум %@ видео из вашей ленты коротких видео. Удержание внимания должно быть минимум 70 процентов. Если ролик длится 1 минуту, то для успешного выполнения задания, вы должны просмотреть его минимум 42 секунды.",
    ),

    key(StringKey.TaskLikeTitle) to mapOf(
        SupportedLanguageKey.En to "Like",
        SupportedLanguageKey.Ru to "Поставить лайки",
    ),
    key(StringKey.TaskLikeMessage) to mapOf(
        SupportedLanguageKey.En to "Give at least 10 likes to the videos you like from your short video feed. Likes are placed by double-tapping the screen, or by clicking on the heart icon.",
        SupportedLanguageKey.Ru to "Поставить минимум 10 лайков на понравившиеся Вам ролики из вашей ленты коротких видео. Лайки ставятся двойным тапом по экрану, либо же кликом на значок “сердечка”.",
    ),

    key(StringKey.TaskSubscribeTitle) to mapOf(
        SupportedLanguageKey.En to "Follow Authors",
        SupportedLanguageKey.Ru to "Подписаться на авторов",
    ),
    key(StringKey.TaskSubscribeMessage) to mapOf(
        SupportedLanguageKey.En to "Subscribe to the content creators like in Snaps. You need to follow at least 5 accounts to complete the task.",
        SupportedLanguageKey.Ru to "Подписаться на понравившихся контент криейтеров в Snaps. Для успешного выполнения задания необходимо подписаться по меньшей мере на 5 аккаунтов.",
    ),

    key(StringKey.TaskPublishVideoTitle) to mapOf(
        SupportedLanguageKey.En to "Upload Video",
        SupportedLanguageKey.Ru to "Выложить видео",
    ),
    key(StringKey.TaskPublishVideoMessage) to mapOf(
        SupportedLanguageKey.En to "Post a video between 5 and 60 seconds long to your Snaps profile. The video must not violate the platform's rules and regulations.",
        SupportedLanguageKey.Ru to "Выложить видео длиной от 5 до 60 секунд в свой профиль Snaps. Видео не должно нарушать правил и принципов платформы.",
    ),

    key(StringKey.TaskSocialPostTitle) to mapOf(
        SupportedLanguageKey.En to "Talk about Snaps",
        SupportedLanguageKey.Ru to "Рассказать о Snaps",
    ),
    key(StringKey.TaskSocialPostMessage) to mapOf(
        SupportedLanguageKey.En to "Post Insta Stories about your Snaps account and the project itself. You don't need to come up with anything! You can use a ready-made template and immediately post it to your Instagram account. The task will be reviewed from 3 to 6 hours.",
        SupportedLanguageKey.Ru to "Выложить Insta Stories о своем аккаунте в Snaps и о самом проекте. Придумывать ничего не нужно! Можно использовать готовый шаблон и сразу же его выложить в свой Instagram аккаунт. Задание будет проверяться от 3 до 6 часов.",
    ),

    key(StringKey.TaskSocialShareTitle) to mapOf(
        SupportedLanguageKey.En to "Share your video with friends",
        SupportedLanguageKey.Ru to "Поделиться своим видео с друзьями",
    ),
    key(StringKey.TaskSocialShareMessage) to mapOf(
        SupportedLanguageKey.En to "Share your Snaps video with your friends via social network. You must click the share button on your video, and send it to your friends via social network. You must share with at least three friends.",
        SupportedLanguageKey.Ru to "Поделиться своим видео в Snaps с друзьями через социальную сеть. Нужно нажать кнопку “поделиться” на своем видео, и отправить друзьям через социальную сеть. Нужно поделиться минимум с тремя друзьями.",
    ),

    key(StringKey.TaskShareTitle) to mapOf(
        SupportedLanguageKey.En to "Share Template",
    ),
    key(StringKey.TaskShareTitleConnectInstagram) to mapOf(
        SupportedLanguageKey.En to "Connect Instagram",
    ),
    key(StringKey.TaskShareActionConnect) to mapOf(
        SupportedLanguageKey.En to "Connect",
    ),
    key(StringKey.TaskShareActionSavePhoto) to mapOf(
        SupportedLanguageKey.En to "Save photo to camera roll",
    ),
    key(StringKey.TaskShareActionPostToInstagram) to mapOf(
        SupportedLanguageKey.En to "I made a post in Instagram",
    ),
    key(StringKey.TaskShareMessagePhotoSaved) to mapOf(
        SupportedLanguageKey.En to "Photo saved",
    ),
    key(StringKey.TaskShareFieldDownloadApp) to mapOf(
        SupportedLanguageKey.En to "Download the app Snaps",
    ),
    key(StringKey.TaskShareFieldEarnCryptocurrencies) to mapOf(
        SupportedLanguageKey.En to "I play Snaps and\u00A0earn cryptocurrencies",
    ),
    key(StringKey.TaskShareMessagePostInstagram) to mapOf(
        SupportedLanguageKey.En to "Task is on review",
        SupportedLanguageKey.Ru to "Задание на проверке",
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
    key(StringKey.TaskFindPointsTitleSponsor) to mapOf(
        SupportedLanguageKey.En to "Sponsor",
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
        SupportedLanguageKey.Ru to "Кошелек",
    ),
    key(StringKey.WalletTitlePaymentStatus) to mapOf(
        SupportedLanguageKey.En to "Payment status:",
    ),
    key(StringKey.WalletFieldPaymentStatusInProcess) to mapOf(
        SupportedLanguageKey.En to "In progress",
    ),
    key(StringKey.WalletFieldPaymentStatusRejected) to mapOf(
        SupportedLanguageKey.En to "In progress",
    ),
    key(StringKey.WalletTitlePaymentTransactionId) to mapOf(
        SupportedLanguageKey.En to "Transaction Id:",
    ),
    key(StringKey.WalletMessagePaymentRejected) to mapOf(
        SupportedLanguageKey.En to "An error occurred because\nno such card was found.",
    ),
    key(StringKey.WalletActionPaymentRejected) to mapOf(
        SupportedLanguageKey.En to "Contact support",
    ),
    key(StringKey.WalletFieldTotal) to mapOf(
        SupportedLanguageKey.En to "Total",
    ),
    key(StringKey.WalletActionTopUp) to mapOf(
        SupportedLanguageKey.En to "Receive",
    ),
    key(StringKey.WalletActionWithdraw) to mapOf(
        SupportedLanguageKey.En to "Send",
    ),
    key(StringKey.WalletActionExchange) to mapOf(
        SupportedLanguageKey.En to "Trade",
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
    key(StringKey.WalletMessageAddressCopied) to mapOf(
        SupportedLanguageKey.En to "Address copied!",
    ),
    key(StringKey.WalletDialogTitleTopUp) to mapOf(
        SupportedLanguageKey.En to "Top up %s",
    ),
    key(StringKey.WalletTitleTransactionsEmpty) to mapOf(
        SupportedLanguageKey.En to "No transactions yet",
    ),
    key(StringKey.WalletMessageTransactionsEmpty) to mapOf(
        SupportedLanguageKey.En to "Make sure you are sending your tokens to the correct address and the correct BEP-20 network",
    ),
    key(StringKey.WalletTitleName) to mapOf(
        SupportedLanguageKey.En to "Name",
    ),
    key(StringKey.WalletTitleQuantity) to mapOf(
        SupportedLanguageKey.En to "Quantity",
    ),
    key(StringKey.WalletTitleDateTransfer) to mapOf(
        SupportedLanguageKey.En to "Date of transfer",
    ),

    key(StringKey.RewardsTitle) to mapOf(
        SupportedLanguageKey.En to "Rewards",
        SupportedLanguageKey.Ru to "Награды",
    ),
    key(StringKey.RewardsTitleFootnote) to mapOf(
        SupportedLanguageKey.En to "Get Rewards",
    ),
    key(StringKey.RewardsTitleAvailableRewards) to mapOf(
        SupportedLanguageKey.En to "Available Rewards",
    ),
    key(StringKey.RewardsTitleLockedRewards) to mapOf(
        SupportedLanguageKey.En to "Locked Rewards",
    ),
    key(StringKey.RewardsTitleHistory) to mapOf(
        SupportedLanguageKey.En to "History",
        SupportedLanguageKey.Ru to "История",
    ),
    key(StringKey.RewardsMessageFootnote) to mapOf(
        SupportedLanguageKey.En to "Get rewards from your unlocked wallet right now. And from the blocked every day a little later.",
    ),
    key(StringKey.RewardsMessageAvailableRewards) to mapOf(
        SupportedLanguageKey.En to "Awards convertible to SNPS",
    ),
    key(StringKey.RewardsMessageLockedRewards) to mapOf(
        SupportedLanguageKey.En to "Rewards unlocked tomorrow",
    ),
    key(StringKey.RewardsActionClaim) to mapOf(
        SupportedLanguageKey.En to "Claim",
    ),
    key(StringKey.RewardsErrorInsufficientBalance) to mapOf(
        SupportedLanguageKey.En to "Token balance is zero",
    ),
    key(StringKey.RewardsErrorRepairGlasses) to mapOf(
        SupportedLanguageKey.En to "To claim the tokens please fix all the glasses",
        SupportedLanguageKey.Ru to "Чтобы заклеймить токены пожалуйста почините все очки",
    ),
    key(StringKey.RewardsFieldFilterLocked) to mapOf(
        SupportedLanguageKey.En to "Locked",
    ),
    key(StringKey.RewardsFieldFilterUnlocked) to mapOf(
        SupportedLanguageKey.En to "Unlocked",
    ),
    key(StringKey.RewardsDialogTitleFootnote1) to mapOf(
        SupportedLanguageKey.En to "Unlocked Rewards",
    ),
    key(StringKey.RewardsDialogTitleFootnote2) to mapOf(
        SupportedLanguageKey.En to "Locked Rewards",
    ),
    key(StringKey.RewardsDialogMessageFootnote1) to mapOf(
        SupportedLanguageKey.En to "Every day, for completing tasks, you will receive rewards. According to the rank of your NFT points, some of the rewards are available for payment immediately and it is displayed in the 'Unlocked Wallet' (in blue). Press the 'Claim' button and receive rewards on your wallet.",
    ),
    key(StringKey.RewardsDialogMessageFootnote2) to mapOf(
        SupportedLanguageKey.En to "According to the rank of your NFT points, you will receive rewards with a smooth and gradual unlocking. Every day, the rewards from your blocked account will go to the unlocked one and you can pick them up. Every day the unlock gets bigger and after a few days in Snaps you will start getting 100%% reward every day on your unlocked wallet.",
    ),
    key(StringKey.RewardsDialogTitleClaim) to mapOf(
        SupportedLanguageKey.En to "Claim tokens",
        SupportedLanguageKey.Ru to "Вывести токены",
    ),
    key(StringKey.RewardsDialogActionClaim) to mapOf(
        SupportedLanguageKey.En to "Confirm",
        SupportedLanguageKey.Ru to "Подтвердить",
    ),
    key(StringKey.RewardsDialogHintClaim) to mapOf(
        SupportedLanguageKey.En to "Enter amount to claim",
        SupportedLanguageKey.Ru to "Введите сумму вывода",
    ),
    key(StringKey.RewardsDialogActionMax) to mapOf(
        SupportedLanguageKey.En to "Max",
        SupportedLanguageKey.Ru to "Mакс",
    ),
    key(StringKey.RewardsDialogFieldAvailable) to mapOf(
        SupportedLanguageKey.En to "Available: %s SNAPS",
        SupportedLanguageKey.Ru to "Доступно: %s SNAPS",
    ),
    key(StringKey.RewardsDialogRepairNftTitle) to mapOf(
        SupportedLanguageKey.En to "Need to repair glasses first",
        SupportedLanguageKey.Ru to "Необходим ремонт очков",
    ),
    key(StringKey.RewardsDialogRepairNftText) to mapOf(
        SupportedLanguageKey.En to "To claim the tokens please repair all glasses",
        SupportedLanguageKey.Ru to "Чтобы заклеймить токены пожалуйста почините все очки",
    ),
    key(StringKey.RewardsDialogRepairNftAction) to mapOf(
        SupportedLanguageKey.En to "Repair glasses",
        SupportedLanguageKey.Ru to "Починить очки",
    ),

    key(StringKey.WithdrawTitle) to mapOf(
        SupportedLanguageKey.En to "Withdraw %s",
    ),
    key(StringKey.WithdrawHintAddress) to mapOf(
        SupportedLanguageKey.En to "Enter address",
    ),
    key(StringKey.WithdrawHintAmount) to mapOf(
        SupportedLanguageKey.En to "Enter amount",
    ),
    key(StringKey.WithdrawActionMax) to mapOf(
        SupportedLanguageKey.En to "Max",
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
    key(StringKey.WithdrawActionSendTransaction) to mapOf(
        SupportedLanguageKey.En to "Send transaction",
    ),
    key(StringKey.WithdrawErrorInvalidAddress) to mapOf(
        SupportedLanguageKey.En to "Invalid address",
    ),
    key(StringKey.WithdrawDialogWithdrawSuccessTitle) to mapOf(
        SupportedLanguageKey.En to "Transaction succeeded",
    ),
    key(StringKey.WithdrawDialogWithdrawSuccessMessage) to mapOf(
        SupportedLanguageKey.En to "Sent %s to %s", // amount to address
    ),
    key(StringKey.WithdrawDialogWithdrawSuccessAction) to mapOf(
        SupportedLanguageKey.En to "View on Bscscan",
    ),

    key(StringKey.ExchangeTitle) to mapOf(
        SupportedLanguageKey.En to "Trade",
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

    key(StringKey.MyCollectionTitle) to mapOf(
        SupportedLanguageKey.En to "My Collection",
    ),
    key(StringKey.MyCollectionMessage) to mapOf(
        SupportedLanguageKey.En to "All your inventory is displayed here",
    ),
    key(StringKey.MyCollectionTitleSlideNft) to mapOf(
        SupportedLanguageKey.En to "Nft",
    ),
    key(StringKey.MyCollectionTitleSlideMysteryBox) to mapOf(
        SupportedLanguageKey.En to "Mystery Box",
    ),
    key(StringKey.MyCollectionTitleMysteryBox) to mapOf(
        SupportedLanguageKey.En to "Mystery Box",
    ),
    key(StringKey.MyCollectionFieldNeedToRepair) to mapOf(
        SupportedLanguageKey.En to "Need to repair",
    ),
    key(StringKey.MyCollectionActionRepairGlasses) to mapOf(
        SupportedLanguageKey.En to "Repair Glasses",
    ),
    key(StringKey.MyCollectionActionProcessing) to mapOf(
        SupportedLanguageKey.En to "Processing",
    ),
    key(StringKey.MyCollectionFieldBonus) to mapOf(
        SupportedLanguageKey.Ru to "+ %s к наградам",
        SupportedLanguageKey.En to "+ %s to rewards",
    ),
    key(StringKey.MyCollectionFieldLevel) to mapOf(
        SupportedLanguageKey.Ru to "УРОВЕНЬ\u00A0%s",
        SupportedLanguageKey.En to "LEVEL\u00A0%s ",
    ),
    key(StringKey.MyCollectionFieldUpperThreshold) to mapOf(
        SupportedLanguageKey.Ru to "%s опыта до %s",
        SupportedLanguageKey.En to "%s experience up to %s",
    ),
    key(StringKey.MyCollectionFieldExperience, PluralKey.Zero) to mapOf(
        SupportedLanguageKey.Ru to "%s очков опыта",
        SupportedLanguageKey.En to "%s experience points",
    ),
    key(StringKey.MyCollectionFieldExperience, PluralKey.One) to mapOf(
        SupportedLanguageKey.Ru to "%s очко опыта",
        SupportedLanguageKey.En to "%s experience point",
    ),
    key(StringKey.MyCollectionFieldExperience, PluralKey.Two) to mapOf(
        SupportedLanguageKey.Ru to "%s очка опыта",
        SupportedLanguageKey.En to "%s experience points",
    ),
    key(StringKey.MyCollectionFieldExperience, PluralKey.Few) to mapOf(
        SupportedLanguageKey.Ru to "%s очка опыта",
        SupportedLanguageKey.En to "%s experience points",
    ),
    key(StringKey.MyCollectionFieldExperience, PluralKey.Many) to mapOf(
        SupportedLanguageKey.Ru to "%s очков опыта",
        SupportedLanguageKey.En to "%s experience points",
    ),
    key(StringKey.MyCollectionFieldExperience, PluralKey.Other) to mapOf(
        SupportedLanguageKey.Ru to "%s очков опыта",
        SupportedLanguageKey.En to "%s experience points",
    ),
    key(StringKey.MyCollectionDialogRepairSuccessTitle) to mapOf(
        SupportedLanguageKey.En to "Repair succeeded",
    ),
    key(StringKey.MyCollectionDialogRepairSuccessAction) to mapOf(
        SupportedLanguageKey.En to "View on Bscscan",
    ),

    key(StringKey.DialogLimitedGasTitle) to mapOf(
        SupportedLanguageKey.Ru to "На вашем счете недостаточно газа",
        SupportedLanguageKey.En to "There is not enough gas on your account",
    ),
    key(StringKey.DialogLimitedGasMessage) to mapOf(
        SupportedLanguageKey.Ru to "SNAPS заботится о своих пользователях и\nпополняет им газ автоматически",
        SupportedLanguageKey.En to "SNAPS takes care of its users and\nrefills their gas automatically",
    ),
    key(StringKey.DialogLimitedGasAction) to mapOf(
        SupportedLanguageKey.Ru to "Пополнить",
        SupportedLanguageKey.En to "Refill",
    ),

    key(StringKey.RankSelectionTitle) to mapOf(
        SupportedLanguageKey.En to "Choose a rank for your glasses",
    ),
    key(StringKey.RankSelectionActionFootnote) to mapOf(
        SupportedLanguageKey.En to "What is a rank",
    ),
    key(StringKey.RankSelectionTitleDailyReward) to mapOf(
        SupportedLanguageKey.En to "Daily reward",
        SupportedLanguageKey.Ru to "Ежедневная награда",
    ),
    key(StringKey.RankSelectionTitleDailyUnlock) to mapOf(
        SupportedLanguageKey.En to "Daily unlock",
        SupportedLanguageKey.Ru to "Ежедневная разблокировка",
    ),
    key(StringKey.RankSelectionTitleDailyConsumption) to mapOf(
        SupportedLanguageKey.En to "Daily consumption",
    ),
    key(StringKey.RankSelectionMessageNotAvailable) to mapOf(
        SupportedLanguageKey.En to "Not available for purchase",
    ),
    key(StringKey.RankSelectionDialogTitleFootnote1) to mapOf(
        SupportedLanguageKey.En to "My glasses",
        SupportedLanguageKey.Ru to "Мои очки",
    ),
    key(StringKey.RankSelectionDialogMessageFootnote1) to mapOf(
        SupportedLanguageKey.En to "All your NFT points are stored here. Free rank NFT points are enough to get started. This will allow you to get your first rewards and try yourself in the Snaps ecosystem!",
    ),
    key(StringKey.RankSelectionDialogActionFootnote1) to mapOf(
        SupportedLanguageKey.En to "Raise NFT Rank",
    ),

    key(StringKey.CreateVideoActionGrantPerms) to mapOf(
        SupportedLanguageKey.En to "Grant permissions",
    ),
    key(StringKey.CreateVideoActionFlip) to mapOf(
        SupportedLanguageKey.En to "Flip",
    ),
    key(StringKey.CreateVideoActionChoose) to mapOf(
        SupportedLanguageKey.En to "Choose",
    ),
    key(StringKey.CreateVideoMessageDurationLimit) to mapOf(
        SupportedLanguageKey.En to "The maximum video duration is 2 minutes!",
    ),

    key(StringKey.PreviewVideoActionDiscard) to mapOf(
        SupportedLanguageKey.En to "Discard",
    ),
    key(StringKey.PreviewVideoActionProceed) to mapOf(
        SupportedLanguageKey.En to "Proceed",
    ),
    key(StringKey.PreviewVideoMessageSuccess) to mapOf(
        SupportedLanguageKey.En to "Video uploaded successfully",
    ),

    key(StringKey.UploadVideoTitle) to mapOf(
        SupportedLanguageKey.En to "Upload",
    ),
    key(StringKey.UploadVideoTitlePreview) to mapOf(
        SupportedLanguageKey.En to "Select video preview",
    ),
    key(StringKey.UploadVideoHintTitle) to mapOf(
        SupportedLanguageKey.En to "Enter video title",
    ),
    key(StringKey.UploadVideoHintDescription) to mapOf(
        SupportedLanguageKey.En to "Enter video description",
    ),
    key(StringKey.UploadVideoActionPublish) to mapOf(
        SupportedLanguageKey.En to "Publish",
    ),

    key(StringKey.PurchaseTitle) to mapOf(
        SupportedLanguageKey.En to "Buy",
    ),
    key(StringKey.PurchaseActionBuyInStore) to mapOf(
        SupportedLanguageKey.En to "Purchase",
    ),
    key(StringKey.PurchaseActionBuyWithBNB) to mapOf(
        SupportedLanguageKey.En to "Buy with BNB",
    ),
    key(StringKey.PurchaseActionFree) to mapOf(
        SupportedLanguageKey.En to "Free",
    ),
    key(StringKey.PurchaseFieldOff) to mapOf(
        SupportedLanguageKey.En to "30%% off",
    ),
    key(StringKey.PurchaseTitlePrice) to mapOf(
        SupportedLanguageKey.En to "Price:",
    ),
    key(StringKey.PurchaseTitleNotAvailable) to mapOf(
        SupportedLanguageKey.En to "Rank %s is not available for mint",
    ),
    key(StringKey.PurchaseTitleDailyCosts) to mapOf(
        SupportedLanguageKey.En to "Daily costs",
    ),
    key(StringKey.PurchaseMessageDailyCosts) to mapOf(
        SupportedLanguageKey.En to "Daily consumption of your NFT points. Every NFT point, except for the FREE rank, deteriorates every day and needs to be restored in order to continue further use and generate income.",
    ),
    key(StringKey.PurchaseTitleDailyReward) to mapOf(
        SupportedLanguageKey.En to "Daily Reward\n%s SNPS",
        SupportedLanguageKey.Ru to "Ежедневная награда\n%s SNPS",
    ),
    key(StringKey.PurchaseDescriptionDailyReward) to mapOf(
        SupportedLanguageKey.En to "Estimated daily reward in tokens when completing tasks",
        SupportedLanguageKey.Ru to "Предполагаемое ежедневное вознаграждение в токенах при выполнени заданий",
    ),
    key(StringKey.PurchaseMessageDailyReward) to mapOf(
        SupportedLanguageKey.En to "This cost is only an estimate of rank and may change",
        SupportedLanguageKey.Ru to "Данная стоимость является лишь оценкой ранга и может меняться",
    ),
    key(StringKey.PurchaseTitleDailyUnlock) to mapOf(
        SupportedLanguageKey.En to "Daily Unlock\n%s",
        SupportedLanguageKey.Ru to "Ежедневная разблокировка\n%s",
    ),
    key(StringKey.PurchaseDescriptionDailyUnlock) to mapOf(
        SupportedLanguageKey.En to "You can transfer %s of the available SNP rewards to the main wallet. After receiving the tokens, they can be exchanged or withdrawn",
        SupportedLanguageKey.Ru to "Вы можете перевести %s доступных вознагрождений SNP на основной кошелек. После получения токенов их можно обменять или вывести",
    ),
    key(StringKey.PurchaseMessageDailyUnlock) to mapOf(
        SupportedLanguageKey.En to "The rest of the SNAPS tokens are frozen and stored in a locked wallet",
        SupportedLanguageKey.Ru to "Остальные токены SNAPS заморожены и хранятся в заблокированном кошельке",
    ),
    key(StringKey.PurchaseTitleRank) to mapOf(
        SupportedLanguageKey.En to "Rank %s",
    ),
    key(StringKey.PurchaseFieldLevel) to mapOf(
        SupportedLanguageKey.En to "%s LVL",
    ),
    key(StringKey.PurchaseMessageSuccess) to mapOf(
        SupportedLanguageKey.En to "Purchase successful",
    ),
    key(StringKey.PurchaseDialogWithBnbTitle) to mapOf(
        SupportedLanguageKey.En to "%s NFT Minting",
    ),
    key(StringKey.PurchaseErrorNotEnoughBnb) to mapOf(
        SupportedLanguageKey.En to "Not enough BNB to mint",
    ),
    key(StringKey.PurchaseDialogWithBnbSuccessTitle) to mapOf(
        SupportedLanguageKey.En to "Transaction succeeded",
    ),
    key(StringKey.PurchaseDialogWithBnbSuccessMessage) to mapOf(
        SupportedLanguageKey.En to "NFT will appear in your collection soon",
    ),
    key(StringKey.PurchaseDialogWithBnbSuccessAction) to mapOf(
        SupportedLanguageKey.En to "View on Bscscan",
    ),

    key(StringKey.VideoClipActionDelete) to mapOf(
        SupportedLanguageKey.En to "Delete video",
        SupportedLanguageKey.Ru to "Удалить видео",
    ),
    key(StringKey.VideoClipTitleAction) to mapOf(
        SupportedLanguageKey.Ru to "Действия по видео",
        SupportedLanguageKey.En to "Video actions",
    ),
    key(StringKey.VideoClipDialogConfirmDeleteMessage) to mapOf(
        SupportedLanguageKey.Ru to "Вы уверены, что хотите удалить видео?",
        SupportedLanguageKey.En to "Are you sure you want to delete video?",
    ),

    key(StringKey.NftDetailsDescriptionDailyReward) to mapOf(
        SupportedLanguageKey.En to "Rank is the main characteristic of NFT points. Higher ranks allow you to get a multiple of more rewards and have a higher unlock speed",
    ),
    key(StringKey.NftDetailsTitleEarnings) to mapOf(
        SupportedLanguageKey.En to "Earnings",
    ),
    key(StringKey.NftDetailsDescriptionEarnings) to mapOf(
        SupportedLanguageKey.En to "Daily Reward\n%s SNPS",
    ),
    key(StringKey.NftDetailsTitleCondition) to mapOf(
        SupportedLanguageKey.En to "Condition",
    ),
    key(StringKey.NftDetailsDescriptionCondition) to mapOf(
        SupportedLanguageKey.En to "Make sure that your NFT glasses are always in excellent condition. If you do not repair NFT glasses for more than 3 days, they will break.",
    ),
    key(StringKey.NftDetailsTitleLevel) to mapOf(
        SupportedLanguageKey.En to "Level and XP",
    ),
    key(StringKey.NftDetailsDescriptionLevel) to mapOf(
        SupportedLanguageKey.En to "Earn XP for completing tasks. For each task you get 25 XP. With each level, your NFT points will bring more income. And at level 10, you can go to the next rank.",
    ),

    key(StringKey.MainVideoFeedTitleForYou) to mapOf(
        SupportedLanguageKey.En to "For You",
        SupportedLanguageKey.Ru to "Для Вас",
    ),
    key(StringKey.MainVideoFeedTitleSubscriptions) to mapOf(
        SupportedLanguageKey.En to "Subscriptions",
        SupportedLanguageKey.Ru to "Подписки",
    ),
)