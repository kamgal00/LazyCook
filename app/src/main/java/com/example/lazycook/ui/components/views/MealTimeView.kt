package com.example.lazycook.ui.components.views

import android.app.TimePickerDialog
import android.inputmethodservice.Keyboard
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lazycook.logic.dataclasses.MealTime
import com.example.lazycook.logic.dataclasses.Tag
import com.example.lazycook.logic.actions.CaloriesReturnValue
import com.example.lazycook.logic.actions.HourReturnValue
import com.example.lazycook.logic.returnables.Delete
import com.example.lazycook.logic.returnables.Edit
import com.example.lazycook.logic.returnables.ExceptionGuiResult
import com.example.lazycook.other.formatAsHour
import com.example.lazycook.ui.ActionConsumer
import com.example.lazycook.ui.Operation
import com.example.lazycook.ui.components.utils.AsIconButton
import com.example.lazycook.ui.components.utils.SampleMealTime
import com.example.lazycook.ui.editOperation
import java.lang.IllegalArgumentException
import java.util.Calendar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Preview(showBackground = true)
@Composable
fun MealTimePreview() {
    MealTimeView(mealTime = SampleMealTime.Breakfast, actionConsumer = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealTimeView(
    mealTime: MealTime,
    actionConsumer: ActionConsumer
) {
    val mContext = LocalContext.current

    val mTimePickerDialog = TimePickerDialog(
        mContext,
        { _, mHour: Int, mMinute: Int ->
            actionConsumer(Edit(HourReturnValue(mHour * 60 + mMinute)))
        }, mealTime.time / 60, mealTime.time % 60, false
    )

    var calories by remember {
        mutableStateOf(mealTime.calories?.let { it.toString() } ?: "")
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                            .padding(5.dp)
                            .border(1.dp, Color.Black)
                            .padding(10.dp)
                            .padding(start = 15.dp)
                    ) {
                        Text(text = mealTime.time.formatAsHour(), fontSize = 30.sp)
                        Operation(Icons.Default.Edit) { mTimePickerDialog.show() }.AsIconButton()
                    }
                }
                Box(
                    contentAlignment = Alignment.Center, modifier = Modifier
                        .weight(1f)
                        .padding(10.dp)
                ) {
                    Text(text = mealTime.name, fontSize = 25.sp)
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Calories:", fontSize = 20.sp)
                Spacer(modifier = Modifier.size(10.dp))
                BasicTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    modifier = Modifier
                        .width(100.dp)
                        .padding(3.dp)
                )
                Operation(Icons.Default.Save) {
                    val x = calories.toIntOrNull()
                    actionConsumer(
                        when {
                            (x == null && calories != "") || (x != null && x < 1) -> ExceptionGuiResult(
                                IllegalArgumentException("Not integer or empty string")
                            )

                            x == null -> Edit(CaloriesReturnValue(null))
                            else -> Edit(CaloriesReturnValue(x))
                        }
                    )
                }.AsIconButton()
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tag: ${mealTime.relatedTag?.name ?: "Not selected"}", fontSize = 20.sp)
                Operation(Icons.Default.Edit) {
                    actionConsumer(Edit(mealTime.relatedTag ?: Tag(0, "")))
                }.AsIconButton()
            }
        }
        Button(onClick = { actionConsumer(Delete(mealTime)) }, modifier = Modifier.fillMaxWidth()) {
            Text("Delete")
        }
    }
}