package com.example.todolistproject
// Este es el manejo de la vista personaliza de cada item, no se usa para esta entrega.

import Dialogs.DialogChangeItemName
import Dialogs.dialogChangeItemNameListener
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.todolistproject.classes.Item
import com.example.todolistproject.model.Database
import com.example.todolistproject.model.ItemRoom
import com.example.todolistproject.model.ItemRoomDao
import com.example.todolistproject.networking.ApiService
import com.example.todolistproject.networking.ItemApi
import com.example.todolistproject.utils.TOKEN
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_app_menu.*
import kotlinx.android.synthetic.main.activity_item_view.*
import kotlinx.android.synthetic.main.activity_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class ItemViewActivity : AppCompatActivity(),dialogChangeItemNameListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    companion object {
        var ITEM = "ITEM"
        var POS = "POS"
    }

    var item: ItemRoom?= null
    var edit: Boolean ?= null //Bool qu indica la prioridad
    lateinit var database_item: ItemRoomDao
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_view)

        database_item = Room.databaseBuilder(this, Database::class.java,"item").allowMainThreadQueries().build().itemRoomDao()

        //Recibe el item actual
        var item_id = intent.getStringExtra(ITEM).toInt()
        item = database_item.getItem(item_id)
        //Se inicializan los text view con su valor correspondiente
        topAppBarItem.title =  item?.name
        textViewCreatedDate.text = "Creado el " + item?.due_date
        textViewDate.text = item?.due_date
        edit = item?.starred

        //Si el item no tiene notas
        if (item!!.notes != ""){
            textView5.setText(item!!.notes)
        }

        //Se muestra en un text view si el item está completado o no
        if(item!!.done){
            buttonCompleteItem.text="Completado"
        }
        else{buttonCompleteItem.text="En progreso"}

        if(edit!!){
            imageViewStar.setImageResource(R.drawable.ic_star_yellow_24dp)
        }

        topAppBarItem.setNavigationOnClickListener{
            onBackPressed()
        }
        topAppBarItem.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logo -> {
                    // Handle favorite icon press
                    //Toast.makeText(applicationContext,"Nombre_Empresa. Since 2020", Toast.LENGTH_LONG).show()
                    changeNameButtonClicked()
                    true
                }
                else -> false
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        //Se edita la fecha de plazo al clickear el calendario
        imageViewCalendar.setOnClickListener(){
            EditDate()
        }
        //Se edita la prioridad al clickear la estrella
        imageViewStar.setOnClickListener(){
            EditPriority()
        }
        //Guardan los cambios de la nota al clickear el boton "guardar cambios"
        buttonSaveNote.setOnClickListener(){
            SaveNote()
        }

    }

    //Se edita la fecha de plazo
    fun EditDate(){

        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val format_date = "dd-MM-yyyy"
            val sdf = SimpleDateFormat(format_date, Locale.US)
            textViewDate.text = sdf.format(cal.time)
            item!!.due_date = sdf.format(cal.time)
            AsyncTask.execute{
                database_item.insertItem(item!!)
            }
            updateItemApi(item!!)
        }

        DatePickerDialog(this, dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    //Edita la prioridad del item
    fun EditPriority() {
        if (edit!!) {
            imageViewStar.setImageResource(R.drawable.ic_star_border_black_24dp)
            item?.starred = false
            AsyncTask.execute{
                database_item.insertItem(item!!)
            }
            updateItemApi(item!!)
        } else {
            imageViewStar.setImageResource(R.drawable.ic_star_yellow_24dp)
            item?.starred = true
            AsyncTask.execute{
                database_item.insertItem(item!!)
            }
            updateItemApi(item!!)
        }
        edit = !edit!!
    }

    fun EditCompleted(){
        if (item?.done == true){
            item?.done = false
            buttonCompleteItem.text="Completar"
            Toast.makeText(applicationContext,"Ahora el item esta en progreso", Toast.LENGTH_LONG).show()
        }
        else{
            item?.done = true
            buttonCompleteItem.text="En progreso"
            Toast.makeText(applicationContext,"Ahora el item esta completado", Toast.LENGTH_LONG).show()
        }
    }
    //Se guarda la nota editada
    fun SaveNote(){
        item?.notes  = textView5.text.toString()
        AsyncTask.execute{
            database_item.insertItem(item!!)
        }
        updateItemApi(item!!)
    }

    override fun onBackPressed() {
        val data = Intent().apply {
            putExtra(ITEM,item)
        }
        setResult(Activity.RESULT_OK,data)
        finish()
        super.onBackPressed()
    }
    //Se abre el dialog cuando se hace click en el boton
    fun changeNameButtonClicked(){
        val dialogChangeName = DialogChangeItemName()
        dialogChangeName.show(supportFragmentManager, "dialogProduct")
    }
    //Se edita el nombre
    override fun changeItemName(nameItem: String){
        item?.name = nameItem
        AsyncTask.execute{
            database_item.insertItem(item!!)
        }
        updateItemApi(item!!)
        topAppBarItem.title =  item?.name
    }
    override fun onPause(){
        super.onPause()
    }

    //Funcion que hace update a la Api
    fun updateItemApi(item: ItemRoom){
        val request = ApiService.buildService(ItemApi::class.java)
        val call = request.updateItemApi(TOKEN,item.id,item)
        call.enqueue(object : Callback<ItemRoom> {
            override fun onResponse(call: Call<ItemRoom>, response: Response<ItemRoom>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        if(response.message() == "OK"){
                            Toast.makeText(this@ItemViewActivity, "Datos Actualizados", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this@ItemViewActivity, "${response.errorBody()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ItemRoom>, t: Throwable) {
                Toast.makeText(this@ItemViewActivity, "No hay conexion a Internet", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val itemMarker =  LatLng(item!!.lat,item!!.longi)
        var mark = mMap.addMarker(MarkerOptions().position(itemMarker).title(item!!.name))
        mark.tag = item!!.id
        mMap.moveCamera(CameraUpdateFactory.newLatLng(itemMarker))
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        TODO("Not yet implemented")
    }

}
