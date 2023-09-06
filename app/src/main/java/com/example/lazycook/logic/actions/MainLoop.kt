package com.example.lazycook.logic.actions

import com.example.lazycook.logic.ActionWithContinuation
import com.example.lazycook.logic.EmptyThrower
import com.example.lazycook.logic.apis.Apis
import com.example.lazycook.logic.apis.ProgramContext
import com.example.lazycook.logic.apis.whileCallCC
import com.example.lazycook.logic.callCC
import com.example.lazycook.logic.returnables.CalendarScreen
import com.example.lazycook.logic.returnables.NavigationDestination


fun Apis.mainLoop(): ActionWithContinuation<NavigationDestination> = callCC { outerScope ->
    fun loop(screen: NavigationDestination): ActionWithContinuation<NavigationDestination> =
        callCC { navigationScope ->
            with(ProgramContext(userInteractions, databaseInteractions, navigationScope)) {
                userInteractions.notifyCurrentScreen(screen) then {
                    screen.showScreen(this)
                } then {
                    outerScope.exit(CalendarScreen)
                }
            }
        } then { loop(it) }
    loop(CalendarScreen)
}