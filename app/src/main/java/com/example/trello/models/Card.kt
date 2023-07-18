package com.example.trello.models

import android.os.Parcel
import android.os.Parcelable
import java.text.ParseException

data class Card(
    val name:String = "",
    val createdBy:String="",
    val assignedTo:ArrayList<String> = ArrayList()
):Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.createStringArrayList()!!
    )
    
    override fun writeToParcel(source: Parcel, flags: Int) {
        source.writeString(name)
        source.writeString(createdBy)
        source.writeStringList(assignedTo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR:Parcelable.Creator<Card> = object : Parcelable.Creator<Card>{

            override fun createFromParcel(source: Parcel?): Card {
                return Card(source!!)
            }
            override fun newArray(size: Int): Array<Card?> {
                return arrayOfNulls(size)
        }



        }
    }
}
