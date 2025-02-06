package com.example.finalproject

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.data.UiToolingDataApi
import androidx.compose.ui.unit.dp
import androidx.room.util.copy
import com.example.finalproject.normalization.NormalizationViewModel
import com.example.finalproject.normalization.NormalizedEntry
import com.example.finalproject.train.TrainSettingsViewModel
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.RowChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Pie


@OptIn(UiToolingDataApi::class)
@SuppressLint("DefaultLocale")
@Composable
fun Test(context: Context, testingData: NormalizationViewModel, trainSettingsViewModel: TrainSettingsViewModel) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Button(onClick = {
                trainSettingsViewModel.startTesting(testingData, context)
            }) {
                Text(text = "Start testing")
            }
            RowChart(
                modifier= Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp),
                data = listOf(
                    Bars(
                        label = "",
                        values = listOf(
                            Bars.Data(
                                label = "Total Test Entries",
                                value = trainSettingsViewModel.totalTestEntries.toDouble(),
                                color = SolidColor(Color.Red)
                            ),
                            Bars.Data(
                                label = "Correct Predictions",
                                value = trainSettingsViewModel.correctPredictions.toDouble(),
                                color = SolidColor(Color.Green)
                            ),
                            Bars.Data(
                                label = "Accuracy",
                                value = trainSettingsViewModel.accuracy.toDouble(),
                                color = SolidColor(Color.Blue)
                            )
                        )),
//                Bars(
//                    label = "Correct Predictions",
//                    values = listOf(
//                        Bars.Data(
//                            value = trainSettingsViewModel.correctPredictions.toDouble(),
//                            color = SolidColor(Color.Green)
//                        )
//                    )),
//                Bars(
//                    label = "Accuracy",
//                    values = listOf(
//                        Bars.Data(
//                            value = trainSettingsViewModel.accuracy.toDouble(),
//                            color = SolidColor(Color.Blue)
//                        )
//                    ))
                ),
                barProperties = BarProperties(
                    cornerRadius = Bars.Data.Radius.Rectangle(
                        topRight = 16.dp, topLeft = 16.dp, bottomRight = 16.dp, bottomLeft = 16.dp
                    ),
                    spacing = 8.dp,
                    thickness = 40.dp,
                ),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
            )
        }





}