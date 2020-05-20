package com.example.todolistproject.classes
// Clase Item usada para instanciar items para cada lista, por ahora solo se agregan a la lista en un array de items
import android.os.Parcelable
import android.text.format.DateFormat
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.time.DateTimeException

@Parcelize
class Item(val boolCompleted : String, val name :String, var boolPriority :String, val fechaDeCreacion :String, val fechaPlazo: String, val nota :String):Parcelable

