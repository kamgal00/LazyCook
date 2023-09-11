package com.example.lazycook.ui.components.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lazycook.logic.actions.ConfirmationElement
import com.example.lazycook.logic.actions.ConfirmationResult
import com.example.lazycook.logic.dataclasses.TitleAndDescription
import com.example.lazycook.logic.returnables.Edit
import com.example.lazycook.logic.returnables.Select
import com.example.lazycook.ui.ActionConsumer


@Preview(showBackground = true)
@Composable
fun TitleEditorPreview() {
    TitleAndDescriptionEditor(
        titleAndDescription = TitleAndDescription(
            "Twój stary",
            "pijany ".repeat(100)
        ), actionConsumer = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleAndDescriptionEditor(
    titleAndDescription: TitleAndDescription,
    actionConsumer: ActionConsumer,
) {
    var title by remember { mutableStateOf(titleAndDescription.title) }
    var description by remember { mutableStateOf(titleAndDescription.description ?: "") }
    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text("Title: ")
            TextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )
            Text("Description: ")
            TextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            )
        }
        Button(
            onClick = { actionConsumer(Edit(TitleAndDescription(title, description))) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Save")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmationPreview() {
    ConfirmationWindow(
        msg = ConfirmationElement("Are you sure you want to delete?"),
        actionConsumer = {})
}

@Composable
fun ConfirmationWindow(
    msg: ConfirmationElement,
    actionConsumer: ActionConsumer
) {
    Box(Modifier.padding(20.dp).fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(msg.message, textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Button(
                    onClick = { actionConsumer(Select(ConfirmationResult(true))) },
                    modifier = Modifier.padding(horizontal = 20.dp).width(IntrinsicSize.Min).weight(1f)
                ) {
                    Text(text = "Yes")
                }
                Button(
                    onClick = { actionConsumer(Select(ConfirmationResult(false))) },
                    modifier = Modifier.padding(horizontal = 20.dp).width(IntrinsicSize.Min).weight(1f)
                ) {
                    Text(text = "No")
                }
            }
        }
    }
}