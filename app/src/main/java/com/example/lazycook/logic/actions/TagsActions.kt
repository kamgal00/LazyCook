package com.example.lazycook.logic.actions

import com.example.lazycook.logic.dataclasses.Tag
import com.example.lazycook.logic.dataclasses.TagList
import com.example.lazycook.logic.ActionWithContinuation
import com.example.lazycook.logic.returnables.CheckCasesContext.Companion.checkGuiActionCases
import com.example.lazycook.logic.GuiElement
import com.example.lazycook.logic.returnables.SafeGuiCallResult
import com.example.lazycook.logic.returnables.Select
import com.example.lazycook.logic.apis.ExitContext
import com.example.lazycook.logic.apis.ProgramContext
import com.example.lazycook.logic.apis.defaultCallCC
import com.example.lazycook.logic.apis.whileCallCC
import com.example.lazycook.logic.dataclasses.IngredientList
import com.example.lazycook.logic.ret
import com.example.lazycook.logic.returnables.Delete


data class TagSelector(
    val currentlySelected: TagList,
    val all: TagList
) : GuiElement

fun TagList.toggle(t: Tag): TagList = TagList(
    if (elements.contains(t)) elements - t
    else elements + t
)

fun ExitContext.chooseTags(
    selectedTags: TagList
): ActionWithContinuation<TagList> =
    tagListToTagSelector(selectedTags) then {
        whileCallCC(selectedTags) { tags, loopScope ->
            userInteractions.show(
                it.copy(currentlySelected = tags),
                additionalDescription = "Choose tags and accept",
                additionalOperations = listOf(Pair("Submit", Select(tags)))

            ) checkCases {
                select(Tag::class) {
                    ret(tags.toggle(it))
                }
                select(TagList::class) {
                    loopScope.exit(tags)
                }
            }
        }
    }

fun ExitContext.chooseSingleTag(): ActionWithContinuation<Tag> =
    tagListToTagSelector(
        TagList(emptyList())
    ) then {
        userInteractions.show(it) checkCases {
            select(Tag::class) {
                ret(it)
            }
        }
    }

fun ExitContext.tagListToTagSelector(tags: TagList): ActionWithContinuation<TagSelector> =
    databaseInteractions.getList(Tag::class) databaseThen { ret(TagSelector(tags, TagList(it))) }

data class AllTagsElement(
    val selector: TagSelector
) : GuiElement

fun ProgramContext.showAllTags(): ActionWithContinuation<Unit> =
    whileCallCC(TagList(emptyList())) { list, _ ->
        tagListToTagSelector(list) then { selector ->
            userInteractions.show(
                AllTagsElement(selector),
                additionalOperations = listOf(
                    Pair("Delete selected tags", Delete(selector.currentlySelected))
                )
            ) checkCases {
                create(Tag::class) {
                    defaultCallCC(list) {
                        databaseInteractions.add(it) databaseThen { ret(list) }
                    }
                }
                select(Tag::class) {
                    ret(list.toggle(it))
                }
                delete(TagList::class) { tagList ->
                    defaultCallCC(list) {
                        confirm("Are you sure you want to remove tags ${tagList.elements.joinToString { it.name }}?") then {
                            if (it) {
                                databaseInteractions.delete(tagList) databaseThen {
                                    ret(TagList(emptyList()))
                                }
                            } else ret(list)
                        }
                    }
                }
            }
        }
    } then { ret(Unit) }