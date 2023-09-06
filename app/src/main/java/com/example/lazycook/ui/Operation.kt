package com.example.lazycook.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.lazycook.logic.GuiElement
import com.example.lazycook.logic.ReturnValue
import com.example.lazycook.logic.returnables.Create
import com.example.lazycook.logic.returnables.Delete
import com.example.lazycook.logic.returnables.Edit
import com.example.lazycook.logic.returnables.Select

data class Operation<T>(val additionalInfo: T, val action: () -> Unit)

fun editOperation(elem: ReturnValue, actionConsumer: ActionConsumer) =
    Operation(Icons.Default.Edit) { actionConsumer(Edit(elem)) }

fun selectOperation(elem: ReturnValue, actionConsumer: ActionConsumer) =
    Operation(Icons.Default.Info) { actionConsumer(Select(elem)) }

fun createOperation(elem: ReturnValue, actionConsumer: ActionConsumer) =
    Operation(Icons.Default.Add) { actionConsumer(Create(elem)) }

fun deleteOperation(elem: ReturnValue, actionConsumer: ActionConsumer) =
    Operation(Icons.Default.Delete) { actionConsumer(Delete(elem)) }
