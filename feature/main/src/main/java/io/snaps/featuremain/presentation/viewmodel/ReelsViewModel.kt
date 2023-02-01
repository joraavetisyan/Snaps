package io.snaps.featuremain.presentation.viewmodel

import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReelsViewModel @Inject constructor(
    private val action: Action,
) : SimpleViewModel()