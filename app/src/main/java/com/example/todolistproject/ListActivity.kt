package com.example.todolistproject
// Este es el manejo de la vista personaliza de cada lista, contiene un recyclerview para los items y botones que aun
// no son necesarios y que muestran un Toast.
// Su función para esta entrega es poder mostrar los items de cada lista y que al volver no se haya perdido el orden de las listas.

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.todolistproject.ItemViewActivity.Companion.ITEM
import com.example.todolistproject.ItemViewActivity.Companion.POS
import com.example.todolistproject.adapters.OnUnCompleteItemClickListener
import com.example.todolistproject.adapters.UncompleteItemsAdapter
import com.example.todolistproject.classes.Item
import com.example.todolistproject.model.*
import com.example.todolistproject.networking.ApiService
import com.example.todolistproject.networking.ItemApi
import com.example.todolistproject.networking.ListApi
import com.example.todolistproject.utils.TOKEN
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_to_do_lists.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List


class ListActivity : AppCompatActivity(), OnUnCompleteItemClickListener {

    companion object {
        var LIST = "LIST"
    }

    private lateinit var linearLayoutManager2: LinearLayoutManager
    private lateinit var adapter2 : UncompleteItemsAdapter
    private lateinit var linearLayoutManager3: LinearLayoutManager
    private lateinit var adapter3 : UncompleteItemsAdapter
    var itemsCreatedCounter = 0 //Cantidad de Items
    var current_list: ListRoom?= null//Lista que se esta mostrando
    lateinit var itemLayout: ConstraintLayout
    var expand:Boolean = false
    var list_id:Int?= null

    var list_items_uncompleted =  ArrayList<ItemRoom>()
    var list_items_completed =  ArrayList<ItemRoom>()

    lateinit var database_list: ListRoomDao
    lateinit var database_item: ItemRoomDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        database_list = Room.databaseBuilder(this, Database::class.java,"list").allowMainThreadQueries().build().listRoomDao()
        database_item = Room.databaseBuilder(this, Database::class.java,"item").allowMainThreadQueries().build().itemRoomDao()


        itemLayout = activity_content_items

        //Lista que llega de la activity anterior ----------
        list_id = intent.getStringExtra(LIST).toInt()
        var list: ListRoom = database_list.getList(list_id!!)
        textViewListName.text = list.name
        current_list = list
        //---------------------------------------------------------

        //Se consumen los items desde la BBDD----------------------------------------
        var items_uncompleted = database_item.getItems(list_id!!,false)
        if(items_uncompleted!= null){
            var cont = 0
            items_uncompleted.forEach(){
                list_items_uncompleted.add(it)
                cont++
            }
            itemsCreatedCounter = cont
        }

        var items_completed = database_item.getItems(list_id!!,true)
        if(items_completed!= null){
            items_completed.forEach(){
                list_items_completed.add(it)
            }
        }
        //-------------------------------------------------------------------------------
        getItemsApi()
        //Recycler View UnCompletedItems----------------------------------
        linearLayoutManager2 = LinearLayoutManager(this)
        recyclerViewUncompleted.layoutManager = linearLayoutManager2
        adapter2 = UncompleteItemsAdapter(list_items_uncompleted,this)
        recyclerViewUncompleted.adapter = adapter2
        itemsCreatedCounter= list_items_uncompleted.size+1
        //----------------------------------------------------------------

        //Recycler View CompletedItems---------------------------------------------
        linearLayoutManager3 = LinearLayoutManager(this)
        recyclerViewCompleted.layoutManager = linearLayoutManager3
        adapter3 = UncompleteItemsAdapter(list_items_completed,this)
        recyclerViewCompleted.adapter = adapter3
        //--------------------------------------------------------------------------

        //Se devulve a la vista anterior
        buttonBack.setOnClickListener(){
           onBackPressed()
        }

        if(!expand){
            LinearCompleted.visibility = View.GONE
        }
        //Expande el recycler view de los completados
        buttonShowCompleted.setOnClickListener(){
            expandRecyclerView()
        }

        //Drag and drop items no completados
        val itemTouchHelperCallback = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),ItemTouchHelper.RIGHT)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val iniPosition = viewHolder.adapterPosition
                val finPosition = target.adapterPosition
                val updateList = list_items_uncompleted.get(iniPosition)
                val updateList2 = list_items_uncompleted.get(finPosition)
                updateList.position = finPosition
                updateList2.position = iniPosition
                list_items_uncompleted.set(iniPosition, updateList)
                adapter2.notifyItemChanged(iniPosition)
                list_items_uncompleted.set(finPosition, updateList2)
                adapter2.notifyItemChanged(finPosition)
                adapter2.changeListPosition(iniPosition,finPosition)
                adapter2.notifyItemMoved(iniPosition,finPosition)

                //Se actualiza la posicion de los items no completados en la BBDD
                list_items_uncompleted.forEach{
                    database_item.insertItem(it)
                    updateItemApi(it)
                }
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val posicion = viewHolder.adapterPosition
                var counter = viewHolder.adapterPosition
                while (counter+1<list_items_uncompleted.size){

                    var updateItem = list_items_uncompleted[counter+1]
                    updateItem.position=counter
                    database_item.insertItem(updateItem)
                    list_items_uncompleted.set(counter+1, updateItem)
                    adapter2.notifyItemChanged(counter+1)
                    counter++
                }
                itemsCreatedCounter-=1
                val item = adapter2.getItem(posicion)
                adapter2.deleteItem(posicion)
                adapter2.notifyItemRemoved(posicion)
                database_item.deleteItem(item)
                Log.d("ELIMINAR",item.toString())
                Log.d("DELETE",database_item.getAllItemsOrdered(list_id!!).toString())
                deleteItemApi(item)
                val snackbar = Snackbar.make(itemLayout,"Eliminaste un item",Snackbar.LENGTH_LONG)
                snackbar.show()
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerViewUncompleted)

        //Drag and drop items completados
        val itemTouchHelperCallback2 = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),ItemTouchHelper.RIGHT)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val iniPosition = viewHolder.adapterPosition
                val finPosition = target.adapterPosition
                val updateList = list_items_completed.get(iniPosition)
                val updateList2 = list_items_completed.get(finPosition)
                updateList.position = finPosition
                updateList2.position = iniPosition
                list_items_completed.set(iniPosition, updateList)
                adapter3.notifyItemChanged(iniPosition)
                list_items_completed.set(finPosition, updateList2)
                adapter3.notifyItemChanged(finPosition)
                adapter3.changeListPosition(iniPosition,finPosition)
                adapter3.notifyItemMoved(iniPosition,finPosition)

                //Se actualiza la posicion de los items no completados en la BBDD
                list_items_completed.forEach{
                    database_item.insertItem(it)
                    updateItemApi(it)
                }
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val posicion = viewHolder.adapterPosition
                var counter = viewHolder.adapterPosition
                while (counter+1<list_items_completed.size){

                    var updateItem = list_items_completed[counter+1]
                    updateItem.position=counter
                    database_item.insertItem(updateItem)
                    list_items_completed.set(counter+1, updateItem)
                    adapter3.notifyItemChanged(counter+1)
                    counter++
                }
                itemsCreatedCounter-=1

                val item = adapter3.getItem(posicion)
                adapter3.deleteItem(posicion)
                adapter3.notifyItemRemoved(posicion)
                database_item.deleteItem(item)
                deleteItemApi(item)
                val snackbar = Snackbar.make(itemLayout,"Eliminaste un item",Snackbar.LENGTH_LONG)
                snackbar.show()
            }

        }

        val itemTouchHelper2 = ItemTouchHelper(itemTouchHelperCallback2)
        itemTouchHelper2.attachToRecyclerView(recyclerViewCompleted)
    }

    //Se agrega un item a la lista
    fun onAddItemToListButtonClick(view: View){
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val due_date: String = simpleDateFormat.format(Date())
        //Se crea el nuevo item
        var newItem = ItemRoom( null,"Item  $itemsCreatedCounter",itemsCreatedCounter-1,list_id!!,false,false,due_date,"")
        //Se agrega a la BBDD y a la lista
        database_item.insertItem(newItem)
        var add_item  = database_item.getLastItem()
        //Se introduce el item en la clase para enviarlo
        var sendItem = ApiItem(listOf(add_item))
        //Se envia a la Api
        postItemApi(sendItem)
        //Se aumenta el conteo y se actualiza el recycler view
        itemsCreatedCounter++
        adapter2.notifyItemInserted(list_items_uncompleted.size )
    }


    fun onShareListButtonClick(view: View){
        Toast.makeText(view.context,"No implementado aun",Toast.LENGTH_LONG).show()
    } // Tiene que compartir lista

    //Devuelve la lista actualizada con todos los items dentro, se vuelve a la vista anterior
    override fun onBackPressed() {
        val data = Intent().apply {
            putExtra(LIST,current_list)
        }
        setResult(Activity.RESULT_OK,data)
        finish()
        super.onBackPressed()
    }
    //Funcion que expande el recycler view de los completados
    fun expandRecyclerView(){
        if(expand){
            LinearCompleted.visibility = View.GONE
            buttonShowCompleted.text = "Mostrar completados"
        }
        else{
            LinearCompleted.visibility = View.VISIBLE
            buttonShowCompleted.text = "Ocultar completados"
        }
        expand = !expand
    }

    //Se va al vista del item cuando se da click en uno de estos
    override fun onItemClicked(item: ItemRoom,position: Int) {
        val intent = Intent(this, ItemViewActivity::class.java)
        Log.d("Item",item.toString())
        intent.putExtra("ITEM",item.id.toString())
        startActivity(intent)
    }

    //Funcion que cambia el estado del item de completado a no completado
    override fun changeStateItem(item: ItemRoom, position: Int) {
        if(item.done){
            item.done = false
            adapter3.deleteItem(position)
            adapter3.notifyItemRemoved(position)
            item.position = list_items_uncompleted.size
            list_items_uncompleted.add(item)
            database_item.insertItem(item)
            updateItemApi(item)
            adapter2.notifyItemInserted(list_items_uncompleted.size)
        }
        else{
            item.done = true
            adapter2.deleteItem(position)
            adapter2.notifyItemRemoved(position)
            item.position = list_items_completed.size
            list_items_completed.add(item)
            database_item.insertItem(item)
            updateItemApi(item)
            adapter3.notifyItemInserted(list_items_completed.size)
        }
    }

    /*//Respuesta de la Item Activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    data.apply {
                        var updateItem:Item = data!!.getParcelableExtra(ITEM)
                        var position: String = data!!.getStringExtra(POS)
                        if(updateItem.boolCompleted){
                            current_list!!.list_items_completed[position.toInt()] = updateItem
                        }
                        else{
                            current_list!!.list_items_uncompleted[position.toInt()] = updateItem
                        }
                    }
                }

            }
        }
    }*/

    override fun onResume() {
        super.onResume()
        adapter2.refereshListItems(list_items_uncompleted)
        adapter3.refereshListItems(list_items_completed)
    }

    fun postItemApi(item: ApiItem){
        val request = ApiService.buildService(ItemApi::class.java)
        val call = request.postItemApi(TOKEN,item)
        call.enqueue(object : Callback<List<ItemRoom>> {
            override fun onResponse(call: Call<List<ItemRoom>>, response: Response<List<ItemRoom>>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        if(response.message() == "OK"){
                            database_item.updateIdItem(response.body()!![0].id,item.items[0].id)
                            var add_item  = database_item.getLastItem()
                            list_items_uncompleted.add(add_item)
                            adapter2.notifyItemInserted(list_items_uncompleted.size)
                            Toast.makeText(this@ListActivity, "Datos Actualizados", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Log.d("HOLAAAAAAAAA","NO recibe respuesta else")
                    Toast.makeText(this@ListActivity, "${response.errorBody()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ItemRoom>>, t: Throwable) {
                Log.d("HOLAAAAAAAAA","NO recibe respuesta onfaliure")
                //userToDoList.add(list)
                //recyclerViewLists.adapter?.notifyItemInserted(userToDoList.size)
                Toast.makeText(this@ListActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    fun updateItemApi(item: ItemRoom){
        val request = ApiService.buildService(ItemApi::class.java)
        val call = request.updateItemApi(TOKEN,item.id,item)
        call.enqueue(object : Callback<ItemRoom> {
            override fun onResponse(call: Call<ItemRoom>, response: Response<ItemRoom>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        if(response.message() == "OK"){
                            Log.d("RESPONSE ITEM UPD", response.body().toString())
                            Toast.makeText(this@ListActivity, "Datos Actualizados", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Log.d("HOLAAAAAAAAA","NO recibe respuesta else")
                    Toast.makeText(this@ListActivity, "${response.errorBody()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ItemRoom>, t: Throwable) {
                Log.d("HOLAAAAAAAAA","NO recibe respuesta onfaliure")
                //userToDoList.add(list)
                //recyclerViewLists.adapter?.notifyItemInserted(userToDoList.size)
                Toast.makeText(this@ListActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    fun deleteItemApi(item: ItemRoom){
        val request = ApiService.buildService(ItemApi::class.java)
        val call = request.deleteItemApi(TOKEN,item.id)
        call.enqueue(object : Callback<ItemRoom> {
            override fun onResponse(call: Call<ItemRoom>, response: Response<ItemRoom>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        if(response.message() == "No Content"){

                        }
                    }
                }
                else{
                    Log.d("HOLAAAAAAAAA","NO recibe respuesta else")
                    Toast.makeText(this@ListActivity, "${response.errorBody()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ItemRoom>, t: Throwable) {
                Toast.makeText(this@ListActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    //Se obtienen todas las listas de la api y si se esta ofline se ingresan las listas que estan en la bbdd
    fun getItemsApi(){
        val request = ApiService.buildService(ItemApi::class.java)
        val call = request.getItemsApi(TOKEN,list_id)
        call.enqueue(object : Callback<List<ItemRoom>> {
            override fun onResponse(call: Call<List<ItemRoom>>, response: Response<List<ItemRoom>>) {
                Log.d("RESPONSE ITEM API",response.body().toString())
                if (response.isSuccessful) {
                    if (response.body() != null) {

                    }
                }
                else{
                    Log.d("HOLAAAAAAAAA","NO recibe respuesta else")
                    Toast.makeText(this@ListActivity, "${response.errorBody()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ItemRoom>>, t: Throwable) {
                //En el caso de que no hay conexion a internet, se utilizan las listas que ya están en la BBDD
                //Si hay listas en la BBDD, se agregan a userToDoList

                Toast.makeText(this@ListActivity, "No hay conexion a Internet", Toast.LENGTH_SHORT).show()
            }
        })

    }

}
