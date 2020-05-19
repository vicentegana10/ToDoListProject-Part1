package com.example.todolistproject
// Este es el manejo de la vista personaliza de cada lista, contiene un recyclerview para los items y botones que aun
// no son necesarios y que muestran un Toast.
// Su función para esta entrega es poder mostrar los items de cada lista y que al volver no se haya perdido el orden de las listas.

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistproject.ItemViewActivity.Companion.ITEM
import com.example.todolistproject.adapters.CompleteItemsAdapter
import com.example.todolistproject.adapters.OnUnCompleteItemClickListener
import com.example.todolistproject.adapters.UncompleteItemsAdapter
import com.example.todolistproject.classes.Item
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
    private lateinit var adapter3 : CompleteItemsAdapter
    var itemsCreatedCounter = 1 //Cantidad de Items
    var current_list: List?= null//Lista que se esta mostrando
    lateinit var itemLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        itemLayout = activity_content_items

        var list: List = intent.getParcelableExtra(LIST)!!
        textViewListName.text = list.name
        current_list = list
        linearLayoutManager2 = LinearLayoutManager(this)
        recyclerViewUncompleted.layoutManager = linearLayoutManager2
        adapter2 = UncompleteItemsAdapter(current_list!!.list_items,this)
        recyclerViewUncompleted.adapter = adapter2

        /*
        linearLayoutManager3 = LinearLayoutManager(this)
        recyclerViewCompleted.layoutManager = linearLayoutManager3
        adapter3 = CompleteItemsAdapter(listItemsCompleted as ArrayList<Item>)
        recyclerViewCompleted.adapter = adapter3

         */
        //Se devulve a la vista anterior
        buttonBack.setOnClickListener(){
           onBackPressed()
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
                adapter2.changeListPosition(iniPosition,finPosition)
                adapter2.notifyItemMoved(iniPosition,finPosition)
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
    }

    //Se agrega un item a la lista
    fun onAddItemToListButtonClick(view: View){
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val fechaDeCreacion: String = simpleDateFormat.format(Date())
        var newItem = Item("", "Item  $itemsCreatedCounter","No",fechaDeCreacion,fechaDeCreacion)
        itemsCreatedCounter++
        current_list?.list_items!!.add(newItem)
        adapter2.notifyItemInserted(current_list!!.list_items.size )
    }

    fun onCheckBoxItemClick(view: View){
        Toast.makeText(view.context,"No implementado aun", Toast.LENGTH_LONG).show()
        val checkBox : CheckBox = findViewById(view.id)
        checkBox.isChecked = false
    } // Tiene que completar el item o descompletar

    fun onShowCompleteButtonClick(view: View) { // Activa/desactiva visibilidad del 2do RecyclerView
        //if (recyclerViewCompleted.visibility==View.VISIBLE){recyclerViewCompleted.visibility=View.INVISIBLE}
        //else{ recyclerViewCompleted.visibility=View.VISIBLE}
        Toast.makeText(view.context,"No implementado aun",Toast.LENGTH_LONG).show()

    }

    fun onShareListButtonClick(view: View){
        Toast.makeText(view.context,"No implementado aun",Toast.LENGTH_LONG).show()
    } // Tiene que compartir lista

    //Devuelve la lista actualizada con todos los items dentro, se vuelve a la vista anterio
    override fun onBackPressed() {
        val data = Intent().apply {
            putExtra(LIST,current_list)
        }
        setResult(Activity.RESULT_OK,data)
        finish()
        super.onBackPressed()
    }

    override fun onItemClicked(item: Item) {
        val intent = Intent(this, ItemViewActivity::class.java)
        intent.putExtra(ITEM,item)
        startActivityForResult(intent,1)
    }


}
