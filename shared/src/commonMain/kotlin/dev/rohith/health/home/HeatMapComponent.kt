@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package dev.rohith.health.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.rohith.health.models.HeatMapData
import dev.rohith.health.theme.DarkColorScheme

@Composable
fun HeatMap(
    modifier: Modifier = Modifier,
    heatMapData: List<HeatMapData> = emptyList(),
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(7),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .fillMaxWidth(1f)
            .height(160.dp),
    ) {
        items(heatMapData) {
            val intensity = (it.count / 3f) % 3.0f
            val color =
                if (intensity == 0.0f) DarkColorScheme.surface else DarkColorScheme.primary.copy(
                    alpha = kotlin.math.min(1f, intensity)
                )
            var showToolTip by remember {
                mutableStateOf(false)
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = color),
                modifier = Modifier
                    .width(41.dp)
                    .height(16.dp)
                    .clickable {
                        showToolTip = !showToolTip
                    },
            ) {
            }
        }
    }
}