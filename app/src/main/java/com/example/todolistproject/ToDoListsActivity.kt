package com.example.todolistproject

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistproject.ListActivity.Companion.LISTNAME
import com.example.todolistproject.adapters.ListsAdapter
import com.example.todolistproject.adapters.OnItemClickListener
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_to_do_lists.*
import kotlinx.android.synthetic.main.recyclerview_list_row.*


class ToDoListsActivity : AppCompatActivity(), OnItemClickListener {

    companion object {
        var USER = "USER"
    }

    var userLog: User? = null
    var userToDoList = ArrayList<List>()//Lista con las ToDoList del usuario
    var listsCreatedCounter = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_lists)
        var user:User = intent.getParcelableExtra(USER)
        userLog = user
        textViewUsername.text = user.email
        recyclerViewLists.adapter = ListsAdapter(userToDoList,this)
        recyclerViewLists.layoutManager = LinearLayoutManager(this)
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

    fun onAddListButtonClick(view: View){
        var newList = List("Lista  $listsCreatedCounter",listsCreatedCounter)
        listsCreatedCounter++
        userToDoList.add(newList)
        recyclerViewLists.adapter?.notifyItemInserted(userToDoList.size)
    }

    fun onEraseListButtonClick(view: View){
        //val removeIndex = LA POSICION DE ESTA LISTA EN LA LISTA DE userToDoList
        //userToDoList.remove(removeIndex)
        //adapter.notifyItemRemoved(removeIndex)
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
        intent2.putExtra(LISTNAME,textViewList.text) // se pasa el primer nombre no el del item apretado :/
        startActivity(intent2)
    }
}

@Parcelize
data class List(val name: String,val position: Int):Parcelable
