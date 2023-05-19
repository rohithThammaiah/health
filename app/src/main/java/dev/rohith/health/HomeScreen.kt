package dev.rohith.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    allowPermission: () -> Unit,
) {
    val homeViewModel: HomeViewModel = mavericksViewModel()
    val state: HomeState = homeViewModel.collectAsState().value
    Scaffold(
        topBar = {
            Text(text = state.title)
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
                .fillMaxSize(), verticalArrangement = Arrangement.Center
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

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        top = 16.dp,
                        end = 12.dp,
                        bottom = 16.dp
                    ),

                    ) {
                    item {
                        Card(
                            onClick = {},
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                        ) {
                            Column(modifier = Modifier
                                .height(200.dp)
                                .padding(16.dp)) {
                                Text(text = "Steps")
                                Spacer(modifier = Modifier.size(12.dp))
                                Text(text = healthRecord?.steps?.toString() ?: "-")
                            }
                        }
                    }

                    item {
                        Card(
                            onClick = {},
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                        ) {
                            Column(modifier = Modifier
                                .height(200.dp)
                                .padding(16.dp)) {
                                Text(text = "Distance")
                                Spacer(modifier = Modifier.size(12.dp))
                                Text(text = healthRecord?.distanceInMeters?.toString() ?: "-")
                            }
                        }
                    }

                    item {
                        Card(
                            onClick = {},
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                        ) {
                            Column(modifier = Modifier
                                .height(200.dp)
                                .padding(16.dp)) {
                                Text(text = "Calories")
                                Spacer(modifier = Modifier.size(12.dp))
                                Text(text = healthRecord?.caloriesBurned?.toString() ?: "-")
                            }
                        }
                    }

                    item {
                        Card(
                            onClick = {},
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                        ) {
                            Column(modifier = Modifier
                                .height(200.dp)
                                .padding(16.dp)) {
                                Text(text = "Max Heart rate")
                                Spacer(modifier = Modifier.size(12.dp))
                                Text(text = healthRecord?.maxHeartRate?.toString() ?: "-")
                            }
                        }
                    }
                }
            }
        }
    }
}
