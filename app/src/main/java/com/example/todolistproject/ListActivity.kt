package com.example.todolistproject

import android.app.Activity
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
        var LIST = "LIST"
    }

    private lateinit var linearLayoutManager2: LinearLayoutManager
    private lateinit var adapter2 : UncompleteItemsAdapter
    private lateinit var linearLayoutManager3: LinearLayoutManager
    private lateinit var adapter3 : CompleteItemsAdapter
    var itemsCreatedCounter = 1
    var listItemsCompleted = mutableListOf<Item>(Item("","Items completado ejemplo","No"))
    var current_list: List?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        var list: List = intent.getParcelableExtra(LIST)!!
        textViewListName.text = list.name
        current_list = list
        linearLayoutManager2 = LinearLayoutManager(this)
        recyclerViewUncompleted.layoutManager = linearLayoutManager2
        adapter2 = UncompleteItemsAdapter(current_list!!.list_items as ArrayList<Item>)
        recyclerViewUncompleted.adapter = adapter2

        linearLayoutManager3 = LinearLayoutManager(this)
        recyclerViewCompleted.layoutManager = linearLayoutManager3
        adapter3 = CompleteItemsAdapter(listItemsCompleted as ArrayList<Item>)
        recyclerViewCompleted.adapter = adapter3

        buttonBack.setOnClickListener(){
            val data = Intent().apply {
                putExtra(LIST,current_list)
            }
            setResult(Activity.RESULT_OK,data)
            finish()
            super.onBackPressed()
        }
    }


    fun onAddItemToListButtonClick(view: View){
        var newItem = Item("", "Item  $itemsCreatedCounter","No")
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
