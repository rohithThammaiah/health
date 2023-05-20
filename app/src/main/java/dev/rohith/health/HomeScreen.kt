package dev.rohith.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksActivityViewModel
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

import java.time.format.DateTimeFormatter
import kotlin.math.abs


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    allowPermission: () -> Unit,
) {
    val homeViewModel: HomeViewModel = mavericksActivityViewModel()
    val state: HomeState = homeViewModel.collectAsState().value
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = state.title, color = MaterialTheme.colorScheme.onPrimaryContainer)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
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
                val healthRecord = state.healthRecord.invoke()
                LazyColumn {

                    item {
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Your stats for the day",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    }

                    item {
                        FlowColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            maxItemsInEachColumn = 2,
                            verticalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            state.healthRecord.invoke()?.forEachIndexed { index, record ->
                                StatUiModel(healthRecord = record)
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = "Latest Activities",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
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
fun ColumnScope.StatUiModel(healthRecord: Record) {
    Card(
        onClick = {},
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(0.5f)
            .weight(1f)
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            Text(text = healthRecord.name)
            Spacer(modifier = Modifier.size(12.dp))
            Text(text = healthRecord.value.toString() ?: "-")
        }
    }
}

@Composable
fun ActivityUiModel(activity: ActivityRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
            activity.healthRecord.forEachIndexed { index, record ->
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
    val PATTERN_FORMAT = "dd MMM, yyyy";

    val formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
        .withZone(ZoneId.systemDefault())

    return formatter.format(instant)
}