# Snaps project

## Configuration
В local.properties добавь два поля:
+ gpr.user=твой ник в гитхабе
+ gpr.key=авториз. токен (добавь в гитхабе в настройках https://github.com/settings/tokens токен (можно непротухающий) и с правами read:packages) - это нужно для библы trust wallet core

## About
Project with functional modules on the next stack:
+ kotlin, coroutines
+ mvvm, hilt, compose, navigation component
+ retrofit 2, kotlinx-serialization, coil, room
+ junit, espresso

## Has the following features

#### 1. Single Activity with Navigation Component
There is the single AppActivity, which contains navigation graph with nested feature graph from feature modules.
BottomBarFragment from feature-bottom-bar is responsible for working with one or more stacks of fragments, 
it can be launched by BottomBarFeatureProvider.

#### 2. Custom views and screens and resources for light/night mode
The project contains custom elements and screen templates (OnBoardingScreen, BottomBarScreen). 

#### 3. Feature Toggle
Based on FirebaseRemoteConfig
+ FeatureToggleUpdater for receiving flags at application startup
+ Feature stores available flags with default values
+ FeatureToggle check flag status

#### 4. Billing
Based on Billing 4.0. SimpleBilling encapsulates the interaction with the library and is responsible for working with purchases.

#### 5. Analytics
Tracker implementation can work with one system for analytics, or with a group of systems. 
The project has an example wrapper for FirebaseAnalytics (FirebaseTracker). 
Access to the Tracker object is through AnalyticsTrackerHolder.

#### 6. A few more tools
+ AppUpdateProvider
+ LocationProvider
+ NetworkStateProvider
+ PreferencesProvider
+ FirebaseNotificationsService
+ BuildInfo (contains info of current build)
+ Resources containers (ColorValue, ImageValue, TextValue)
+ Effect (like Result in Kotlin for results of operations)
+ CacheProvider (with two implementations)
+ ApiConfig (for build retrofit instances)
+ apiCall(...) and cachedApiCall(...) (for network requests)
+ NotificationHelper
+ Navigation extensions (in nav_ext.kt)
+ and many other extensions for some routine

#### Naming
+ Provider/Source - a Source provide data in a stream, a Provider through getters

# Нейминг строк
Схема нейминга для строк-элементов ui: местоТипНазвание
- место: название экрана (если общее - опускается) + диалог, bottom sheet и т.п (screenName_, screenNameDialogName_, screenNameBottomSheetName_)
- тип:
    1. title - название экрана, заголовки полей и т.п
    2. message - сообщение
    3. hint - подсказки (в тесктовых полях, под полем)
    4. field - какое-то конкретное значение (напр.: значения в списке, названия валют)
    5. action - названия кнопок и т.п
    6. error - ошибка
- название: название строки (если единственное такого типа на экране - можно опустить; также можно опустить, если это название экрана)

Пример:
- RegistrationMessagePrivacyPolicy
- RegistrationActionPrivacyPolicy
- WalletErrorInsufficientBalance
- WalletDialogTitleTopUp