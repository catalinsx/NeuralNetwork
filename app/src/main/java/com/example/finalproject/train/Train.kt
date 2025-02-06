package com.example.finalproject.train

import android.annotation.SuppressLint
import android.text.Layout
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.cartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.fixed
import com.patrykandpatrick.vico.compose.common.component.rememberLayeredComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shadow
import com.patrykandpatrick.vico.compose.common.data.rememberExtraLambda
import com.patrykandpatrick.vico.compose.common.dimensions
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shape.markerCorneredShape
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.HorizontalDimensions
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.LayeredComponent
import com.patrykandpatrick.vico.core.common.component.Shadow
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.Corner
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorPosition
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random



@SuppressLint("SuspiciousIndentation")
@Composable
fun Train(viewModel: TrainSettingsViewModel) {
//    val modelProducer = remember { CartesianChartModelProducer() }
//    val errorValues = remember { generateErrorValues(1000) }
//
//    LaunchedEffect(Unit) {
//        withContext(Dispatchers.Default) {
//            modelProducer.runTransaction {
//                lineSeries {
//                    series(x, errorValues)
//                }
//            }
//        }
//    }
//
//    ComposeChart1(modelProducer)

    val errorsPerEpoch = viewModel.errorsPerEpochChart
    val validationPerEpoch = viewModel.validationErrorsPerEpochChart

    val indicatorProperties = HorizontalIndicatorProperties(
        enabled = true,
        textStyle = MaterialTheme.typography.labelSmall,
        count = 5,
        position = IndicatorPosition.Horizontal.Start,
        padding = 32.dp,
        contentBuilder = { indicator->
            "%.5f".format(indicator)
        }
    )

    val popupProperties = PopupProperties(
        enabled = true,
        animationSpec = tween(300),
        duration = 2000L,
        textStyle = MaterialTheme.typography.labelSmall,
        containerColor = Color.White,
        cornerRadius = 8.dp,
        contentHorizontalPadding = 4.dp,
        contentVerticalPadding = 2.dp,
        contentBuilder = { value->
            "%.7f".format(value)
        }
    )

    val labelProperties = LabelProperties(
        enabled = true,
        textStyle = MaterialTheme.typography.labelSmall,
        labels = listOf("100", "200", "300", "400", "500", "600", "700", "800", "900", "1000")
    )

           LineChart(
               modifier = Modifier
                   .fillMaxSize()
                   .padding(horizontal = 22.dp),
               data = listOf(
                   Line(
                       label = "Training Error per Epoch",
                       values = errorsPerEpoch,
                       color = SolidColor(Color(0xFF009626)),
                       firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                       secondGradientFillColor = Color.Transparent,
                       strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                       gradientAnimationDelay = 1000,
                       drawStyle = DrawStyle.Stroke(width = 2.dp),
                   ),
                   Line(
                       label = "Validation Error per Epoch",
                       values = validationPerEpoch,
                       color = SolidColor(Color(0xFFFF0000)),
                       firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                       secondGradientFillColor = Color.Transparent,
                       strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                       gradientAnimationDelay = 1000,
                       drawStyle = DrawStyle.Stroke(width = 2.dp),
                   )
               ),
               animationMode = AnimationMode.Together(delayBuilder = { it * 500L }),
               indicatorProperties = indicatorProperties,
               popupProperties = popupProperties,
               labelProperties = labelProperties
           )
}





