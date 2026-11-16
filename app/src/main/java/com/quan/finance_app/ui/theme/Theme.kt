package com.quan.finance_app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class MoneyFlowColors(
    val textPrimary: Color = Ink900,
    val textSecondary: Color = Ink600,
    val textTertiary: Color = Ink500,
    val textDisabled: Color = Ink400,
    val textInverse: Color = Ink0,
    val textBrand: Color = Blue600,
    val textLink: Color = Blue600,
    val textLinkHover: Color = Blue700,

    val surfacePage: Color = Ink50,
    val surfaceCard: Color = Ink0,
    val surfaceSunken: Color = Ink100,
    val surfaceRaised: Color = Ink0,
    val surfaceInverse: Color = Blue950,
    val surfaceBrand: Color = Blue500,
    val surfaceBrandSoft: Color = Blue50,
    val surfaceOutSoft: Color = Heat50,
    val surfaceInSoft: Color = Grow50,
    val surfaceOverlay: Color = Color(0x8F0A1220), // rgba(10,18,32,.56)

    val borderCard: Color = Ink100,
    val borderSubtle: Color = Ink200,
    val borderDefault: Color = Ink300,
    val borderStrong: Color = Ink400,
    val borderBrand: Color = Blue500,
    val borderFocus: Color = Blue400,

    val flowOut: Color = Heat500,
    val flowOutStrong: Color = Heat600,
    val flowOutTrack: Color = Heat100,
    val flowIn: Color = Grow500,
    val flowInStrong: Color = Grow600,
    val flowInTrack: Color = Grow100,

    val stateDanger: Color = Alert500,
    val stateWarning: Color = Sun500,
    val stateSuccess: Color = Grow500,
    val stateInfo: Color = Blue500
)

val LocalMoneyFlowColors = staticCompositionLocalOf { MoneyFlowColors() }
val LocalMoneyFlowTypography = staticCompositionLocalOf { MoneyFlowTypography() }
val LocalMoneyFlowSpacing = staticCompositionLocalOf { MoneyFlowSpacing() }
val LocalMoneyFlowRadii = staticCompositionLocalOf { MoneyFlowRadii() }

object MoneyFlowTheme {
    val colors: MoneyFlowColors
        @Composable
        @ReadOnlyComposable
        get() = LocalMoneyFlowColors.current

    val typography: MoneyFlowTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalMoneyFlowTypography.current

    val spacing: MoneyFlowSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalMoneyFlowSpacing.current

    val radii: MoneyFlowRadii
        @Composable
        @ReadOnlyComposable
        get() = LocalMoneyFlowRadii.current
}

@Composable
fun MoneyFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // The design system currently only specifies light theme colors (e.g. surfaceCard = white).
    // We will use the default light colors for now.
    val colors = MoneyFlowColors()
    val typography = MoneyFlowTypography()
    val spacing = MoneyFlowSpacing()
    val radii = MoneyFlowRadii()

    CompositionLocalProvider(
        LocalMoneyFlowColors provides colors,
        LocalMoneyFlowTypography provides typography,
        LocalMoneyFlowSpacing provides spacing,
        LocalMoneyFlowRadii provides radii,
        content = content
    )
}