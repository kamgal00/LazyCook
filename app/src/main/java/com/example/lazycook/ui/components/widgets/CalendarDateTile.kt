package com.example.lazycook.ui.components.widgets

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lazycook.other.format
import java.util.Calendar

@Composable
fun Calendar.AsCalendarDateTile(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(1.dp, Color.Black)
            .padding(top = 5.dp),
        horizontalAlignment = CenterHorizontally
    ) {
        Text(text = format())
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Center),
                text = format("EEE"),
                fontSize = 23.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
