package com.example.lazycook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.lazycook.logic.actions.mainLoop
import com.example.lazycook.logic.apis.Apis
import com.example.lazycook.room.RoomDatabaseInterface
import com.example.lazycook.ui.JetpackUserInterface
import com.example.lazycook.ui.components.utils.DummyDatabaseInteractions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val jui = JetpackUserInterface()
        val apis = Apis(
            userInteractions = jui,
            databaseInteractions = RoomDatabaseInterface(applicationContext)
        )
        setContent {
            jui.draw()
        }
        apis.mainLoop().run { finish() }
    }
}
