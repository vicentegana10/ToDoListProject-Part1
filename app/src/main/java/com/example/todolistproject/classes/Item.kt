package com.example.todolistproject.classes

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Item(val boolCheckBox : String, val name :String, val boolPriority : String ):Parcelable
