package com.example.lazycook

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lazycook.logic.actions.mainLoop
import com.example.lazycook.logic.apis.Apis
import com.example.lazycook.room.RoomDatabaseInterface
import com.example.lazycook.ui.JetpackUserInterface

import android.Manifest
import androidx.activity.viewModels

private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123

class MainActivity : ComponentActivity() {

    private val androidViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        }

        androidViewModel.updateComponentActivity(this)
        setContent {
            androidViewModel.DrawMainScreen()
        }
    }
}
