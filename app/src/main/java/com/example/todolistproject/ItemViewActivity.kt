package com.example.todolistproject
// Este es el manejo de la vista personaliza de cada item, no se usa para esta entrega.

import Dialogs.DialogChangeItemName
import Dialogs.dialogChangeItemNameListener
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todolistproject.classes.Item
import kotlinx.android.synthetic.main.activity_item_view.*
import kotlinx.android.synthetic.main.activity_list.*
import java.text.SimpleDateFormat
import java.util.*


class ItemViewActivity : AppCompatActivity(),dialogChangeItemNameListener {

    companion object {
        var ITEM = "ITEM"
        var POS = "POS"
    }

    var item: Item ?= null
    var edit: Boolean ?= null
    var position: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_view)

        item = intent.getParcelableExtra(ITEM)!!
        position = intent.getStringExtra(POS)!!

        textViewItemName.text = item?.name
        textViewCreatedDate.text = "Creado el " + item?.fechaDeCreacion
        textViewDate.text = item?.fechaPlazo
        edit = item?.boolPriority

        if(item!!.boolCompleted){
            buttonCompleteItem.text="Completado"
        }
        else{buttonCompleteItem.text="En progreso"}

        if(edit!!){
            imageViewStar.setImageResource(R.drawable.ic_star_yellow_24dp)
        }

        buttonBackItem.setOnClickListener(){
            onBackPressed()
        }

        imageViewCalendar.setOnClickListener(){
            EditDate()
        }

        imageViewStar.setOnClickListener(){
            EditPriority()
        }

        //buttonCompleteItem.setOnClickListener(){
        //    EditCompleted()
        //}

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
        if (edit!!) {
            imageViewStar.setImageResource(R.drawable.ic_star_border_black_24dp)
            item?.boolPriority = false
        } else {
            imageViewStar.setImageResource(R.drawable.ic_star_yellow_24dp)
            item?.boolPriority = true
        }
        edit = !edit!!
    }

    fun EditCompleted(){
        if (item?.boolCompleted == true){
            item?.boolCompleted = false
            buttonCompleteItem.text="Completar"
            Toast.makeText(applicationContext,"Ahora el item esta en progreso", Toast.LENGTH_LONG).show()
        }
        else{
            item?.boolCompleted = true
            buttonCompleteItem.text="En progreso"
            Toast.makeText(applicationContext,"Ahora el item esta completado", Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        val data = Intent().apply {
            putExtra(ITEM,item)
            putExtra(POS,position)
        }
        setResult(Activity.RESULT_OK,data)
        finish()
        super.onBackPressed()
    }

    fun changeNameButtonClicked(view: View){
        val dialogChangeName = DialogChangeItemName()
        dialogChangeName.show(supportFragmentManager, "dialogProduct")
    }
    override fun changeItemName(nameItem: String){
        item?.name = nameItem
        textViewItemName.text = item?.name
    }
}
