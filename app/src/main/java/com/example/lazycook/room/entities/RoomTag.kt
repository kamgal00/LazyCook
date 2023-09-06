package com.example.lazycook.room.entities

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.example.lazycook.logic.dataclasses.IdWithType
import com.example.lazycook.logic.dataclasses.Tag
import com.example.lazycook.logic.dataclasses.TagList

@Entity
data class RoomTag(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String
)

@Entity(
    foreignKeys = [
        ForeignKey(
            RoomTag::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("tagId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoomTagElement(
    @PrimaryKey(autoGenerate = true) val tagElementId: Int,
    val id: Int,
    val type: String,
    val tagId: Int
)

data class FRoomTag(
    @Embedded
    val roomTagElement: RoomTagElement,
    @Relation(
        entity = RoomTag::class,
        parentColumn = "tagId",
        entityColumn = "id"
    )
    val tag: Tag
)

@Dao
interface TagDao {
    @Delete(entity = RoomTagElement::class)
    fun deleteTagsOf(obj: IdWithType)

    @Insert
    fun insertTagElements(vararg roomTagElements: RoomTagElement)

    fun addTagsTo(obj: IdWithType, tags: TagList) =
        insertTagElements(*tags.elements.map {
            RoomTagElement(
                0,
                obj.id,
                obj.type,
                it.id
            )
        }.toTypedArray())

    @Transaction
    fun updateTagsOf(obj: IdWithType, tags: TagList) {
        deleteTagsOf(obj)
        addTagsTo(obj, tags)
    }

    @Query("SELECT * FROM RoomTag")
    fun getAllTags(): List<Tag>

    @Query("SELECT * FROM RoomTag WHERE id = :id")
    fun getTagWithId(id: Int): Tag

    @Insert(entity = RoomTag::class)
    fun insertTag(tag: Tag): Long

    @Transaction
    fun insertTagAndGet(tag: Tag): Tag {
        val id = insertTag(tag)
        return getTagWithId(id.toInt())
    }

    @Transaction
    @Query("SELECT * FROM RoomTagElement WHERE id = :id AND type = :type")
    fun getTagsOf(id: Int, type: String): List<FRoomTag>

    @Delete(entity = RoomTag::class)
    fun deleteTags(vararg tag: Tag)
}