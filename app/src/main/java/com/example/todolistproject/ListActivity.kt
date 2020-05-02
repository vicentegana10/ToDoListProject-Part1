package com.example.todolistproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistproject.adapters.CompleteItemsAdapter
import com.example.todolistproject.adapters.UncompleteItemsAdapter
import com.example.todolistproject.classes.Item
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {

    companion object {
        var LISTNAME = "LISTNAME"
    }

    private lateinit var linearLayoutManager2: LinearLayoutManager
    private lateinit var adapter2 : UncompleteItemsAdapter
    private lateinit var linearLayoutManager3: LinearLayoutManager
    private lateinit var adapter3 : CompleteItemsAdapter

    var listItems = ArrayList<Item>()
    var itemsCreatedCounter = 1
    var listItemsCompleted = mutableListOf<Item>(Item("","Items completado ejemplo","No"))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        var list: List = intent.getParcelableExtra(LISTNAME)!!
        textViewListName.text = list.name
        list.list_items = listItems
        Log.d("lista",list.toString())

        linearLayoutManager2 = LinearLayoutManager(this)
        recyclerViewUncompleted.layoutManager = linearLayoutManager2
        adapter2 = UncompleteItemsAdapter(listItems as ArrayList<Item>)
        recyclerViewUncompleted.adapter = adapter2

        linearLayoutManager3 = LinearLayoutManager(this)
        recyclerViewCompleted.layoutManager = linearLayoutManager3
        adapter3 = CompleteItemsAdapter(listItemsCompleted as ArrayList<Item>)
        recyclerViewCompleted.adapter = adapter3

        buttonBack.setOnClickListener(){
            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause();
        // aca va lo que pasa cuando esta en pausa
        print("en Pausa")
    }

    override fun onResume() {
        super.onResume()
        // aca va lo que pasa cuando esta en resume
        print("en Resume")
    }

    override fun onStop() {
        super.onStop()
        // aca va lo que pasa cuando esta en stop  ACA SE GUARDAN VALORES CLASE 6
        print("en Stop")
    }

    fun onAddItemToListButtonClick(view: View){
        var newItem = Item("", "Item  $itemsCreatedCounter","No")
        itemsCreatedCounter++
        listItems.add(newItem)
        adapter2.notifyItemInserted(listItems.size )
    }

    fun onCheckBoxItemClick(view: View){
        Toast.makeText(view.context,"No implementado aun", Toast.LENGTH_LONG).show()
        val checkBox : CheckBox = findViewById(view.id)
        checkBox.isChecked = false
    } // Tiene que completar el item o descompletar

    fun onShowCompleteButtonClick(view: View) { // Activa/desactiva visibilidad del 2do RecyclerView
        if (recyclerViewCompleted.visibility==View.VISIBLE){recyclerViewCompleted.visibility=View.INVISIBLE}
        else{ recyclerViewCompleted.visibility=View.VISIBLE}
    }

    fun onShareListButtonClick(view: View){
        Toast.makeText(view.context,"No implementado aun",Toast.LENGTH_LONG).show()
    } // Tiene que compartir lista

    fun onBackToListsButtonClick(view: View){ //falta pasarle info a ToDoListsActivity
        val myIntent = Intent(baseContext, ToDoListsActivity::class.java)
        startActivity(myIntent)
    }
}
