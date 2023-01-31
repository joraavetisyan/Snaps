package com.defince.featureregistration.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.defince.corenavigation.AppRoute
import com.defince.corenavigation.RegistrationFeatureProvider
import com.defince.corenavigation.base.Navigator
import com.defince.corenavigation.base.composable
import com.defince.featureregistration.presentation.screen.RegistrationScreen
import javax.inject.Inject

internal class ScreenNavigator(navHostController: NavHostController) : Navigator(navHostController)

class RegistrationFeatureProviderImpl @Inject constructor() : RegistrationFeatureProvider {
    override fun NavGraphBuilder.registrationGraph(controller: NavHostController) {
        composable(AppRoute.Registration) { RegistrationScreen(controller) }
    }
}