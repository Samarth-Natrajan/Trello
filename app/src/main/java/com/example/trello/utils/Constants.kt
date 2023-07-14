package com.example.trello.utils

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap

object Constants {
    const val USERS:String = "users"
    const val BOARDS:String = "boards"
    const val IMAGE:String = "image"
    const val NAME:String = "name"
    const val MOBILE:String = "mobile"
    const val ASSIGNED_TO:String = "assignedTo"

    fun getFileExtension(activity: Activity, uri: Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}