package com.quan.finance_app.ui.theme

import androidx.compose.ui.graphics.Color

// Flow Blue (primary)
val Blue50 = Color(0xFFEEF4FF)
val Blue100 = Color(0xFFDCE8FF)
val Blue200 = Color(0xFFB8D0FF)
val Blue300 = Color(0xFF8AB1FF)
val Blue400 = Color(0xFF558CFA)
val Blue500 = Color(0xFF2B6BF0)
val Blue600 = Color(0xFF1A50CC)
val Blue700 = Color(0xFF14409E)
val Blue800 = Color(0xFF112F70)
val Blue900 = Color(0xFF0B1F49)
val Blue950 = Color(0xFF06132E)

// Heat (money going OUT / spending)
val Heat50 = Color(0xFFFFF3EC)
val Heat100 = Color(0xFFFFE2D1)
val Heat200 = Color(0xFFFFC3A3)
val Heat300 = Color(0xFFFF9E6E)
val Heat400 = Color(0xFFFF7A45)
val Heat500 = Color(0xFFF2571F)
val Heat600 = Color(0xFFCE3F10)
val Heat700 = Color(0xFFA2300C)
val Heat800 = Color(0xFF6E2109)

// Grow (money coming IN / income & savings)
val Grow50 = Color(0xFFE9FBF1)
val Grow100 = Color(0xFFC9F5DD)
val Grow200 = Color(0xFF92E8BC)
val Grow300 = Color(0xFF55D697)
val Grow400 = Color(0xFF23BE78)
val Grow500 = Color(0xFF0FA162)
val Grow600 = Color(0xFF0B7F4E)
val Grow700 = Color(0xFF08603C)

// Alert (overspend, destructive)
val Alert50 = Color(0xFFFFF1F3)
val Alert100 = Color(0xFFFFE0E5)
val Alert300 = Color(0xFFFF95A8)
val Alert500 = Color(0xFFE11D48)
val Alert600 = Color(0xFFBE123C)
val Alert700 = Color(0xFF9F1239)

// Sun (warnings, budget nearing limit)
val Sun50 = Color(0xFFFFF8E6)
val Sun100 = Color(0xFFFFEFC2)
val Sun300 = Color(0xFFFFD152)
val Sun500 = Color(0xFFEFA707)
val Sun600 = Color(0xFFC48304)

// Ink (cool blue-grey neutrals)
val Ink0 = Color(0xFFFFFFFF)
val Ink50 = Color(0xFFF6F8FC)
val Ink100 = Color(0xFFEDF1F7)
val Ink200 = Color(0xFFDCE2EC)
val Ink300 = Color(0xFFBCC5D6)
val Ink400 = Color(0xFF939EB4)
val Ink500 = Color(0xFF6B7891)
val Ink600 = Color(0xFF4A5872)
val Ink700 = Color(0xFF2C3A55)
val Ink800 = Color(0xFF1A2740)
val Ink900 = Color(0xFF101B2D)
val Ink950 = Color(0xFF0A1220)

// Category hues
val CatFood = Heat500
val CatTransport = Sun500
val CatShopping = Color(0xFFE0489B)
val CatBills = Color(0xFF7A5CF0)
val CatHealth = Grow500
val CatFun = Heat400
val CatHome = Blue500
val CatOther = Ink500

// Gradients (Represented as lists of colors for Brush in Compose)
val GradientFlow = listOf(Blue600, Blue400)
val GradientOut = listOf(Heat500, Sun500)
val GradientIn = listOf(Grow600, Grow300)
val GradientHero = listOf(Blue700, Blue500, Blue400)