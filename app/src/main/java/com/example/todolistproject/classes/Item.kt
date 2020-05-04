package com.example.todolistproject.classes
// Clase Item usada para instanciar items para cada lista, por ahora solo se agregan a la lista en un array de items
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Item(val boolCheckBox : String, val name :String, val boolPriority : String ):Parcelable
