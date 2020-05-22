package com.example.todolistproject
// Este es el manejo de la vista personaliza de cada item, no se usa para esta entrega.

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.todolistproject.classes.Item
import kotlinx.android.synthetic.main.activity_item_view.*
import java.text.SimpleDateFormat
import java.util.*


class ItemViewActivity : AppCompatActivity() {

    companion object {
        var ITEM = "ITEM"
    }

    var item: Item ?= null
    var edit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_view)

        item = intent.getParcelableExtra(ITEM)!!

        textViewItemName.text = item?.name
        textViewCreatedDate.text = "Creado el " + item?.fechaDeCreacion
        textViewDate.text = item?.fechaPlazo

        imageViewCalendar.setOnClickListener(){
            EditDate()
        }

        imageViewStar.setOnClickListener(){
            EditPriority()
        }

        buttonBackItem.setOnClickListener(){
            super.onBackPressed()
        }

    }

    fun EditDate(){

        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "dd-MM-yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            textViewDate.text = sdf.format(cal.time)
            item?.fechaPlazo = sdf.format(cal.time)
        }

        DatePickerDialog(this, dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    fun EditPriority() {
        if (edit) {
            imageViewStar.setImageResource(R.drawable.ic_star_border_black_24dp)
            item?.boolPriority = false
        } else {
            imageViewStar.setImageResource(R.drawable.ic_star_yellow_24dp)
            item?.boolPriority = true
        }
        edit = !edit
    }
}
