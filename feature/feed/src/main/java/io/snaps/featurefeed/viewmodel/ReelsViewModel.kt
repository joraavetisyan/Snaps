package io.snaps.featurefeed.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import io.snaps.coredata.network.Action
import io.snaps.coreui.viewmodel.SimpleViewModel
import javax.inject.Inject

@HiltViewModel
class ReelsViewModel @Inject constructor(
    private val action: Action,
) : SimpleViewModel()