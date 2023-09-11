package com.example.lazycook.ui.components.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lazycook.logic.dataclasses.Meal
import com.example.lazycook.logic.dataclasses.MealDate
import com.example.lazycook.logic.dataclasses.MealTime
import com.example.lazycook.logic.actions.CalendarElement
import com.example.lazycook.logic.actions.CalendarSlot
import com.example.lazycook.logic.returnables.Create
import com.example.lazycook.logic.returnables.Select
import com.example.lazycook.ui.components.utils.ignoreConstraints
import com.example.lazycook.ui.components.utils.SampleMeal
import com.example.lazycook.ui.components.utils.SampleMealTime
import com.example.lazycook.other.addDays
import com.example.lazycook.other.formatAsHour
import com.example.lazycook.other.toCalendar
import com.example.lazycook.ui.ActionConsumer
import com.example.lazycook.ui.components.widgets.AsCalendarDateTile
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale

@Preview(showBackground = true)
@Composable
fun MealCalendarPreview() {
    MealCalendar(
        CalendarElement(
            mealTimes = listOf(
                SampleMealTime.Breakfast,
                SampleMealTime.Lunch,
                SampleMealTime.SecondBreakfast,
                SampleMealTime.Dinner
            ),
            meals = listOf(
                SampleMeal.SampleBreakfast,
                SampleMeal.SampleLunch,
                SampleMeal.SampleDinner
            ),
            currentDate = MealDate(
                DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY)
                    .parse("10.09.2023")!!
            )
        ),
        actionConsumer = {}
    )
}

@Composable
fun MealCalendar(
    element: CalendarElement,
    actionConsumer: ActionConsumer,
    selectedSlot: CalendarSlot? = null
) {
    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    val headerHeight = 200.dp
    val firstColumnWidth = 90.dp
    val columnWidth = 70.dp
    val rowHeight = 70.dp
    val hourRowHeight = 30.dp

    val days = 14

    val mealTimeToMeal = element.meals.groupBy { it.mealTime.id }

    val sortedMealTimes = element.mealTimes.sortedBy { it.time }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .width(firstColumnWidth)
                .fillMaxHeight()
        ) {
            Spacer(modifier = Modifier.size(firstColumnWidth, headerHeight))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScroll)
            ) {
                val calendar = Calendar.getInstance()
                calendar.time = element.currentDate.date
                repeat(days) {
                    calendar.AsCalendarDateTile(Modifier.size(firstColumnWidth, rowHeight))
                    calendar.addDays(1)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(horizontalScroll)
        ) {
            Row(Modifier.height(headerHeight)) {
                sortedMealTimes.forEach {
                    Box(
                        modifier = Modifier
                            .size(columnWidth, headerHeight)
                            .border(1.dp, color = Color.Black)
                            .clickable {
                                actionConsumer(Select(it))
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .align(TopCenter)
                                .size(columnWidth, headerHeight - hourRowHeight),
                            contentAlignment = Center
                        ) {
                            Text(
                                text = it.name,
                                modifier = Modifier
                                    .ignoreConstraints()
                                    .width(headerHeight - hourRowHeight)
                                    .rotate(-90f),
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp
                            )
                        }
                        Text(
                            text = it.time.formatAsHour(),
                            modifier = Modifier
                                .align(BottomCenter)
                                .height(hourRowHeight),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(columnWidth, headerHeight)
//                        .border(1.dp, color = Color.Black)
                        .clickable { actionConsumer(Create(MealTime(0, 0, null, null))) },
                    contentAlignment = Center
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScroll)
            ) {
                val calendar = Calendar.getInstance()
                calendar.time = element.currentDate.date
                calendar.set(Calendar.HOUR_OF_DAY, 12)
                repeat(days) {
                    val currentTimeStamp = calendar.time
                    val currentMidnight = Calendar.getInstance().apply {
                        time = calendar.time
                        set(Calendar.HOUR_OF_DAY, 0)
                    }.time
                    Row(
                        modifier = Modifier
                            .height(rowHeight)
                            .fillMaxWidth()
                    ) {
                        sortedMealTimes.forEach { mealTime ->
                            Box(
                                modifier = Modifier
                                    .size(columnWidth, rowHeight)
                                    .border(1.dp, Color.LightGray),
                                contentAlignment = Center
                            ) {
                                val isSelectedSlot = selectedSlot != null &&
                                        currentMidnight == selectedSlot.date.date &&
                                        mealTime.id == selectedSlot.mealTime.id
                                mealTimeToMeal[mealTime.id]?.firstOrNull {
                                    currentTimeStamp.after(
                                        it.startDate.date
                                    ) && currentTimeStamp.before(
                                        it.endDate.date
                                    )
                                }?.let {
                                    CalendarMealTile(
                                        meal = it, modifier = Modifier
                                            .fillMaxSize()
                                            .padding(5.dp)
                                            .clickable { actionConsumer(Select(it)) }
                                    )
                                } ?: Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = if (isSelectedSlot) Color.Blue else Color.LightGray,
                                    modifier = Modifier.clickable {
                                        actionConsumer(
                                            Select(
                                                CalendarSlot(
                                                    MealDate(currentMidnight), mealTime
                                                )
                                            )
                                        )
                                    }
                                )

                            }
                        }
                    }
                    calendar.addDays(1)
                }
            }
        }
    }
}

@Composable
fun CalendarMealTile(
    meal: Meal,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(Color.Green),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = meal.photo,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                )
                .padding(3.dp)
        )
//                ?:
//        Icon(imageVector = Icons.Default.Check, contentDescription = null)
    }
}