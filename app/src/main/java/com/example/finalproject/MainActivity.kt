package com.example.finalproject

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finalproject.normalization.DataNormalization
import com.example.finalproject.normalization.NormalizationViewModel
import com.example.finalproject.train.Train
import com.example.finalproject.train.TrainSettings
import com.example.finalproject.train.TrainSettingsViewModel
import com.example.finalproject.ui.theme.FinalProjectTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinalProjectTheme {
                var selectedItemIndex by rememberSaveable {
                    mutableIntStateOf(0)
                }
                val navController = rememberNavController()
                val normalizationViewModel: NormalizationViewModel = viewModel()
                val trainViewModel: TrainSettingsViewModel = viewModel()

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    Row(Modifier.fillMaxSize()) {
                        NavigationSideBar(
                            items = items,
                            selectedItemIndex = selectedItemIndex,
                            onNavigate = {
                                selectedItemIndex = it
                                when (it) {
                                    0 -> navController.navigate("dataentry")
                                    1 -> navController.navigate("datanormalization")
                                    2 -> navController.navigate("trainsettings")
                                    3 -> navController.navigate("graph")
                                    4 -> navController.navigate("testing")
                                }
                            }
                        )

                        LaunchedEffect(navController) {
                            navController.currentBackStackEntryFlow.collect { backStackEntry ->
                                when (backStackEntry.destination.route) {
                                    "dataentry" -> selectedItemIndex = 0
                                    "datanormalization" -> selectedItemIndex = 1
                                    "trainsettings" -> selectedItemIndex = 2
                                    "graph" -> selectedItemIndex = 3
                                    "testing" -> selectedItemIndex = 4
                                }
                            }
                        }

                        NavHost(
                            navController = navController,
                            startDestination = "dataentry",
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable("dataentry") { DataEntry(this@MainActivity,normalizationViewModel, navController) }
                            composable("datanormalization") { DataNormalization(normalizationViewModel) }
                            composable("graph") { Train(trainViewModel) }
                            composable("testing") { Test(this@MainActivity, normalizationViewModel, trainViewModel) }
                            composable("trainsettings") { TrainSettings( trainViewModel, normalizationViewModel) }
                        }
                    }
                }
            }
        }
    }
}

data class NavigationItem(
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)


val items = listOf(
    NavigationItem("Input", Icons.Outlined.Settings, Icons.Filled.Settings),
    NavigationItem("Normalization", Icons.Outlined.Done, Icons.Filled.Done),
    NavigationItem("TrainSettings", Icons.Outlined.Settings, Icons.Filled.Settings),
    NavigationItem("Graph", Icons.Outlined.PlayArrow, Icons.Filled.PlayArrow),
    NavigationItem("Testing", Icons.Outlined.ExitToApp, Icons.Filled.ExitToApp)

)

@Composable
fun NavigationSideBar(
    items: List<NavigationItem>,
    selectedItemIndex: Int,
    onNavigate: (Int) -> Unit
) {
    NavigationRail(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .offset(x = (-1.5).dp) // divider
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        ) {
            items.forEachIndexed { index, item ->
                NavigationRailItem(
                    icon = {
                        Icon(
                            imageVector = if (index == selectedItemIndex) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    },
                    label = {
                        Text(item.title)
                            },
                    selected = index == selectedItemIndex,
                    onClick = { onNavigate(index) }
                )
            }
        }
    }
}

