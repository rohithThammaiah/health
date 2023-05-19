package dev.rohith.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksActivityViewModel
import com.airbnb.mvrx.compose.mavericksViewModel

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
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                val isSdkAvailable = state.isHealthSDKAvailable
                if (isSdkAvailable is Success) {
                    Text(text = "Is sdk present: ${state.isHealthSDKAvailable}")
                } else {
                    Text(text = "Checking...")
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

                            Card(
                                onClick = {},
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth(0.5f)
                                    .weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .height(200.dp)
                                        .padding(16.dp)
                                ) {
                                    Text(text = "Steps")
                                    Spacer(modifier = Modifier.size(12.dp))
                                    Text(text = healthRecord?.steps?.toString() ?: "-")
                                }
                            }



                            Card(
                                onClick = {},
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth(0.5f)
                                    .weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .height(200.dp)

                                        .padding(16.dp)
                                ) {
                                    Text(text = "Distance")
                                    Spacer(modifier = Modifier.size(12.dp))
                                    Text(text = healthRecord?.distanceInMeters?.toString() ?: "-")
                                }
                            }


                            Card(
                                onClick = {},
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth(0.5f)
                                    .weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .height(200.dp)
                                        .padding(16.dp)
                                ) {
                                    Text(text = "Calories")
                                    Spacer(modifier = Modifier.size(12.dp))
                                    Text(text = healthRecord?.caloriesBurned?.toString() ?: "-")
                                }
                            }



                            Card(
                                onClick = {},
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth(0.5f)
                                    .weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .height(200.dp)
                                        .padding(16.dp)
                                ) {
                                    Text(text = "Max Heart rate")
                                    Spacer(modifier = Modifier.size(12.dp))
                                    Text(text = healthRecord?.maxHeartRate?.toString() ?: "-")
                                }
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
                                Text(text = activity.type)
                                Spacer(modifier = Modifier.size(8.dp))
                                Row {
                                    Text(text = "Steps")
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text(text = activity.healthRecord.steps.toString())
                                }
                                Row {
                                    Text(text = "Calories Burned")
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text(text = activity.healthRecord.caloriesBurned.toString())
                                }
                                Row {
                                    Text(text = "Distance covered")
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text(text = activity.healthRecord.distanceInMeters.toString())
                                }
                                Row {
                                    Text(text = "Peak Heart rate")
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text(text = activity.healthRecord.maxHeartRate.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun <T> LazyListScope.gridItems(
    data: List<T>,
    columnCount: Int,
    modifier: Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    itemContent: @Composable BoxScope.(T) -> Unit,
) {
    val size = data.count()
    val rows = if (size == 0) 0 else 1 + (size - 1) / columnCount
    items(rows, key = { it.hashCode() }) { rowIndex ->
        Row(
            horizontalArrangement = horizontalArrangement,
            modifier = modifier
        ) {
            for (columnIndex in 0 until columnCount) {
                val itemIndex = rowIndex * columnCount + columnIndex
                if (itemIndex < size) {
                    Box(
                        modifier = Modifier.weight(1F, fill = true),
                        propagateMinConstraints = true
                    ) {
                        itemContent(data[itemIndex])
                    }
                } else {
                    Spacer(Modifier.weight(1F, fill = true))
                }
            }
        }
    }
}