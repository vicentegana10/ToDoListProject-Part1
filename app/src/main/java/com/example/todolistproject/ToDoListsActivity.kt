package com.example.todolistproject

import Dialogs.DialogList
import Dialogs.dialogListListener
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistproject.ListActivity.Companion.LISTNAME
import com.example.todolistproject.adapters.ListsAdapter
import com.example.todolistproject.adapters.OnItemClickListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_to_do_lists.*
import kotlinx.android.synthetic.main.recyclerview_list_row.*
import org.w3c.dom.NameList
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class ToDoListsActivity : AppCompatActivity(), OnItemClickListener,dialogListListener {

    companion object {
        var USER = "USER"
    }

    var userLog: User? = null
    var userToDoList = ArrayList<List>()//Lista con las ToDoList del usuario
    var listsCreatedCounter = 1

    lateinit var listLayout:ConstraintLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_lists)
        listLayout = activity_content_list
        var user:User = intent.getParcelableExtra(USER)
        userLog = user
        textViewUsername.text = user.email

        recyclerViewLists.adapter = ListsAdapter(userToDoList,this)
        recyclerViewLists.layoutManager = LinearLayoutManager(this)

        ButtonAddList.setOnClickListener(){
            onAddListButtonClick()
        }

        val itemTouchHelperCallBack = object : ItemTouchHelper.Callback()  {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),ItemTouchHelper.RIGHT)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
            ): Boolean {
                val iniPosition = viewHolder.adapterPosition
                val finPosition = target.adapterPosition
                ListsAdapter(userToDoList,this@ToDoListsActivity).changeListPosition(iniPosition,finPosition)
                recyclerViewLists.adapter?.notifyItemMoved(iniPosition,finPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val list = ListsAdapter(userToDoList,this@ToDoListsActivity).getList(position)
                ListsAdapter(userToDoList,this@ToDoListsActivity).deleteList(viewHolder.adapterPosition)
                recyclerViewLists.adapter?.notifyItemRemoved(position)
                val snackbar = Snackbar.make(listLayout,"Eliminaste una Lista",Snackbar.LENGTH_LONG)
                snackbar.setAction("Deshacer",{
                    ListsAdapter(userToDoList,this@ToDoListsActivity).restoreList(position,list)
                    recyclerViewLists.adapter?.notifyItemInserted(position)
                })
                snackbar.setActionTextColor(Color.BLUE)
                snackbar.show()
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerViewLists)

    }


    fun onAddListButtonClick(){
        /*var newList = List("Lista  $listsCreatedCounter",listsCreatedCounter)
        listsCreatedCounter++
        userToDoList.add(newList)*/
        val dialogList = DialogList()
        dialogList.show(supportFragmentManager, "dialogProduct")
        Log.d("holaaa",userToDoList.toString())
    }


    fun onListButtonClick(view: View) { //falta pasarle info a ListActivity
        val intent2 = Intent(view.context, ListActivity::class.java)
        intent2.putExtra(LISTNAME,textViewList.text.toString()) // se pasa el primer nombre no el del item apretado :/
        view.context.startActivity(intent2)
    }

    fun onLogOutButtonClick(view: View) { //Por ahora esta bien asi
        val myIntent = Intent(baseContext, MainActivity::class.java)
        startActivity(myIntent)
    }

    override fun onItemClicked(list: List) {
        val intent2 = Intent(this, ListActivity::class.java)
        intent2.putExtra(LISTNAME,list) // se pasa el primer nombre no el del item apretado :/
        startActivity(intent2)
    }

    override fun addList(nameList: String){
        userToDoList.add(List(nameList,listsCreatedCounter))
        listsCreatedCounter++
        recyclerViewLists.adapter?.notifyItemInserted(userToDoList.size)

    }
}

@Parcelize
data class List(val name: String,val position: Int):Parcelable
