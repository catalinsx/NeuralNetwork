package com.example.finalproject.normalization

import android.icu.text.DecimalFormat
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.finalproject.BehaviourCell
import com.example.finalproject.StatusCell
import com.example.finalproject.TableCell

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DataNormalization(viewModel: NormalizationViewModel) {
    val normalizedEntries = viewModel.normalizedEntries
    val decimalFormat = DecimalFormat("#.###")

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


        items(normalizedEntries) { entry ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                TableCell(text = decimalFormat.format(entry.speed_x), modifier = Modifier.widthIn(min = 100.dp))
                TableCell(text = decimalFormat.format(entry.altitude), modifier = Modifier.widthIn(min = 100.dp))
                TableCell(text = decimalFormat.format(entry.course), modifier = Modifier.widthIn(min = 100.dp))
                TableCell(text = decimalFormat.format(entry.courseVariation), modifier = Modifier.widthIn(min = 100.dp))
                TableCell(text = decimalFormat.format(entry.accX), modifier = Modifier.widthIn(min = 100.dp))
                TableCell(text = decimalFormat.format(entry.accY), modifier = Modifier.widthIn(min = 100.dp))
                TableCell(text = decimalFormat.format(entry.accZ), modifier = Modifier.widthIn(min = 100.dp))
                BehaviourCell(text = entry.behaviour, modifier = Modifier.widthIn(min = 100.dp))
                TableCell(text = decimalFormat.format(entry.roll), modifier = Modifier.widthIn(min = 100.dp))
                TableCell(text = decimalFormat.format(entry.pitch), modifier = Modifier.widthIn(min = 100.dp))
                TableCell(text = decimalFormat.format(entry.yaw), modifier = Modifier.widthIn(min = 100.dp))
                TableCell(text = decimalFormat.format(entry.distanceAheadVehicle), modifier = Modifier.widthIn(min = 100.dp))
                TableCell(text = decimalFormat.format(entry.timeOfImpactAheadVehicle), modifier = Modifier.widthIn(min = 100.dp))
            }
        }

//
//        if (displayedItemsCount < csvEntries.size) {
//            item {
//                Button(
//                    onClick = {
//
//                        displayedItemsCount += 50
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 16.dp)
//                ) {
//                    Text("Show More")
//                }
//            }
//        }
    }
}