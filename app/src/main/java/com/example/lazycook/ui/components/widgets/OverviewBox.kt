package com.example.lazycook.ui.components.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lazycook.ui.Operation
import com.example.lazycook.ui.components.utils.AsIconButton

@Composable
fun OverviewBox(
    description: String,
    modifier: Modifier = Modifier,
    descriptionFontSize: TextUnit = 20.sp,
    actions: List<Operation<ImageVector>>,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = description,
                modifier = Modifier
                    .weight(1f),
                textAlign = TextAlign.Center,
                fontSize = descriptionFontSize
            )
            actions.forEach { it.AsIconButton() }
        }
        Divider()
        content()
    }
}