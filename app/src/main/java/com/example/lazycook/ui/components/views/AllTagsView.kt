package com.example.lazycook.ui.components.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lazycook.logic.actions.AllTagsElement
import com.example.lazycook.logic.actions.TagSelector
import com.example.lazycook.logic.dataclasses.Tag
import com.example.lazycook.logic.dataclasses.TagList
import com.example.lazycook.logic.dataclasses.TextFieldReturnVal
import com.example.lazycook.ui.ActionConsumer
import com.example.lazycook.ui.components.utils.AsIconButton
import com.example.lazycook.ui.components.utils.SampleTag
import com.example.lazycook.ui.components.widgets.TagSelectionView
import com.example.lazycook.ui.createOperation

@Preview(showBackground = true)
@Composable
fun AllTagsPreview() {
    AllTagsView(
        allTagsElement = AllTagsElement(
            TagSelector(
                TagList(emptyList()),
                SampleTag.SampleTagList
            )
        ), actionConsumer = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTagsView(
    allTagsElement: AllTagsElement,
    actionConsumer: ActionConsumer
) {
    var text by remember { mutableStateOf("") }
    Box(modifier = Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
        Column() {
            Row(Modifier.fillMaxWidth()) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.weight(1f)
                )
                createOperation(Tag(0, text), actionConsumer).AsIconButton()
            }
            TagSelectionView(selector = allTagsElement.selector, actionConsumer = actionConsumer)
        }
    }
}