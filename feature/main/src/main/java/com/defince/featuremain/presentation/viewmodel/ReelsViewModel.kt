package com.defince.featuremain.presentation.viewmodel

import com.defince.coredata.network.Action
import com.defince.coreui.viewmodel.SimpleViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReelsViewModel @Inject constructor(
    private val action: Action,
) : SimpleViewModel()