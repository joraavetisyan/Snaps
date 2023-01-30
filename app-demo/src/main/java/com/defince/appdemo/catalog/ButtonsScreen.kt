package com.defince.appdemo.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.defince.corecommon.container.textValue
import com.defince.coreuicompose.tools.add
import com.defince.coreuicompose.tools.insetAllExcludeTop
import com.defince.coreuicompose.uikit.button.SimpleButtonActionL
import com.defince.coreuicompose.uikit.button.SimpleButtonActionM
import com.defince.coreuicompose.uikit.button.SimpleButtonActionS
import com.defince.coreuicompose.uikit.button.SimpleButtonContent
import com.defince.coreuicompose.uikit.button.SimpleButtonDefaultL
import com.defince.coreuicompose.uikit.button.SimpleButtonDefaultM
import com.defince.coreuicompose.uikit.button.SimpleButtonDefaultS
import com.defince.coreuicompose.uikit.button.SimpleButtonGreyL
import com.defince.coreuicompose.uikit.button.SimpleButtonGreyM
import com.defince.coreuicompose.uikit.button.SimpleButtonGreyS
import com.defince.coreuicompose.uikit.button.SimpleButtonInlineL
import com.defince.coreuicompose.uikit.button.SimpleButtonInlineM
import com.defince.coreuicompose.uikit.button.SimpleButtonInlineS
import com.defince.coreuicompose.uikit.button.SimpleButtonLightL
import com.defince.coreuicompose.uikit.button.SimpleButtonLightM
import com.defince.coreuicompose.uikit.button.SimpleButtonLightS
import com.defince.coreuicompose.uikit.button.SimpleButtonOutlineL
import com.defince.coreuicompose.uikit.button.SimpleButtonOutlineM
import com.defince.coreuicompose.uikit.button.SimpleButtonOutlineS
import com.defince.coreuicompose.uikit.duplicate.SimpleTopAppBar
import com.defince.coreuitheme.compose.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButtonsScreen(navController: NavHostController) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SimpleTopAppBar(
                title = "Buttons".textValue(),
                navigationIcon = AppTheme.specificIcons.back to navController::popBackStack,
                scrollBehavior = scrollBehavior,
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            contentPadding = insetAllExcludeTop().asPaddingValues().add(horizontal = 24.dp).add(it),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item { Actions() }
            item { Defaults() }
            item { Lights() }
            item { Outlines() }
            item { Inlines() }
            item { Greys() }
        }
    }
}

@Composable
private fun Actions() {
    val icon = AppTheme.specificIcons.add
    Header("Action")
    Container {
        SimpleButtonActionS(onClick = {}) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonActionS(onClick = {}, enabled = false) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonActionS(onClick = {}) {
            SimpleButtonContent("Label".textValue(), iconRight = icon)
        }
        SimpleButtonActionS(onClick = {}) {
            SimpleButtonContent(null, icon)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Container {
        SimpleButtonActionM(onClick = {}) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonActionM(onClick = {}, enabled = false) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonActionM(onClick = {}) {
            SimpleButtonContent("Label".textValue(), iconRight = icon)
        }
        SimpleButtonActionM(onClick = {}) {
            SimpleButtonContent(null, icon)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Container {
        SimpleButtonActionL(onClick = {}) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonActionL(onClick = {}, enabled = false) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonActionL(onClick = {}) {
            SimpleButtonContent("Label".textValue(), iconRight = icon)
        }
        SimpleButtonActionL(onClick = {}) {
            SimpleButtonContent(null, icon)
        }
    }
}

@Composable
private fun Defaults() {
    val icon = AppTheme.specificIcons.add
    Header("Default")
    Container {
        SimpleButtonDefaultS(onClick = {}) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonDefaultS(onClick = {}, enabled = false) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonDefaultS(onClick = {}) {
            SimpleButtonContent("Label".textValue(), iconRight = icon)
        }
        SimpleButtonDefaultS(onClick = {}) {
            SimpleButtonContent(null, icon)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Container {
        SimpleButtonDefaultM(onClick = {}) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonDefaultM(onClick = {}, enabled = false) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonDefaultM(onClick = {}) {
            SimpleButtonContent("Label".textValue(), iconRight = icon)
        }
        SimpleButtonDefaultM(onClick = {}) {
            SimpleButtonContent(null, icon)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Container {
        SimpleButtonDefaultL(onClick = {}) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonDefaultL(onClick = {}, enabled = false) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonDefaultL(onClick = {}) {
            SimpleButtonContent("Label".textValue(), iconRight = icon)
        }
        SimpleButtonDefaultL(onClick = {}) {
            SimpleButtonContent(null, icon)
        }
    }
}

@Composable
private fun Lights() {
    val icon = AppTheme.specificIcons.add
    Header("Light")
    Container {
        SimpleButtonLightS(onClick = {}) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonLightS(onClick = {}, enabled = false) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonLightS(onClick = {}) {
            SimpleButtonContent("Label".textValue(), iconRight = icon)
        }
        SimpleButtonLightS(onClick = {}) {
            SimpleButtonContent(null, icon)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Container {
        SimpleButtonLightM(onClick = {}) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonLightM(onClick = {}, enabled = false) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonLightM(onClick = {}) {
            SimpleButtonContent("Label".textValue(), iconRight = icon)
        }
        SimpleButtonLightM(onClick = {}) {
            SimpleButtonContent(null, icon)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Container {
        SimpleButtonLightL(onClick = {}) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonLightL(onClick = {}, enabled = false) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonLightL(onClick = {}) {
            SimpleButtonContent("Label".textValue(), iconRight = icon)
        }
        SimpleButtonLightL(onClick = {}) {
            SimpleButtonContent(null, icon)
        }
    }
}

@Composable
private fun Outlines() {
    val icon = AppTheme.specificIcons.add
    Header("Outline")
    Container {
        SimpleButtonOutlineS(onClick = {}) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonOutlineS(onClick = {}, enabled = false) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonOutlineS(onClick = {}) {
            SimpleButtonContent("Label".textValue(), iconRight = icon)
        }
        SimpleButtonOutlineS(onClick = {}) {
            SimpleButtonContent(null, icon)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Container {
        SimpleButtonOutlineM(onClick = {}) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonOutlineM(onClick = {}, enabled = false) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonOutlineM(onClick = {}) {
            SimpleButtonContent("Label".textValue(), iconRight = icon)
        }
        SimpleButtonOutlineM(onClick = {}) {
            SimpleButtonContent(null, icon)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Container {
        SimpleButtonOutlineL(onClick = {}) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonOutlineL(onClick = {}, enabled = false) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonOutlineL(onClick = {}) {
            SimpleButtonContent("Label".textValue(), iconRight = icon)
        }
        SimpleButtonOutlineL(onClick = {}) {
            SimpleButtonContent(null, icon)
        }
    }
}

@Composable
private fun Inlines() {
    val icon = AppTheme.specificIcons.add
    Header("Inline")
    Container {
        SimpleButtonInlineS(onClick = {}) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonInlineS(onClick = {}, enabled = false) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonInlineS(onClick = {}) {
            SimpleButtonContent("Label".textValue(), iconRight = icon)
        }
        SimpleButtonInlineS(onClick = {}) {
            SimpleButtonContent(null, icon)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Container {
        SimpleButtonInlineM(onClick = {}) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonInlineM(onClick = {}, enabled = false) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonInlineM(onClick = {}) {
            SimpleButtonContent("Label".textValue(), iconRight = icon)
        }
        SimpleButtonInlineM(onClick = {}) {
            SimpleButtonContent(null, icon)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Container {
        SimpleButtonInlineL(onClick = {}) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonInlineL(onClick = {}, enabled = false) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonInlineL(onClick = {}) {
            SimpleButtonContent("Label".textValue(), iconRight = icon)
        }
        SimpleButtonInlineL(onClick = {}) {
            SimpleButtonContent(null, icon)
        }
    }
}
@Composable
private fun Greys() {
    val icon = AppTheme.specificIcons.add
    Header("Grey")
    Container {
        SimpleButtonGreyS(onClick = {}) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonGreyS(onClick = {}, enabled = false) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonGreyS(onClick = {}) {
            SimpleButtonContent("Label".textValue(), iconRight = icon)
        }
        SimpleButtonGreyS(onClick = {}) {
            SimpleButtonContent(null, icon)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Container {
        SimpleButtonGreyM(onClick = {}) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonGreyM(onClick = {}, enabled = false) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonGreyM(onClick = {}) {
            SimpleButtonContent("Label".textValue(), iconRight = icon)
        }
        SimpleButtonGreyM(onClick = {}) {
            SimpleButtonContent(null, icon)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Container {
        SimpleButtonGreyL(onClick = {}) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonGreyL(onClick = {}, enabled = false) {
            SimpleButtonContent("Label".textValue())
        }
        SimpleButtonGreyL(onClick = {}) {
            SimpleButtonContent("Label".textValue(), iconRight = icon)
        }
        SimpleButtonGreyL(onClick = {}) {
            SimpleButtonContent(null, icon)
        }
    }
}

@Composable
private fun Header(text: String) {
    Text(
        text = text,
        style = AppTheme.specificTypography.titleLarge,
        modifier = Modifier.padding(bottom = 12.dp),
        color = AppTheme.specificColorScheme.textPrimary,
    )
}

@Composable
private fun Container(content: @Composable RowScope.() -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = content,
    )
}