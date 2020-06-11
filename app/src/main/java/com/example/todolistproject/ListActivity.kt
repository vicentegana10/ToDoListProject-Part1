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
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_list.*
import java.text.SimpleDateFormat
import java.util.*


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
        //--------------------------------------------------
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
                }
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val posicion = viewHolder.adapterPosition
                val item = adapter2.getItem(posicion)
                adapter2.deleteItem(posicion)
                adapter2.notifyItemRemoved(posicion)
                val snackbar = Snackbar.make(itemLayout,"Eliminaste un item",Snackbar.LENGTH_LONG)
                snackbar.setAction("Deshacer",{
                    adapter2.restoreItem(posicion,item)
                    adapter2.notifyItemInserted(posicion)
                })
                snackbar.setActionTextColor(Color.BLUE)
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
                }

                Log.d("Nuevas POS",database_item.getAllItemsOrdered(list_id!!).toString())
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val posicion = viewHolder.adapterPosition
                val item = adapter3.getItem(posicion)
                adapter3.deleteItem(posicion)
                adapter3.notifyItemRemoved(posicion)
                val snackbar = Snackbar.make(itemLayout,"Eliminaste un item",Snackbar.LENGTH_LONG)
                snackbar.setAction("Deshacer",{
                    adapter3.restoreItem(posicion,item)
                    adapter3.notifyItemInserted(posicion)
                })
                snackbar.setActionTextColor(Color.BLUE)
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
        var newItem = ItemRoom( null,"Item  $itemsCreatedCounter",itemsCreatedCounter-1,list_id!!,false,false,due_date,"")
        database_item.insertItem(newItem)
        var add_item  = database_item.getLastItem()
        list_items_uncompleted.add(add_item)
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
            adapter2.notifyItemInserted(list_items_uncompleted.size)
        }
        else{
            item.done = true
            adapter2.deleteItem(position)
            adapter2.notifyItemRemoved(position)
            item.position = list_items_completed.size
            list_items_completed.add(item)
            database_item.insertItem(item)
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
        adapter2.notifyDataSetChanged()
        adapter3.notifyDataSetChanged()
    }

}
