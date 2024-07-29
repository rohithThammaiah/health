package dev.rohith.health.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.rohith.health.resources.Res
import dev.rohith.health.resources.figtree_bold
import dev.rohith.health.resources.figtree_medium
import dev.rohith.health.resources.figtree_regular
import dev.rohith.health.resources.figtree_semibold
import org.jetbrains.compose.resources.FontResource


// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp,
    ),
    displaySmall = TextStyle(
//        fontFamily = poppins,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 12.sp,
    ),
    headlineLarge = TextStyle(
//        fontFamily = poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
    ),
    headlineMedium = TextStyle(
//        fontFamily = poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
    ),
    headlineSmall = TextStyle(
//        fontFamily = poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
    ),
    titleLarge = TextStyle(
//        fontFamily = poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
    ),
    titleMedium = TextStyle(
//        fontFamily = poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
    ),
    titleSmall = TextStyle(
//        fontFamily = poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
    ),
    bodyLarge = TextStyle(
//        fontFamily = poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
    ),
    bodyMedium = TextStyle(
//        fontFamily = poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    bodySmall = TextStyle(
//        fontFamily = poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
    ),
    labelLarge = TextStyle(
//        fontFamily = poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    labelMedium = TextStyle(
//        fontFamily = poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    ),
    labelSmall = TextStyle(
//        fontFamily = poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
    ),
)
