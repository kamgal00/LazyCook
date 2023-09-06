package com.example.lazycook.logic.dataclasses

import com.example.lazycook.logic.DataObject

data class Tag(
    val id: Int,
    val name: String
) : DataObject

data class TagList(override val elements: List<Tag>) : DataList<Tag>(Tag::class, elements)