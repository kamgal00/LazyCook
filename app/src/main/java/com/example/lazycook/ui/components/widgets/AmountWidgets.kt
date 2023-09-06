package com.example.lazycook.ui.components.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.lazycook.logic.dataclasses.Amount
import com.example.lazycook.ui.ActionConsumer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lazycook.logic.dataclasses.AmountList
import com.example.lazycook.ui.Operation
import com.example.lazycook.logic.actions.AmountEditor
import com.example.lazycook.logic.actions.AmountSelector
import com.example.lazycook.logic.returnables.Create
import com.example.lazycook.logic.returnables.Delete
import com.example.lazycook.logic.returnables.Edit
import com.example.lazycook.logic.returnables.ExceptionGuiResult
import com.example.lazycook.logic.returnables.Select
import kotlin.math.max

@Preview(showBackground = true)
@Composable
fun AmountUtilsPreview() {
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f)) {
            EnterUnitAndAmount(
                amountEditor = AmountEditor(Amount("kcal", 400.0)),
                actionConsumer = {})
        }
        Box(Modifier.weight(1f)) {
            EnterUnitAndAmount(
                amountEditor = AmountEditor(null),
                actionConsumer = {})
        }
        Box(Modifier.weight(1f)) {
            ChooseAmountView(
                selector = AmountSelector(listOf("units", "kcal", "g"), Amount("kcal", 1000.0)),
                actionConsumer = {}
            )
        }
        Box(Modifier.weight(1f)) {
            ChooseAmountView(
                selector = AmountSelector(listOf("units", "kcal", "g"), null),
                actionConsumer = {}
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterUnitAndAmount(
    amountEditor: AmountEditor,
    actionConsumer: ActionConsumer
) {
    var unitText by remember { mutableStateOf(amountEditor.amount?.unit ?: "") }
    var amountText by remember { mutableStateOf(amountEditor.amount?.amount?.toString() ?: "") }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .align(Alignment.Center)
                .width(IntrinsicSize.Min)
                .padding(10.dp)
        ) {
            Row {
                TextField(
                    value = unitText,
                    onValueChange = { unitText = it },
                    enabled = amountEditor.amount == null,
                    modifier = Modifier.width(100.dp)
                )
                Spacer(modifier = Modifier.size(20.dp))
                TextField(
                    value = amountText,
                    onValueChange = { amountText = it }
                )
            }
            Spacer(modifier = Modifier.size(20.dp))
            if (amountEditor.amount == null) {
                Button(onClick = {
                    amountText.toDoubleOrNull().let {
                        if (it == null || it < 1) actionConsumer(
                            ExceptionGuiResult(IllegalArgumentException("Entered amount is not a positive integer"))
                        )
                        else actionConsumer(Create(Amount(unitText, it)))
                    }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Add")
                }
            } else {
                Button(onClick = {
                    amountText.toDoubleOrNull().let {
                        if (it == null || it < 1) actionConsumer(
                            ExceptionGuiResult(IllegalArgumentException("Entered amount is not a positive integer"))
                        )
                        else actionConsumer(Edit(Amount(unitText, it)))
                    }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Save")
                }
                Button(onClick = {
                    actionConsumer(Delete(amountEditor.amount))
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Delete")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseAmountView(
    selector: AmountSelector,
    actionConsumer: ActionConsumer
) {

    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember {
        mutableIntStateOf(selector.previousAmount?.let {
            selector.possibleMeasures.indexOf(
                it.unit
            ).let { max(it, 0) }
        } ?: selector.possibleMeasures.indexOf("unit"))
    }
    var amountText by remember {
        mutableStateOf(selector.previousAmount?.amount.let { it?.toString() ?: "1" })
    }
    if (selector.possibleMeasures.isEmpty()) actionConsumer(
        ExceptionGuiResult(
            IllegalArgumentException("No measures to choose from!")
        )
    )
    else {

        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .align(Alignment.Center)
                    .width(IntrinsicSize.Min)
                    .padding(10.dp)
            ) {
                Row {
                    Box(Modifier.width(100.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = {
                                expanded = !expanded
                            },
                        ) {
                            TextField(
                                value = selector.possibleMeasures[selectedIndex],
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                selector.possibleMeasures.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(text = item) },
                                        onClick = {
                                            selectedIndex = selector.possibleMeasures.indexOf(item)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(20.dp))
                    TextField(
                        value = amountText,
                        onValueChange = { amountText = it }
                    )
                }
                Spacer(modifier = Modifier.size(20.dp))
                if (selector.previousAmount == null) {
                    Button(onClick = {
                        amountText.toDoubleOrNull().let {
                            if (it == null || it < 1) actionConsumer(
                                ExceptionGuiResult(IllegalArgumentException("Entered amount is not a positive integer"))
                            )
                            else actionConsumer(
                                Select(Amount(selector.possibleMeasures[selectedIndex], it))
                            )
                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Add")
                    }
                } else {
                    Button(onClick = {
                        amountText.toDoubleOrNull().let {
                            if (it == null || it < 1) actionConsumer(
                                ExceptionGuiResult(IllegalArgumentException("Entered amount is not a positive integer"))
                            )
                            else actionConsumer(
                                Select(Amount(selector.possibleMeasures[selectedIndex], it))
                            )
                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Save")
                    }
                    Button(onClick = {
                        actionConsumer(Delete(selector.previousAmount))
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShowMeasures(
    list: AmountList,
    actionConsumer: ActionConsumer,
    description: String = "Measures",
    actions: List<Operation<ImageVector>> = emptyList()
) {

    OverviewBox(
        description = description,
        actions = actions
    ) {
        val vertScrollState = rememberScrollState()
        FlowRow(
            modifier = Modifier
                .padding(10.dp)
                .height(100.dp)
                .verticalScroll(vertScrollState)
        ) {
            list.listElements.forEach {
                Text(
                    text = it.amount.toString() + " " + it.unit,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .clickable { actionConsumer(Edit(it)) }
                        .padding(5.dp)
                        .clip(
                            RoundedCornerShape(5.dp)
                        )
                        .background(Color.LightGray)
                        .padding(5.dp)
                )
            }

        }
    }
}
