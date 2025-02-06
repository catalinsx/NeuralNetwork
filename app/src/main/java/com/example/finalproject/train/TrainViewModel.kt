package com.example.finalproject.train

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.R
import com.example.finalproject.normalization.NormalizationViewModel
import com.example.finalproject.normalization.NormalizedEntry
import com.example.finalproject.readCsvAndParse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.ln
import kotlin.random.Random

@SuppressLint("StaticFieldLeak")
class TrainSettingsViewModel : ViewModel() {
    var hiddenLayerCount by mutableStateOf(1)
    var hiddenLayerNeurons = mutableStateListOf<Int>()

    var expanded by mutableStateOf(false)
    var learningRate by mutableStateOf(0.01)
    var errorTolerance by mutableStateOf(0.001)
    var maxEpochs by mutableStateOf(500)

    var weights = mutableListOf<List<MutableList<Double>>>()


    private val _validationErrorsPerEpoch =
        mutableStateListOf<Double>()
    val validationErrorsPerEpochChart: SnapshotStateList<Double> = _validationErrorsPerEpoch

    private val _errorsPerEpoch = mutableStateListOf<Double>()
    val errorsPerEpochChart: SnapshotStateList<Double> = _errorsPerEpoch

    init {
        updateHiddenLayerNeurons(hiddenLayerCount)
    }

    fun updateHiddenLayerCount(count: Int) {
        hiddenLayerCount = count
        updateHiddenLayerNeurons(count)
    }

    private fun updateHiddenLayerNeurons(count: Int) {
        hiddenLayerNeurons.clear()
        repeat(count) {
            hiddenLayerNeurons.add(1)
        }
    }

    fun updateHiddenLayerNeurons(index: Int, count: Int) {
        if (index in hiddenLayerNeurons.indices) {
            hiddenLayerNeurons[index] = count
        }
    }

    fun toggleExpanded() {
        expanded = !expanded
    }

    var testingInProgress by mutableStateOf(false)

    var testError by mutableStateOf(0.0f)
    var accuracy by mutableStateOf(0.0f)
    var correctPredictions by mutableStateOf(0)
    var totalTestEntries by mutableIntStateOf(0)


    fun startTesting(testData: NormalizationViewModel, context: Context) {
        testingInProgress = true
        viewModelScope.launch {
            val testingEntries = readCsvAndParse(context, R.raw.dataset900)
            testData.normalizeTestingData(testingEntries)
            delay(3000)
            testNetwork(testData.normalizedTestingEntries)
            testingInProgress = false
        }
    }

    private fun testNetwork(
        normalizedEntries: List<NormalizedEntry>
    ) {
        var totalTestError = 0.0
        var correctPredictionsCount = 0

        for (entry in normalizedEntries) {
            val outputs =
                forwardPropagation(entry, weights, hiddenLayerNeurons.take(hiddenLayerCount)).last()
            val expectedOutputs = entry.outputs.map { it.toDouble() }

            Log.d("Testing", "Expected: $expectedOutputs, Actual: $outputs")

            totalTestError += calculateLoss(expectedOutputs, outputs)

            val predictedClass = outputs.indexOf(outputs.maxOrNull() ?: 0.0)
            val actualClass = expectedOutputs.indexOf(expectedOutputs.maxOrNull() ?: 0.0)

            if (predictedClass == actualClass) {
                correctPredictionsCount++
            }
        }

        testError = (totalTestError / normalizedEntries.size).toFloat()
        accuracy =
            (correctPredictionsCount.toFloat() / normalizedEntries.size) * 100
        correctPredictions = correctPredictionsCount
        totalTestEntries =
            normalizedEntries.size

        Log.d("Testing", "Test Error: $testError")
        Log.d(
            "Testing",
            "Correct Predictions: $correctPredictions out of ${normalizedEntries.size} Accuracy: $accuracy%"
        )
    }


    fun initializeWeights(inputNeuronCount: Int, outputNeuronCount: Int) {
        weights.clear()
        val random = Random.Default

        /*
            List(inputNeuronCount) creeaza o lista cu inputNeuronCount elemente, care definite in acolada
            reprezinta o expresie lambda care genereaza o lista mutable (modificabila) cu hiddenLayerNeurons[0] elemente
            adica numarul neuroniilor din primul hidden layer care la randu lui are alt lambda sa genereze random number pt weights
          */
        val inputToHiddenWeights = List(inputNeuronCount) {
            MutableList(hiddenLayerNeurons[0]) {
                random.nextDouble(-0.5, 0.5)
            }
        }
        weights.add(inputToHiddenWeights)

        for (i in 0 until hiddenLayerCount - 1) {
            val hiddenToHiddenWeights = List(hiddenLayerNeurons[i]) {
                MutableList(hiddenLayerNeurons[i + 1]) {
                    random.nextDouble(-0.5, 0.5)
                }
            }
            weights.add(hiddenToHiddenWeights)
        }

        val hiddenToOutputWeights = List(hiddenLayerNeurons.last()) {
            MutableList(outputNeuronCount) {
                random.nextDouble(-0.5, 0.5)
            }
        }
        weights.add(hiddenToOutputWeights)
    }

    @SuppressLint("DefaultLocale")
    fun printWeights(weights: List<List<List<Double>>>) {
        for (layerIndex in weights.indices) {
            println("Layer $layerIndex Weights:")

            for (neuronWeights in weights[layerIndex]) {
                println(neuronWeights.joinToString(", ") { String.format("%.4f", it) })
            }

            println()
        }
    }


    private var trainingInProgress by mutableStateOf(false)
    var progress by mutableFloatStateOf(0f)

    fun startTraining(
        trainingData: NormalizationViewModel,
        inputNeuronCount: Int,
        outputNeuronCount: Int
    ) {
        trainingInProgress = true
        viewModelScope.launch {
            trainNetwork(trainingData, inputNeuronCount, outputNeuronCount)
            trainingInProgress = false
        }
    }

    fun stopTraining() {
        trainingInProgress = false
        progress = 0f
        _errorsPerEpoch.clear()
        epoch = 0
    }

    var epoch = 0
    private suspend fun trainNetwork(
        trainingData: NormalizationViewModel,
        inputNeuronCount: Int,
        outputNeuronCount: Int
    ) {
        val hiddenLayersConfig = hiddenLayerNeurons.take(hiddenLayerCount)
        val learningRate = this.learningRate
        val errorTolerance = this.errorTolerance
        val maxEpochs = this.maxEpochs

        initializeWeights(inputNeuronCount, outputNeuronCount)
        printWeights(weights)

        if (trainingData.normalizedEntries.isEmpty()) {
            Log.e("Training Error", "Training data is empty")
            return
        }

        val validationEntries = get10PercentData(trainingData.normalizedEntries)
        val trainingEntries =
            trainingData.normalizedEntries - validationEntries

        epoch = 0
        var totalError: Double
        var validationError: Double

        while (epoch < maxEpochs && trainingInProgress) {
            totalError = 0.0
            validationError = 0.0


            for (entry in trainingEntries) {
                val allLayerOutputs = forwardPropagation(entry, weights, hiddenLayersConfig)
                val outputs = allLayerOutputs.last()
                val expectedOutputs = entry.outputs.map { it.toDouble() }

                totalError += calculateLoss(expectedOutputs, outputs)
                backpropagation(expectedOutputs, weights, allLayerOutputs, learningRate)
            }

            val meanTrainingError = totalError / trainingEntries.size
            _errorsPerEpoch += meanTrainingError
            Log.d("Training", "Epoch $epoch, Training Error: $meanTrainingError")

            for (entry in validationEntries) {
                val outputs = forwardPropagation(entry, weights, hiddenLayersConfig).last()
                val expectedOutputs = entry.outputs.map { it.toDouble() }

                validationError += calculateLoss(expectedOutputs, outputs)
            }
            val meanValidationError = validationError / validationEntries.size
            _validationErrorsPerEpoch += meanValidationError
            Log.d("Validation", "Epoch $epoch, Validation Error: $meanValidationError")


            if (meanTrainingError < errorTolerance) break

            epoch++
            progress = epoch.toFloat() / maxEpochs
            delay(25)
        }
        trainingInProgress = false
    }


    private fun get10PercentData(entries: List<NormalizedEntry>): List<NormalizedEntry> {
        val sampleSize = (entries.size * 0.1).toInt()
        return entries.shuffled().take(sampleSize)
    }

}








