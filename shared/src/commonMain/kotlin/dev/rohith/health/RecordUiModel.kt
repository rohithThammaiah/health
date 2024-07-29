package dev.rohith.health

import androidx.compose.ui.graphics.Color
import dev.rohith.health.resources.Res
import org.jetbrains.compose.resources.DrawableResource

data class RecordUiModel(
    val name: String,
    val value: String,
    val background: Color,
    val onBackground: Color,
    val icon: DrawableResource,
)