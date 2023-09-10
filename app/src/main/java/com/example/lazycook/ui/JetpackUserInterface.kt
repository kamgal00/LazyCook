package com.example.lazycook.ui

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.lazycook.logic.ActionWithContinuation
import com.example.lazycook.logic.returnables.GuiAction
import com.example.lazycook.logic.returnables.GuiCallResult
import com.example.lazycook.logic.GuiElement
import com.example.lazycook.logic.apis.UserInteractions
import com.example.lazycook.logic.returnables.CalendarScreen
import com.example.lazycook.logic.returnables.Cancel
import com.example.lazycook.logic.returnables.Navigate
import com.example.lazycook.logic.returnables.NavigationDestination
import com.example.lazycook.logic.returnables.RecipesScreen
import com.example.lazycook.ui.components.utils.AsButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.lazycook.logic.returnables.PhotoGallery
import com.example.lazycook.logic.returnables.SafeGuiCallResult
import com.example.lazycook.logic.returnables.Select
import com.example.lazycook.logic.returnables.ShoppingListsScreen
import com.example.lazycook.logic.returnables.TagsScreen

typealias ActionConsumer = (GuiCallResult) -> Unit

operator fun ActionConsumer.div(r: GuiCallResult): () -> Unit = { this(r) }

class JetpackUserInterface : UserInteractions {


    private var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>? = null

    fun updateComponentActivity(componentActivity: ComponentActivity) {
        pickMedia =
            componentActivity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                currentActionConsumer.value(Select(PhotoGallery(uri)))
            }
    }

    private val currentGuiElement: MutableState<GuiElement?> = mutableStateOf(null)
    private val currentActionConsumer: MutableState<ActionConsumer> = mutableStateOf({})
    private val additionalDesc: MutableState<String?> = mutableStateOf(null)
    private val additionalOp: MutableState<List<Pair<String, SafeGuiCallResult>>> =
        mutableStateOf(emptyList())

    private val toastMessage: MutableState<String?> = mutableStateOf(null)
    private val currentScreen: MutableState<NavigationDestination> = mutableStateOf(CalendarScreen)

    override fun askForConfirmation(msg: String): GuiAction {
        TODO("Not yet implemented")
    }

    override fun show(
        guiElement: GuiElement,
        additionalDescription: String?,
        additionalOperations: List<Pair<String, SafeGuiCallResult>>
    ): GuiAction = ActionWithContinuation {
        currentGuiElement.value = guiElement
        additionalDesc.value = additionalDescription
        additionalOp.value = additionalOperations
        currentActionConsumer.value = it
    }

    override fun notifyCurrentScreen(screen: NavigationDestination): ActionWithContinuation<Unit> =
        ActionWithContinuation {
            currentScreen.value = screen
            it(Unit)
        }


    override fun printMessage(msg: String): ActionWithContinuation<Unit> = ActionWithContinuation {
        toastMessage.value = msg
        it(Unit)
    }


    @Composable
    fun MainScreen() {
        val currGui by remember { currentGuiElement }
        val actionConsumer by remember { currentActionConsumer }
        val description by remember { additionalDesc }
        val operations by remember { additionalOp }

        val toastMsg by remember { toastMessage }
        val context = LocalContext.current
        val currentScreen by remember { currentScreen }

        val tabs = listOf(CalendarScreen, RecipesScreen, ShoppingListsScreen, TagsScreen)
        toastMsg?.let {
            val toast = Toast.makeText(context, toastMsg, Toast.LENGTH_LONG)
            toast.show()
            toastMessage.value = null
        }

        BackHandler { actionConsumer(Cancel) }


        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = tabs.indexOf(currentScreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { _, screen ->
                    Tab(text = { Text(screen.name) },
                        selected = currentScreen == screen,
                        onClick = { actionConsumer(Navigate(screen)) },
                        icon = {
                            when (screen) {
                                CalendarScreen -> Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null
                                )

                                RecipesScreen -> Icon(
                                    imageVector = Icons.Default.Fastfood,
                                    contentDescription = null
                                )

                                ShoppingListsScreen -> Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = null
                                )

                                TagsScreen -> Icon(
                                    imageVector = Icons.Default.Tag,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            }
            description?.let {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .border(1.dp, Color.Black)
                        .padding(10.dp)
                        .padding(horizontal = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(it, fontSize = 20.sp)
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    DrawGuiElement(
                        currGui,
                        actionConsumer,
                        pickMedia ?: ComponentActivity().registerForActivityResult(
                            ActivityResultContracts.PickVisualMedia()
                        ) {})
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(25.dp)
                ) {
                    operations.map {
                        Operation(
                            it.first,
                            actionConsumer / it.second
                        )
                    }.forEach { it.AsButton() }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    JetpackUserInterface().MainScreen()
}