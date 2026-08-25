package com.quan.finance_app.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import androidx.compose.ui.text.font.Font
import com.quan.finance_app.R

val FontCore = FontFamily(
    Font(R.font.be_vietnam_pro_light, FontWeight.Light),
    Font(R.font.be_vietnam_pro_regular, FontWeight.Normal),
    Font(R.font.be_vietnam_pro_medium, FontWeight.Medium),
    Font(R.font.be_vietnam_pro_semibold, FontWeight.SemiBold),
    Font(R.font.be_vietnam_pro_bold, FontWeight.Bold),
    Font(R.font.be_vietnam_pro_extrabold, FontWeight.ExtraBold)
)

val FontMono = FontFamily(
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
    Font(R.font.jetbrains_mono_medium, FontWeight.Medium),
    Font(R.font.jetbrains_mono_bold, FontWeight.Bold)
)

data class MoneyFlowTypography(
    val display1: TextStyle = TextStyle(
        fontFamily = FontCore,
        fontWeight = FontWeight.Bold,
        fontSize = 44.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.02).sp
    ),
    val display2: TextStyle = TextStyle(
        fontFamily = FontCore,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.02).sp
    ),
    val title1: TextStyle = TextStyle(
        fontFamily = FontCore,
        fontWeight = FontWeight.Bold, // Assuming headings use 700
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.02).sp
    ),
    val title2: TextStyle = TextStyle(
        fontFamily = FontCore,
        fontWeight = FontWeight.SemiBold, // 600
        fontSize = 21.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.01).sp
    ),
    val title3: TextStyle = TextStyle(
        fontFamily = FontCore,
        fontWeight = FontWeight.Medium, // 500
        fontSize = 17.sp,
        lineHeight = 24.sp
    ),
    val body: TextStyle = TextStyle(
        fontFamily = FontCore,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    val bodySm: TextStyle = TextStyle(
        fontFamily = FontCore,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp
    ),
    val caption: TextStyle = TextStyle(
        fontFamily = FontCore,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    val micro: TextStyle = TextStyle(
        fontFamily = FontCore,
        fontWeight = FontWeight.SemiBold, // Often used in eyebrows with caps
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.08.sp
    ),
    
    // Amount Typography
    val amountHero: TextStyle = TextStyle(
        fontFamily = FontMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 40.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.02).sp
    ),
    val amountLg: TextStyle = TextStyle(
        fontFamily = FontMono,
        fontWeight = FontWeight.Medium,
        fontSize = 26.sp,
        lineHeight = 32.sp
    ),
    val amountMd: TextStyle = TextStyle(
        fontFamily = FontMono,
        fontWeight = FontWeight.Medium,
        fontSize = 19.sp,
        lineHeight = 24.sp
    ),
    val amountSm: TextStyle = TextStyle(
        fontFamily = FontMono,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 22.sp
    )
)
