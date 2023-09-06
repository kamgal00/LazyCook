package com.example.lazycook.ui.components.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lazycook.ui.Operation
import com.example.lazycook.logic.dataclasses.Tag
import com.example.lazycook.logic.dataclasses.TagList
import com.example.lazycook.logic.actions.TagSelector
import com.example.lazycook.logic.returnables.Select
import com.example.lazycook.ui.ActionConsumer
import com.example.lazycook.ui.components.utils.SampleTag

@Preview(showBackground = true)
@Composable
fun MealTypeTagPreview() {
    MealTypeTag(SampleTag.Breakfast, circleColor = Color.Black, tagColor = Color.LightGray)
}

@Composable
fun MealTypeTag(
    tag: Tag,
    modifier: Modifier = Modifier,
    circleColor: Color,
    tagColor: Color
) {
    Row(
        modifier = modifier
            .height(30.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(tagColor)
            .padding(end = 10.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .padding(10.dp)
                .clip(CircleShape)
                .background(circleColor)
        )
        Text(text = tag.name)
    }
}

@Preview(showBackground = true)
@Composable
fun TagSelectorPreview() {
    TagSelectionView(
        selector = TagSelector(
            TagList(listOf(SampleTag.Vegetarian)),
            TagList(listOf(SampleTag.Vegetarian, SampleTag.Dinner, SampleTag.SecondBreakfast))
        ), actionConsumer = {})
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagSelectionView(
    selector: TagSelector,
    actionConsumer: ActionConsumer,
    description: String = "Tags",
    actions: List<Operation<ImageVector>> = emptyList()
) {
    OverviewBox(
        description = description,
        actions = actions
    ) {
        FlowRow(modifier = Modifier.padding(10.dp)) {
            selector.all.elements.forEach {
                MealTypeTag(
                    it,
                    circleColor = Color.White,
                    tagColor =
                    if (selector.currentlySelected.elements.contains(it)) Color.Yellow else
                        Color.LightGray,
                    modifier = Modifier
                        .padding(5.dp)
                        .clickable { actionConsumer(Select(it)) }
                )
            }

        }
    }
}