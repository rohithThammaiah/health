@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class
)

package dev.rohith.health.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.rohith.health.Async
import dev.rohith.health.RecordUiModel
import dev.rohith.health.models.ActivityRecord
import dev.rohith.health.models.HealthStat
import dev.rohith.health.models.HeatMapData
import dev.rohith.health.resources.Res
import dev.rohith.health.resources.ic_calendar_month_24
import dev.rohith.health.resources.ic_walk_24
import dev.rohith.health.theme.DarkColorScheme
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs
import kotlin.time.Duration

@Composable
fun HomeScreen(
    title: String,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    isHealthPermissionEnabled: Async<Boolean>,
    allowPermission: () -> Unit,
    healthRecord: Async<List<RecordUiModel>>,
    activities: Async<List<ActivityRecord>>,
    heatMapData: Async<List<HeatMapData>>,
) {
    Scaffold(
        topBar = {
            Card(
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                var isPickerVisible by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            isPickerVisible = !isPickerVisible
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        )
                    ) {
                        Icon(
                            painter = painterResource(resource = Res.drawable.ic_calendar_month_24),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = title,
                            style = typography.displayLarge,
                            modifier = Modifier.align(Alignment.Bottom)
                        )
                    }
                }

                AnimatedVisibility(visible = isPickerVisible) {
                    val datePickerState =
                        rememberDatePickerState(
                            initialSelectedDateMillis = selectedDate.atStartOfDayIn(
                                TimeZone.UTC
                            ).toEpochMilliseconds()
                        )
                    Instant.fromEpochMilliseconds(datePickerState.selectedDateMillis ?: -1).toLocalDateTime(
                        TimeZone.UTC).let {
                        onDateSelected.invoke(it.date)
                    }
                    DatePicker(
                        state = datePickerState,
                        title = null,
                        headline = null,
                        showModeToggle = false,
                    )
                }
            }
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
        ) {
            if (isHealthPermissionEnabled.invoke() != true) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Button(
                        onClick = allowPermission,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(text = "Give Health Connect permission")
                    }
                }
            } else {
                LazyColumn {

                    item {
                        Spacer(modifier = Modifier.size(16.dp))
                    }

                    item {
                        FlowRow(
                            maxItemsInEachRow = 2,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(horizontal = 16.dp),
                        ) {
                            healthRecord.invoke()?.forEach { record ->
                                StatUiModel(healthRecord = record)
                            }
                        }

                    }

                    item {
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = "Recent Activities",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = typography.headlineLarge,
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        Spacer(modifier = Modifier.size(16.dp))
                    }


                    items(activities.invoke() ?: emptyList()) { activity ->
                        ActivityUiModel(activity = activity)
                    }

                    item {
                        Spacer(modifier = Modifier.padding(8.dp))
                    }

                    item {
                        HeatMap(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            heatMapData = heatMapData.invoke() ?: emptyList(),
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun RowScope.StatUiModel(healthRecord: RecordUiModel) {
    Card(
        onClick = {},
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(0.5f)
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = healthRecord.background)
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = healthRecord.name,
                    color = healthRecord.onBackground,
                    style = typography.titleMedium
                )

                Icon(
                    painter = painterResource(resource = healthRecord.icon as DrawableResource),
                    contentDescription = healthRecord.name,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = healthRecord.value,
                color = healthRecord.onBackground,
                style = typography.bodyLarge,
            )
        }
    }
}

@Composable
fun ActivityUiModel(activity: ActivityRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        border = BorderStroke(1.dp, color = Color(0xFF000000)),
        colors = CardDefaults.cardColors(containerColor = DarkColorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
        ) {
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = DarkColorScheme.primary)
            ) {
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(resource = Res.drawable.ic_walk_24),
                        contentDescription = null,
                        tint = DarkColorScheme.onPrimary,
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
            Column() {
                Row {
                    Text(
                        text = activity.type,
                        color = DarkColorScheme.onSurface,
                        style = typography.titleLarge,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = formatDuration(activity.duration),
                        color = DarkColorScheme.onSurface,
                        style = typography.bodySmall
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    activity.healthRecord.filterNot { it.type == HealthStat.PEAK_HEART_BEAT }
                        .forEachIndexed { _, record ->
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = record.prettyValue,
                                    color = DarkColorScheme.onSurface,
                                    style = typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.size(3.dp))
                                Text(
                                    text = record.unit,
                                    color = DarkColorScheme.onSurface.copy(alpha = 0.8f),
                                    style = typography.bodyMedium
                                )
                            }
                        }
                }
            }
        }
    }
}

fun formatDuration(duration: Duration): String {
    val seconds: Long = duration.inWholeSeconds
    val absSeconds = abs(seconds)
    val positive =
        "${absSeconds / 3600}:${absSeconds % 3600 / 60}:%${absSeconds % 60}"
    return if (seconds < 0) "-$positive" else positive
}