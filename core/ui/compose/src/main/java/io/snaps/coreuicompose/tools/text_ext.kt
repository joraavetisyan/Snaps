import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.resolveAsTypeface

@Composable
fun TextStyle.toTypeface(): Typeface {
    val resolver = LocalFontFamilyResolver.current
    return resolver.resolveAsTypeface(
        fontFamily = fontFamily,
        fontWeight = fontWeight ?: FontWeight.Normal,
        fontStyle = fontStyle ?: FontStyle.Normal,
        fontSynthesis = fontSynthesis ?: FontSynthesis.All
    ).value
}