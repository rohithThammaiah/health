@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package dev.rohith.health

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksActivityViewModel
import dev.rohith.health.ui.theme.Typography
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@Composable
fun HomeScreen(
    allowPermission: () -> Unit,
) {
    val homeViewModel: HomeViewModel = mavericksActivityViewModel()
    val state: HomeState = homeViewModel.collectAsState().value
    Scaffold(
        topBar = {
            Card(
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar_month_24),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Today",
                            style = Typography.displayLarge,
                            modifier = Modifier.align(Alignment.Bottom)
                        )
                    }
                }
            }
        },
        bottomBar = {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(0.dp),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(0.dp),
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.ic_show_chart_24
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Text(text = "Fitness", style = Typography.labelLarge)
                        }
                    }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(0.dp),
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = R.drawable.ic_food_bank_24
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(text = "Nutrition", style = Typography.labelLarge)
                        }
                    }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(0.dp),
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = R.drawable.ic_water_drop_24
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(text = "Hydro", style = Typography.labelLarge)
                        }
                    }

                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
        ) {
            if (state.isHealthSDKPermissionGranted.invoke() != true) {
                Button(
                    onClick = allowPermission,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Give Health Connect permission")
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
                            state.healthRecord.invoke()?.forEach { record ->
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


                    items(state.activities.invoke() ?: emptyList()) { activity ->
                        ActivityUiModel(activity = activity)
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
                    painter = painterResource(id = healthRecord.icon),
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
        border = BorderStroke(1.dp, color = Color(0xFF000000))
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 12.dp
            )
        ) {
            Row {
                Text(text = activity.type)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = formatDuration(activity.duration))
            }
            Spacer(modifier = Modifier.size(8.dp))
            activity.healthRecord.forEachIndexed { _, record ->
                Row {
                    Text(text = record.name)
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = record.value.toString())
                }
            }

            Spacer(modifier = Modifier.size(16.dp))
            Text(text = formatInstant(activity.timeStamp))
        }
    }
}

fun formatDuration(duration: Duration): String {
    val seconds: Long = duration.seconds
    val absSeconds = abs(seconds)
    val positive = String.format(
        "%d:%02d:%02d",
        absSeconds / 3600,
        absSeconds % 3600 / 60,
        absSeconds % 60
    )
    return if (seconds < 0) "-$positive" else positive
}

fun formatInstant(instant: Instant): String {
    val format = "dd MMM, yyyy"

    val formatter = DateTimeFormatter.ofPattern(format)
        .withZone(ZoneId.systemDefault())

    return formatter.format(instant)
}