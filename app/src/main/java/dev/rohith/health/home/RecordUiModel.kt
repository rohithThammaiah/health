package dev.rohith.health.home

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

data class RecordUiModel(
    val name: String,
    val value: String,
    val background: Color,
    val onBackground: Color,
    @DrawableRes val icon: Int,
)