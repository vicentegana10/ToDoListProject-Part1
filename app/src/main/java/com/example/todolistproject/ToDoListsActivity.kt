package com.example.todolistproject
// Manejo de la vista mas importante de la entrega, se crea la clase Lista y esta view tiene muchos usos.
// Se pueden agregar listas gracias a un dialog apretando un boton flotante abajo a la derecha.
// Estas listas se ven en un recyclerview y tienen un boton para editarles el nombre tambien mediante Dialog.
// Se pueden eliminar listas gracias a onSwiped() y mover gracias a onMove(), funciones que avisan al adaptador del cambio
// inmediatamente.   Tambien se puede ingresar a la view de cada lista apretando sobre una en especifico y al volver no se habrá
// olvidado el orden.    Por ultimo para cerrar sesion hay un boton arriba al lado del nombre de usuario para volver al Login.

import Dialogs.DialogList
import Dialogs.DialogList2
import Dialogs.dialogList2Listener
import Dialogs.dialogListListener
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistproject.AppMenuActivity.Companion.USER
import com.example.todolistproject.ListActivity.Companion.LIST
import com.example.todolistproject.adapters.ListsAdapter
import com.example.todolistproject.adapters.OnButtonClickListener
import com.example.todolistproject.adapters.OnItemClickListener
import com.example.todolistproject.classes.Item
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_to_do_lists.*


class ToDoListsActivity : AppCompatActivity(), OnItemClickListener,dialogListListener,OnButtonClickListener, dialogList2Listener {

    companion object {
        var USER = "USER"
    }

    var userLog: User? = null//Usuario
    var userToDoList = ArrayList<List>()//Lista con las ToDoList del usuario
    var listsCreatedCounter = 0//Contador de la cantidad de listas

    lateinit var listLayout:ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_lists)
        restoreContent(savedInstanceState)
        listLayout = activity_content_list
        var user:User = intent.getParcelableExtra(USER)
        userLog = user
        textViewUsername.text = user.email

        recyclerViewLists.adapter = ListsAdapter(userToDoList,this,this)
        recyclerViewLists.layoutManager = LinearLayoutManager(this)

        //Se agrega un lista al apretar el boton
        ButtonAddList.setOnClickListener(){
            onAddListButtonClick()
        }

        textViewUsername.setOnClickListener(){
            onUsernameClicked(user)
        }

        //Se implemetó el drag and drop, cambia la listas de lugar, y se eleminan hacia el lado
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
                // Para intercambiar los index de ambos items que se mueve (ya que esta funcion va 1x1)
                val updateList = userToDoList.get(iniPosition)
                val updateList2 = userToDoList.get(finPosition)
                updateList.position = finPosition
                updateList2.position = iniPosition
                userToDoList.set(iniPosition, updateList)
                recyclerViewLists.adapter?.notifyItemChanged(iniPosition)
                userToDoList.set(finPosition, updateList2)
                recyclerViewLists.adapter?.notifyItemChanged(finPosition)
                // Solo para cuando se mueven

                ListsAdapter(userToDoList,this@ToDoListsActivity,this@ToDoListsActivity).changeListPosition(iniPosition,finPosition)
                recyclerViewLists.adapter?.notifyItemMoved(iniPosition,finPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                // Para intercambiar los index del item al moverse
                var counter = viewHolder.adapterPosition
                while (counter+1<userToDoList.size){

                    var updateList = userToDoList[counter+1]
                    updateList.position=counter
                    userToDoList.set(counter+1, updateList)
                    recyclerViewLists.adapter?.notifyItemChanged(counter+1)
                    counter++
                }
                listsCreatedCounter-=1
                // index de todos hacia abajo cambiados

                val list = ListsAdapter(userToDoList,this@ToDoListsActivity,this@ToDoListsActivity).getList(position)
                ListsAdapter(userToDoList,this@ToDoListsActivity,this@ToDoListsActivity).deleteList(viewHolder.adapterPosition)
                recyclerViewLists.adapter?.notifyItemRemoved(position)
                val snackbar = Snackbar.make(listLayout,"Eliminaste una Lista",Snackbar.LENGTH_LONG)
                snackbar.setAction("Deshacer") {
                    ListsAdapter(userToDoList,this@ToDoListsActivity,this@ToDoListsActivity).restoreList(position,list)
                    recyclerViewLists.adapter?.notifyItemInserted(position)
                    // revertir lo hecho en modificar el atributo position de la lista
                    var counter2 = viewHolder.adapterPosition
                    while (counter2+1<userToDoList.size){

                        var updateList2 = userToDoList[counter2+1]
                        updateList2.position=counter2+1
                        userToDoList.set(counter2+1, updateList2)
                        recyclerViewLists.adapter?.notifyItemChanged(counter2+1)
                        counter2++
                    }
                    listsCreatedCounter+=1


                }
                snackbar.setActionTextColor(Color.BLUE)
                snackbar.show()
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerViewLists)

    }

    //Se abre el diagog para añadir la lista
    fun onAddListButtonClick(){
        val dialogList = DialogList()
        dialogList.show(supportFragmentManager, "dialogProduct")
    }

    //Se abre el detalle de la lista
    override fun onItemClicked(list: List) {
        val intent2 = Intent(this, ListActivity::class.java)
        intent2.putExtra(LIST,userToDoList[list.position]) // se pasa el primer nombre no el del item apretado :/
        startActivityForResult(intent2,1)
    }

    //Se abre el menu aplicacion
    fun onUsernameClicked(user: User){
        val intent3 = Intent(this,AppMenuActivity::class.java)
        intent3.putExtra(USER,user)
        startActivityForResult(intent3,1)
    }

    //Abre el dialog para cambiar el nombre de la lista
    override fun onButtonClicked(list: List) {
        val dialogList = DialogList2()
        val indexAsParameter = Bundle()
        indexAsParameter.putInt("KEY1",list.position)
        dialogList.arguments = indexAsParameter
        dialogList.show(supportFragmentManager, "dialogProduct")
    }

    //Se añade un lista a userToDoList
    override fun addList(nameList: String){
        var list_items_uncompleted = ArrayList<Item>()
        var list_items_completed = ArrayList<Item>()
        userToDoList.add(List(nameList,listsCreatedCounter,list_items_uncompleted,list_items_completed))
        listsCreatedCounter++
        recyclerViewLists.adapter?.notifyItemInserted(userToDoList.size)

    }
    //Cambia el nombre de la lista
    override fun changeName(nameList: String,indexRec : Int){
        Toast.makeText(applicationContext,"Se cambió el nombre a:  "+nameList,Toast.LENGTH_LONG).show()
        val updateList = userToDoList.get(indexRec)
        updateList.name=nameList
        userToDoList.set(updateList.position,updateList)
        recyclerViewLists.adapter?.notifyItemChanged(updateList.position)
    }
    //Funcion logOut que genera un intent hacia login
    fun LogOut(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    //Restaura el contenido al girar la pantalla
    private fun restoreContent(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            userLog = savedInstanceState.getParcelable("user")
            userToDoList = savedInstanceState.getParcelableArrayList<List>("UserList")!!
        }
    }

    //Guarda las variables para cuando se gire la pantalla
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("user",userLog)
        outState.putParcelableArrayList("UserList",userToDoList)
    }

    //Recibe una lista actualizada con todos los items añadidos en el detalle de cada lista
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    data.apply {
                        var updateList:List = data!!.getParcelableExtra(LIST)
                        userToDoList[updateList.position] = updateList
                    }
                }

            }
        }
    }

}

@Parcelize
data class List(var name: String, var position: Int, var list_items_uncompleted: ArrayList<Item>, var list_items_completed: ArrayList<Item>):Parcelable
