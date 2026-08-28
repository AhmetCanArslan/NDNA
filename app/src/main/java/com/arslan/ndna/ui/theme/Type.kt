package com.arslan.ndna.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Default = Typography()

// Expressive type: heavier headlines, tighter tracking, roomier body.
val ExpressiveTypography = Typography(
    displaySmall = Default.displaySmall.copy(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-1).sp
    ),
    headlineLarge = Default.headlineLarge.copy(
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.8).sp
    ),
    headlineMedium = Default.headlineMedium.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp
    ),
    headlineSmall = Default.headlineSmall.copy(fontWeight = FontWeight.Bold),
    titleLarge = Default.titleLarge.copy(fontWeight = FontWeight.Bold),
    titleMedium = Default.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    labelLarge = Default.labelLarge.copy(fontWeight = FontWeight.SemiBold),
    labelMedium = Default.labelMedium.copy(fontWeight = FontWeight.SemiBold),
    bodyLarge = Default.bodyLarge.copy(lineHeight = 26.sp)
)

// Rounder than baseline M3: expressive shapes lean on big corners.
val ExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)
