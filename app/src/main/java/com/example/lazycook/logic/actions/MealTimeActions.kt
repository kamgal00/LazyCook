package com.example.lazycook.logic.actions

import com.example.lazycook.logic.dataclasses.MealTime
import com.example.lazycook.logic.dataclasses.Tag
import com.example.lazycook.logic.ActionWithContinuation
import com.example.lazycook.logic.ReturnValue
import com.example.lazycook.logic.apis.ProgramContext
import com.example.lazycook.logic.apis.defaultCallCC
import com.example.lazycook.logic.apis.whileCallCC
import com.example.lazycook.logic.ret


data class HourReturnValue(val hour: Int) : ReturnValue
data class CaloriesReturnValue(val calories: Int?) : ReturnValue


fun ProgramContext.showMealTime(mealTime: MealTime): ActionWithContinuation<Unit> =
    whileCallCC(mealTime) { mealTime, loopScope ->
        userInteractions.show(mealTime) checkCases {
            edit(HourReturnValue::class) {
                defaultCallCC(mealTime) {
                    databaseInteractions.edit(mealTime.copy(time = it.hour)) databaseThen { ret(it) }
                }
            }
            edit(CaloriesReturnValue::class) {
                defaultCallCC(mealTime) {
                    databaseInteractions.edit(mealTime.copy(calories = it.calories)) databaseThen {
                        ret(it)
                    }
                }
            }
            edit(Tag::class) {
                defaultCallCC(mealTime) {
                    chooseSingleTag() then {
                        databaseInteractions.edit(mealTime.copy(relatedTag = it)) databaseThen {
                            ret(it)
                        }
                    }
                }
            }
            delete {
                defaultCallCC(mealTime) {
                    confirm("Are you sure you want to delete meal time $mealTime?") then {
                        if (it) {
                            databaseInteractions.delete(mealTime) databaseThen {
                                loopScope.exit(mealTime)
                            }
                        } else ret(mealTime)
                    }
                }
            }
        }
    } then { ret(Unit) }
