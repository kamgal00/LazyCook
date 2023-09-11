package com.example.lazycook.logic.actions

import com.example.lazycook.logic.dataclasses.IngredientList
import com.example.lazycook.logic.dataclasses.Meal
import com.example.lazycook.logic.dataclasses.MealDate
import com.example.lazycook.logic.dataclasses.MealTime
import com.example.lazycook.logic.dataclasses.TagList
import com.example.lazycook.logic.ActionWithContinuation
import com.example.lazycook.logic.GuiElement
import com.example.lazycook.logic.ReturnValue
import com.example.lazycook.logic.apis.ProgramContext
import com.example.lazycook.logic.apis.ExitContext
import com.example.lazycook.logic.apis.defaultCallCC
import com.example.lazycook.logic.apis.whileCallCC
import com.example.lazycook.logic.emptyAction
import com.example.lazycook.logic.ret
import com.example.lazycook.other.addDays
import com.example.lazycook.other.putInList
import com.example.lazycook.other.toCalendar
import java.util.Calendar


data class CalendarElement(
    val mealTimes: List<MealTime>,
    val meals: List<Meal>,
    val currentDate: MealDate,
) : GuiElement

data class DateRangeSelector(
    val mealTimes: List<MealTime>,
    val currentDate: MealDate,
    val selectedSlot: CalendarSlot
) : GuiElement

data class CalendarSlot(val date: MealDate, val mealTime: MealTime) : ReturnValue

fun ProgramContext.showCalendar(): ActionWithContinuation<Unit> =
    whileCallCC(Unit) { _, _ ->
        defaultCallCC(
            CalendarElement(
                emptyList(),
                emptyList(),
                MealDate(Calendar.getInstance().time)
            )
        ) {
            databaseInteractions.getList(MealTime::class) databaseThen { mealTimes ->
                databaseInteractions.getList(Meal::class) databaseThen { meals ->
                    ret(CalendarElement(mealTimes, meals, MealDate(Calendar.getInstance().time)))
                }
            }
        } then { calendar ->
            userInteractions.show(calendar) checkCases {
                select(MealTime::class) {
                    showMealTime(it) then { ret(Unit) }
                }
                create(MealTime::class) {
                    defaultCallCC(Unit) {
                        databaseInteractions.add(MealTime(0, 8 * 60, null, null)) databaseThen {
                            showMealTime(it)
                        } then { ret(Unit) }
                    }
                }
                select(Meal::class) {
                    showMeal(it)
                }
                select(CalendarSlot::class) { startCalendarSlot ->
                    defaultCallCC(Unit) {
                        getCalendarSlot(
                            calendar.mealTimes,
                            calendar.currentDate,
                            startCalendarSlot
                        ) then { calendarSlot ->
                            calendarSlot.date.toCalendar().addDays(1).time.let {
                                ret(CalendarSlot(MealDate(it), calendarSlot.mealTime))
                            }
                        } then { endCalendarSlot ->
                            getIngredient(
                                startingTags = TagList(startCalendarSlot.mealTime.relatedTag.putInList()),
                                defaultAmountProducer = getDefaultMeasuresProducerForMeal(
                                    startCalendarSlot.date,
                                    endCalendarSlot.date,
                                    startCalendarSlot.mealTime
                                )
                            ) then { ingredient ->
                                databaseInteractions.add(
                                    Meal(
                                        0,
                                        startCalendarSlot.date,
                                        endCalendarSlot.date,
                                        startCalendarSlot.mealTime,
                                        ingredient.recipe.photo
                                    )
                                ) databaseThen { meal ->
                                    databaseInteractions.saveRelatedIngredients(
                                        meal.asIdWithType(),
                                        IngredientList(listOf(ingredient))
                                    ) databaseThen { showMeal(meal) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

fun ExitContext.getCalendarSlot(
    mealTimes: List<MealTime>,
    currentDate: MealDate,
    selected: CalendarSlot
): ActionWithContinuation<CalendarSlot> =
    userInteractions.show(
        DateRangeSelector(mealTimes, currentDate, selected),
        additionalDescription = "Select end date:"
    ) checkCases {
        select(CalendarSlot::class) {
            if (it.mealTime.id == selected.mealTime.id && !it.date.date.before(selected.date.date)) ret(
                it
            )
            else emptyAction()
        }
    }

