package com.example.lazycook.ui.components.utils

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.lazycook.ui.Operation

fun Modifier.noMinIntrinsicHeight() = this then object : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult = measurable.measure(constraints)
        .let { layout(it.width, it.height) { it.placeRelative(0, 0) } }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ): Int = 0
}

fun Modifier.ignoreConstraints() =
    layout { measurable, constraints ->
        measurable.measure(Constraints(0, Constraints.Infinity, 0, Constraints.Infinity))
            .let { layout(it.width, it.height) { it.placeRelative(0, 0) } }
    }

@Composable
fun Operation<String>.AsButton(modifier: Modifier = Modifier) =
    Button(onClick = action, modifier = modifier.fillMaxWidth()) {
        Text(text = additionalInfo)
    }

@Composable
fun Operation<ImageVector>.AsIconButton(modifier: Modifier = Modifier) =
    IconButton(onClick = action, modifier = modifier) {
        Icon(imageVector = additionalInfo, contentDescription = null)
    }

@Composable
fun Uri.AsAsyncImage(modifier: Modifier = Modifier) =
    AsyncImage(
        model = this,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .border(
                width = 1.dp,
                color = Color.LightGray,
            )
            .padding(3.dp)
    )
