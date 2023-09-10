package com.example.lazycook.ui.components.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lazycook.logic.actions.FullInfoMeal
import com.example.lazycook.logic.returnables.Delete
import com.example.lazycook.other.addDays
import com.example.lazycook.ui.components.utils.SampleMeal
import com.example.lazycook.other.formatAsHour
import com.example.lazycook.other.toCalendar
import com.example.lazycook.ui.ActionConsumer
import com.example.lazycook.ui.components.utils.SampleRecipe
import com.example.lazycook.ui.components.widgets.AsCalendarDateTile
import com.example.lazycook.ui.components.widgets.IngredientListWidget
import com.example.lazycook.ui.editOperation

@Preview(showBackground = true)
@Composable
fun MealViewPreview() {
    MealView(
        fullMeal = FullInfoMeal(SampleMeal.SampleBreakfast, SampleRecipe.SampleIngredientList),
        actionConsumer = {},
    )
}

@Composable
fun MealView(
    fullMeal: FullInfoMeal,
    actionConsumer: ActionConsumer,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = fullMeal.meal.mealTime.time.formatAsHour(),
                            fontSize = 20.sp
                        )
                        Text(
                            text = fullMeal.meal.mealTime.relatedTag?.name.orEmpty(),
                            fontSize = 30.sp
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Start:")
                    val tileModifier = Modifier.size(90.dp, 70.dp)
                    fullMeal.meal.startDate.toCalendar().AsCalendarDateTile(tileModifier)
                    Spacer(modifier = Modifier.size(10.dp))
                    Text("End:")
                    fullMeal.meal.endDate.toCalendar().addDays(-1).AsCalendarDateTile(tileModifier)
                }
            }
            IngredientListWidget(
                ingredients = fullMeal.ingredientList,
                actionConsumer = actionConsumer,
                description = "Dishes",
                actions = listOf(editOperation(fullMeal.ingredientList, actionConsumer))
            )
        }
//        Column {
//            Button(
//                onClick = { actionConsumer(Delete(fullMeal.meal)) },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text(text = "Delete")
//            }
//        }
    }
}