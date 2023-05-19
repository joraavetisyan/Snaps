package io.snaps.coredata.di

import javax.inject.Qualifier

/**
 * To bridge from SingletonComponent to custom component (eg. [UserSessionComponent])
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Bridged