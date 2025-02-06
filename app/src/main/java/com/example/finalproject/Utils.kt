package com.example.finalproject

import android.content.Context
import com.opencsv.CSVReader
import java.io.InputStreamReader

data class CsvEntry(
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
    val behaviour: String
)

fun readCsvAndParse(context: Context, fileId: Int): List<CsvEntry> {
    val inputStream = context.resources.openRawResource(fileId)
    val reader = CSVReader(InputStreamReader(inputStream))


    val csvLines = reader.readAll().drop(1)

    return csvLines.map { row ->
        CsvEntry(
            speed_x = row[0].toDouble(),
            altitude = row[1].toDouble(),
            course = row[2].toDouble(),
            courseVariation = row[3].toDouble(),
            accX = row[4].toDouble(),
            accY = row[5].toDouble(),
            accZ = row[6].toDouble(),
            roll = row[7].toDouble(),
            pitch = row[8].toDouble(),
            yaw = row[9].toDouble(),
            distanceAheadVehicle = row[10].toDouble(),
            timeOfImpactAheadVehicle = row[11].toDouble(),
            behaviour = row[12]
        )
    }
}

