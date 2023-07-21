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

    const val DOCUMENT_ID:String = "documentId"
    const val TASK_LIST:String = "taskList"
    const val BOARD_DETAIL:String = "board_detail"
    const val ID:String = "id"
    const val EMAIL:String = "email"
    const val TASK_LIST_ITEM_POSITION:String = "task_list_item_postion"
    const val CARD_LIST_ITEM_POSITION:String = "card_list_item_postion"


    fun getFileExtension(activity: Activity, uri: Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}