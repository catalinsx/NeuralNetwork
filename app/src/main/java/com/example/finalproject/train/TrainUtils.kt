package com.example.finalproject.train

import com.example.finalproject.normalization.NormalizedEntry
import kotlin.math.exp
import kotlin.math.ln



fun sigmoid(x: Double): Double {
    return 1.0 / (1.0 + exp(-x))
}

fun sigmoidDerivat(output: Double): Double {
    return output * (1 - output)
}

fun softmax(outputs: List<Double>): List<Double> {
    val expValues = outputs.map { exp(it) }
    val sumExpValues = expValues.sum()
    return expValues.map { it / sumExpValues }
}


// zip combina doua liste intr-o lista de perechi
// val mere = listOf(1, 2, 3)
// val pere = listOf(3, 4, 5)
// val zipped = mere.zip(pere) // [(1, 3), (2, 4), (3, 5)]

fun calculateGlobalInput(inputs: List<Double>, weights: List<Double>): Double {
    return inputs.zip(weights) { input, weight ->
        input * weight
    }.sum()
}

/*
allLayersOutputs reprezinta outputurile tuturor straturilor prin care trece forward propagation

se itereaza prin fiecare strat ascuns cu forEachIndexed ca sa vad layerIndexu si cati neuroni are
la inceputul fiecarui hidden layer se creeaza o lista noua layer outputs care are dimensiunea
egala cu numarul de neuroni din stratul respectiv, initializate toate cu 0.0

dupa care pentru fiecare neuronIndex din neuronii de pe hidden layer calculez globalInputul
in functia de calculateglobalinput se preia matricea de weights uri si se mapeaza toate ca sa se faca
produsul dintre input si weight unde ulterior se  aduna toate produsele

*/

fun forwardPropagation(
    entry: NormalizedEntry,
    weights: List<List<MutableList<Double>>>,
    hiddenLayersConfig: List<Int>
): List<List<Double>> {
    var inputs = entry.inputs
    val allLayerOutputs = mutableListOf<List<Double>>()

    hiddenLayersConfig.forEachIndexed { layerIndex, neuronCount ->
        val layerOutputs = MutableList(neuronCount) { 0.0 }
        for (neuronIndex in 0 until neuronCount) {
            val globalInput =
                calculateGlobalInput(inputs, weights[layerIndex].map { it[neuronIndex] })
            layerOutputs[neuronIndex] =
                sigmoid(globalInput) // se aplica sigmoidala pt fiecare output avand ca parametru global inputul
        }
        allLayerOutputs.add(layerOutputs)
        inputs = layerOutputs // trecem la urmatorul strat
    }

    val outputLayerWeights = weights.last() // ultima lista de greutati
    val outputLayer = MutableList(3) { 0.0 }
    for (outputIndex in outputLayer.indices) {
        val globalInput =
            calculateGlobalInput(inputs, outputLayerWeights.map { it[outputIndex] })
        outputLayer[outputIndex] = sigmoid(globalInput)
    }
    allLayerOutputs.add(outputLayer)

    return allLayerOutputs
}

fun backpropagation(
    expectedOutputs: List<Double>,
    weights: MutableList<List<MutableList<Double>>>,
    allLayerOutputs: List<List<Double>>,
    learningRate: Double
) {


    //iau outputurile de la ultimul layer si le scad pe cele asteptate si inmultesc cu derivata functiei sigmoidale
    var previousDelta = allLayerOutputs.last().mapIndexed { i, output ->
        (output - expectedOutputs[i]) * sigmoidDerivat(output)
    }


    // cele doua foruri fac legatura dintre last layer si last hidden layer si modifica weights urile
    // in functie de viteza de invatare, eroarea calculata si stocata in delta si outputurile de pe layerul anterior ( hidden layer )
    for (i in previousDelta.indices) {
        for (j in allLayerOutputs[allLayerOutputs.size - 2].indices) {
            weights.last()[j][i] -= learningRate * previousDelta[i] * allLayerOutputs[allLayerOutputs.size - 2][j]
        }
    }


    // continuam backpropagationul de la penultimul layer pana la primul daca avem mai mult de 1 hidden layer
    for (layerIndex in weights.size - 2 downTo 0) {
        val layerOutputs = allLayerOutputs[layerIndex]

// iau outputurile de pe fiecare neuron din stratul curent si calculez suma, previousDelta are erorile
        // dp stratul anterior care va face o suma pt fiecare k ( valoare din delta )
        // anume, eroarea inmultita cu greutatea de pe stratul current ( care e definita in for) cu stratul urmator
        // practic, inmulteste doua valori, weights fiind legatura dintre neuronii dintre straturi
        val layerDelta = layerOutputs.mapIndexed { neuronIndex, output ->
            val weightedSum = previousDelta.indices.sumOf { k ->
                previousDelta[k] * weights[layerIndex + 1][neuronIndex][k]
            }
            sigmoidDerivat(output) * weightedSum
        }


        // ajustarea weights ca la cel cu .last() pt scaderea erorii
        if (layerIndex > 0) {
            for (i in layerDelta.indices) {
                for (j in allLayerOutputs[layerIndex - 1].indices) {
                    weights[layerIndex][j][i] -= learningRate * layerDelta[i] * allLayerOutputs[layerIndex - 1][j]
                }
            }
        }

        previousDelta = layerDelta // actualizare delta, trecere la alt strat
    }
}


//fun calculateLoss(real: List<Double>, predicted: List<Double>): Double {
//    val n = real.size
//    var sumsquarredErrors = 0.0
//
//    for (i in 0 until n){
//        val error = real[i] - predicted[i]
//        sumsquarredErrors += (error * error)
//    }
//    return sumsquarredErrors / n
//}

fun calculateLoss(real: List<Double>, predicted: List<Double>): Double {
    return -real.zip(predicted).sumOf { (realValue, predictedValue) ->
        realValue * ln(predictedValue)
    }
}