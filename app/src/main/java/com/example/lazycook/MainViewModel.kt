package com.example.lazycook

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.lazycook.logic.actions.mainLoop
import com.example.lazycook.logic.apis.Apis
import com.example.lazycook.room.RoomDatabaseInterface
import com.example.lazycook.ui.JetpackUserInterface

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var onFinish: ()->Unit = {}
    private val jui = JetpackUserInterface()
    private val db = RoomDatabaseInterface(application.applicationContext)
    private val apis = Apis(
        userInteractions = jui,
        databaseInteractions = db
    )

    fun updateComponentActivity(ca: ComponentActivity) {
        jui.updateComponentActivity(ca)
        onFinish = { ca.finish() }
    }

    @Composable
    fun DrawMainScreen() = jui.MainScreen()

    init {
        apis.mainLoop().run { onFinish() }
    }

}