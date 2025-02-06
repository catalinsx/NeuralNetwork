package com.example.finalproject.train

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.finalproject.normalization.NormalizationViewModel
import com.example.finalproject.normalization.NormalizedEntry
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainSettings(viewModel: TrainSettingsViewModel, trainingData: NormalizationViewModel) {
    var hiddenLayers by remember { mutableStateOf(viewModel.hiddenLayerCount) }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Select Hidden Layers:")
            ExposedDropdownMenuBox(
                expanded = viewModel.expanded,
                onExpandedChange = { viewModel.toggleExpanded() }
            ) {
                OutlinedTextField(
                    value = hiddenLayers.toString(),
                    onValueChange = {},
                    label = { Text("Layers") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.expanded)
                    },
                    modifier = Modifier
                        .width(80.dp)
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = viewModel.expanded,
                    onDismissRequest = { viewModel.toggleExpanded() }
                ) {
                    (1..3).forEach { layerCount ->
                        DropdownMenuItem(
                            text = { Text(layerCount.toString()) },
                            onClick = {
                                viewModel.updateHiddenLayerCount(layerCount)
                                hiddenLayers = layerCount
                                viewModel.toggleExpanded()
                            }
                        )
                    }
                }
            }

            for (i in 1..hiddenLayers) {
                OutlinedTextField(
                    value = viewModel.hiddenLayerNeurons.getOrElse(i - 1) { 0 }.toString(),
                    onValueChange = { newCount ->
                        viewModel.updateHiddenLayerNeurons(i - 1, newCount.toIntOrNull() ?: 0)
                    },
                    label = { Text("Neurons in HL$i") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }

        VerticalDivider(
            modifier = Modifier.fillMaxHeight(),
            thickness = 2.dp
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Learning Rate:")
            OutlinedTextField(
                value = viewModel.learningRate.toString(),
                onValueChange = { viewModel.learningRate = it.toDoubleOrNull() ?: 0.0 },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Text("Error Tolerance:")
            OutlinedTextField(
                value = formatDouble(viewModel.errorTolerance),
                onValueChange = {
                    val newValue = it.toDoubleOrNull()
                    viewModel.errorTolerance = newValue ?: 0.0
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Text("Max Epochs:")
            OutlinedTextField(
                value = viewModel.maxEpochs.toString(),
                onValueChange = { viewModel.maxEpochs = it.toIntOrNull() ?: 0 },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )



            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                         viewModel.startTraining(trainingData, 12, 3)
                    },
                    modifier = Modifier.padding(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.Green,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.LightGray
                    )
                ) {
                    Text("Start")
                }
                Text(
                    text = "Epochs: ${viewModel.epoch}",
                    fontStyle = FontStyle.Italic)
                Button(
                    onClick = {
                        viewModel.stopTraining()
                    },
                    modifier = Modifier.padding(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.LightGray
                    )
                ) {
                    Text("Stop")
                }
            }


            var progress by remember { mutableFloatStateOf(0f) }
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = LinearOutSlowInEasing
                ), label = ""
            )

            LaunchedEffect(viewModel.progress) {
                progress = viewModel.progress
            }
            CustomLinearProgressIndicator(progress = animatedProgress)
        }
    }
}

@SuppressLint("DefaultLocale")
private fun formatDouble(value: Double): String {
    return String.format("%.4f", value)
}


@Composable
fun CustomLinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    progressColor: Color = Color.Green,
    backgroundColor: Color = Color(0xFFB9F792),
    clipShape: Shape = RoundedCornerShape(16.dp)
) {
    Box(
        modifier = modifier
            .clip(clipShape)
            .background(backgroundColor)
            .height(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(progressColor)
                .fillMaxHeight()
                .fillMaxWidth(progress)
        )
    }
}


