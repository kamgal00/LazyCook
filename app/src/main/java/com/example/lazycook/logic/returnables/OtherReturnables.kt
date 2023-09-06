package com.example.lazycook.logic.returnables

import android.net.Uri
import com.example.lazycook.logic.ReturnValue

data class PhotoGallery(val uri: Uri?): ReturnValue
object PhotoTake: ReturnValue