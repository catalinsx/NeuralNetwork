package com.example.finalproject.normalization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.CsvEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class NormalizedEntry(
    val speed_x: Double,
    val altitude: Double,
    val course: Double,
    val courseVariation: Double,
    val accX: Double,
    val accY: Double,
    val accZ: Double,
    val roll: Double,
    val pitch: Double,
    val yaw: Double,
    val distanceAheadVehicle: Double,
    val timeOfImpactAheadVehicle: Double,
    val behaviour: List<Int> // one-hot encoded
){

    val inputs: List<Double>
        get() = listOf(
            speed_x, altitude, course, courseVariation,
            accX, accY, accZ,
            roll, pitch, yaw, distanceAheadVehicle, timeOfImpactAheadVehicle,
        )

    val outputs: List<Int>
        get() = behaviour
}

class NormalizationViewModel : ViewModel() {


    var normalizedEntries: List<NormalizedEntry> = emptyList()
    var normalizedTestingEntries: List<NormalizedEntry> = emptyList()


    fun normalizeData(entries: List<CsvEntry>) {
        viewModelScope.launch {
            normalizedEntries = withContext(Dispatchers.Default) {
                normalization(entries)
            }
        }
    }

    fun normalizeTestingData(entries: List<CsvEntry>) {
        viewModelScope.launch {
            normalizedTestingEntries = withContext(Dispatchers.Default) {
                normalization(entries)
            }
        }
    }

    private fun normalizeValue(value: Double, min: Double, max: Double): Double {
        return if (max != min) {
            (value - min) / (max - min)
        } else {
            0.0
        }
    }


    private fun encodeBehaviour(behaviour: String) : List<Int>{
        return when(behaviour){
            "NORMAL" -> listOf(1, 0, 0)
            "AGGRESSIVE" -> listOf(0, 1, 0)
            "DROWSY" -> listOf(0, 0, 1)
            else -> listOf(0, 0, 0) // doar ca nu merge when fara else desi nu e folosibil
        }
    }


    private fun normalization(entries: List<CsvEntry>): List<NormalizedEntry> {
        val minSpeedX = entries.minOf { it.speed_x }
        val maxSpeedX = entries.maxOf { it.speed_x }
        val minAltitude = entries.minOf { it.altitude }
        val maxAltitude = entries.maxOf { it.altitude }
        val minCourse = entries.minOf { it.course }
        val maxCourse = entries.maxOf { it.course }
        val minCourseVariation = entries.minOf { it.courseVariation }
        val maxCourseVariation = entries.maxOf { it.courseVariation }
        val minAccX = entries.minOf { it.accX }
        val maxAccX = entries.maxOf { it.accX }
        val minAccY = entries.minOf { it.accY }
        val maxAccY = entries.maxOf { it.accY }
        val minAccZ = entries.minOf { it.accZ }
        val maxAccZ = entries.maxOf { it.accZ }
        val minRoll = entries.minOf { it.roll }
        val maxRoll = entries.maxOf { it.roll }
        val minPitch = entries.minOf { it.pitch }
        val maxPitch = entries.maxOf { it.pitch }
        val minYaw = entries.minOf { it.yaw }
        val maxYaw = entries.maxOf { it.yaw }
        val minDistanceAheadVehicle = entries.minOf { it.distanceAheadVehicle }
        val maxDistanceAheadVehicle = entries.maxOf { it.distanceAheadVehicle }
        val minTimeOfImpactAheadVehicle = entries.minOf { it.timeOfImpactAheadVehicle }
        val maxTimeOfImpactAheadVehicle = entries.maxOf { it.timeOfImpactAheadVehicle }

        return entries.map { entry ->
            NormalizedEntry(
                speed_x = normalizeValue(entry.speed_x, minSpeedX, maxSpeedX),
                altitude = normalizeValue(entry.altitude, minAltitude, maxAltitude),
                course = normalizeValue(entry.course, minCourse, maxCourse),
                courseVariation = normalizeValue(entry.courseVariation, minCourseVariation, maxCourseVariation),
                accX = normalizeValue(entry.accX, minAccX, maxAccX),
                accY = normalizeValue(entry.accY, minAccY, maxAccY),
                accZ = normalizeValue(entry.accZ, minAccZ, maxAccZ),
                roll = normalizeValue(entry.roll, minRoll, maxRoll),
                pitch = normalizeValue(entry.pitch, minPitch, maxPitch),
                yaw = normalizeValue(entry.yaw, minYaw, maxYaw),
                distanceAheadVehicle = normalizeValue(entry.distanceAheadVehicle, minDistanceAheadVehicle, maxDistanceAheadVehicle),
                timeOfImpactAheadVehicle = normalizeValue(entry.timeOfImpactAheadVehicle, minTimeOfImpactAheadVehicle, maxTimeOfImpactAheadVehicle),
                behaviour = encodeBehaviour(entry.behaviour)
            )
        }
    }
}
