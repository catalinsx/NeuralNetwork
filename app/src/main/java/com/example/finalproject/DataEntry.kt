package com.example.finalproject

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finalproject.normalization.NormalizationViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Stable
fun DataEntry(context: Context, viewModel: NormalizationViewModel, navController: NavController) {
    val csvEntries = remember { readCsvAndParse(context, R.raw.dataset1800) }
    var displayedItemsCount by remember { mutableIntStateOf(50) }

    Box(modifier = Modifier.fillMaxSize()){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            stickyHeader {
                ElevatedCard(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.elevatedCardElevation(8.dp),
                    colors = CardDefaults.elevatedCardColors(Color.Green)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(Color.Green)
                    ) {
                        TableCell(text = "Speed", title = true, modifier = Modifier.widthIn(min = 100.dp))
                        TableCell(text = "Altitude", title = true, modifier = Modifier.widthIn(min = 100.dp))
                        TableCell(text = "Course", title = true, modifier = Modifier.widthIn(min = 100.dp))
                        TableCell(text = "CourseVar", title = true, modifier = Modifier.widthIn(min = 100.dp))
                        TableCell(text = "AccX", title = true, modifier = Modifier.widthIn(min = 100.dp))
                        TableCell(text = "AccY", title = true, modifier = Modifier.widthIn(min = 100.dp))
                        TableCell(text = "AccZ", title = true, modifier = Modifier.widthIn(min = 100.dp))
                        TableCell(text = "Behaviour", title = true, modifier = Modifier.widthIn(min = 100.dp))
                        TableCell(text = "Roll", title = true, modifier = Modifier.widthIn(min = 100.dp))
                        TableCell(text = "Pitch", title = true, modifier = Modifier.widthIn(min = 100.dp))
                        TableCell(text = "Yaw", title = true, modifier = Modifier.widthIn(min = 100.dp))
                        TableCell(text = "Distance V.A", title = true, modifier = Modifier.widthIn(min = 100.dp))
                        TableCell(text = "Time of I.V.A", title = true, modifier = Modifier.widthIn(min = 100.dp))
                    }
                }
            }

            items(csvEntries.take(displayedItemsCount)) { entry ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    TableCell(text = entry.speed_x.toString(), modifier = Modifier.widthIn(min = 100.dp))
                    TableCell(text = entry.altitude.toString(), modifier = Modifier.widthIn(min = 100.dp))
                    TableCell(text = entry.course.toString(), modifier = Modifier.widthIn(min = 100.dp))
                    TableCell(text = entry.courseVariation.toString(), modifier = Modifier.widthIn(min = 100.dp))
                    TableCell(text = entry.accX.toString(), modifier = Modifier.widthIn(min = 100.dp))
                    TableCell(text = entry.accY.toString(), modifier = Modifier.widthIn(min = 100.dp))
                    TableCell(text = entry.accZ.toString(), modifier = Modifier.widthIn(min = 100.dp))
                    StatusCell(text = entry.behaviour, modifier = Modifier.widthIn(min = 100.dp))
                    TableCell(text = entry.roll.toString(), modifier = Modifier.widthIn(min = 100.dp))
                    TableCell(text = entry.pitch.toString(), modifier = Modifier.widthIn(min = 100.dp))
                    TableCell(text = entry.yaw.toString(), modifier = Modifier.widthIn(min = 100.dp))
                    TableCell(text = entry.distanceAheadVehicle.toString(), modifier = Modifier.widthIn(min = 100.dp))
                    TableCell(text = entry.timeOfImpactAheadVehicle.toString(), modifier = Modifier.widthIn(min = 100.dp))
                }
            }


            if (displayedItemsCount < csvEntries.size) {
                item {
                    Button(
                        onClick = {
                            displayedItemsCount += 50
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text("Show More")
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = {
                viewModel.normalizeData(csvEntries)
                navController.navigate("datanormalization")
            },
            modifier = Modifier
                .padding(32.dp)
                .align(Alignment.BottomEnd),
            containerColor = Color.Green,
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                Icon(Icons.Filled.Send, contentDescription = "Send")
                Text("Normalize")
            }
        }
    }
}

@Composable
fun StatusCell(
    text: String,
    alignment: TextAlign = TextAlign.Center,
    modifier: Modifier
) {
    val color = when (text) {
        "NORMAL" -> Color(0xfff8deb5)
        "DROWSY" -> Color(0xfff8deb5)
        "AGGRESSIVE" -> Color(0xFFF8DEB5)
        else -> Color(0xffffcccf)
    }
    val textColor = when (text) {
        "AGGRESSIVE" -> Color(0xFFF51100)
        "NORMAL" -> Color(0xFF13B120)
        "DROWSY" -> Color(0xFFFF9800)
        else -> Color(0xffca1e17)
    }

    Text(
        text = text,
        modifier = modifier
            .padding(12.dp)
            .background(color, shape = RoundedCornerShape(50.dp)),
        textAlign = alignment,
        color = textColor
    )
}

@Composable
fun TableCell(
    text: String,
    alignment: TextAlign = TextAlign.Center,
    title: Boolean = false,
    modifier: Modifier
) {
    Text(
        text = text,
        modifier = modifier
            .padding(10.dp),
        fontWeight = if (title) FontWeight.Bold else FontWeight.Normal,
        textAlign = alignment
    )
}

@Composable
fun BehaviourCell(
    text: List<Int>,
    alignment: TextAlign = TextAlign.Center,
    title: Boolean = false,
    modifier: Modifier
) {
    Text(
        text = text.toString(),
        modifier = modifier
            .padding(10.dp),
        fontWeight = if (title) FontWeight.Bold else FontWeight.Normal,
        textAlign = alignment
    )
}



