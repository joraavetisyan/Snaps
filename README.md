# Snaps project

## Configuration

Add two fields to local.properties:

+ gpr.user=your github nickname
+ gpr.key=your authorization token (add it in github settings https://github.com/settings/tokens (
  can be expiring) and with the `read:packages` rights) - this is necessary for the trust wallet
  core library to fetch

## About

Project with functional modules on the next stack:

+ kotlin, coroutines
+ mvvm, hilt, compose, navigation component
+ retrofit 2, kotlinx-serialization, coil, room
+ junit, espresso

## Has the following features

#### 1. Single Activity with Navigation Component

There is a single AppActivity, which contains navigation graph with nested feature graph from
feature modules.
BottomBarFragment from feature-bottom-bar is responsible for working with one or more stacks of
fragments,
it can be launched by BottomBarFeatureProvider.

#### 2. Custom views and screens and resources for light/night mode

The project contains custom elements and screen templates (OnBoardingScreen, BottomBarScreen).

#### 3. Feature Toggle

Based on FirebaseRemoteConfig

+ FeatureToggleUpdater for receiving flags at application startup
+ Feature stores available flags with default values
+ FeatureToggle check flag status

#### 4. Billing

Based on Billing 4.0. SimpleBilling encapsulates the interaction with the library and is responsible
for working with purchases.

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

#### Dependency injection

UserSessionScope and UserSessionComponent are used to inject user session related
classes (eg repos), so that the user data gets cleared when a new user logs in. 
Those classes provided with @Bridged qualifier to avoid boilerplate code

#### Naming conventions

+ Provider/Source - a Source provide data in a stream, a Provider through getters

#### Hiding bottom navigation bar on bottom sheet dialog shown

- Use ModalBottomSheetTargetStateListener in the screen, BottomDialogBarVisibilityHandler in the
  view model

#### Strings naming

Naming scheme for string ui elements: `placeTypeName`

- `place`: screen name (if common - omitted) + dialog, bottom sheet, etc. (screenName_,
  screenNameDialogName_, screenNameBottomSheetName_)
- `type`:
    1. title - screen title, field titles, etc.
    2. message - messages
    3. hint - hints (in text fields, below the field)
    4. field - some specific values (for example: values in the list, names of currencies)
    5. action - button names, etc.
    6. error - errors
- `name`: name of the string (if there is only one of this type on the screen - you can omit it; you
  can also omit it if it's a screen title)

Examples:

- RegistrationMessagePrivacyPolicy
- RegistrationActionPrivacyPolicy
- WalletErrorInsufficientBalance
- WalletDialogTitleTopUp