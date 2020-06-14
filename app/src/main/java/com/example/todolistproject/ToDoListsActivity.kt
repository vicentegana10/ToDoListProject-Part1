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
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.todolistproject.AppMenuActivity.Companion.USER
import com.example.todolistproject.ListActivity.Companion.LIST
import com.example.todolistproject.adapters.ListsAdapter
import com.example.todolistproject.adapters.OnButtonClickListener
import com.example.todolistproject.adapters.OnItemClickListener
import com.example.todolistproject.classes.Item
import com.example.todolistproject.model.*
import com.example.todolistproject.networking.ApiService
import com.example.todolistproject.networking.ListApi
import com.example.todolistproject.networking.UserApi
import com.example.todolistproject.utils.TOKEN
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_to_do_lists.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ToDoListsActivity : AppCompatActivity(), OnItemClickListener,dialogListListener,OnButtonClickListener, dialogList2Listener {

    companion object {
        var USER = "USER"
    }

    var userLog: UserRoom? = null//Usuario
    var userToDoList = ArrayList<ListRoom>()//Lista con las ToDoList del usuario
    var listsCreatedCounter = 0//Contador de la cantidad de listas
    var check_api:Boolean ?=null

    lateinit var listLayout:ConstraintLayout

    lateinit var database: UserRoomDao
    lateinit var database_list: ListRoomDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_lists)
        restoreContent(savedInstanceState)
        listLayout = activity_content_list
        var user:UserRoom = intent.getParcelableExtra(USER)

        topAppBar.title = user?.first_name

        recyclerViewLists.adapter = ListsAdapter(userToDoList,this,this)
        recyclerViewLists.layoutManager = LinearLayoutManager(this)

        database_list = Room.databaseBuilder(this, Database::class.java,"list").allowMainThreadQueries().build().listRoomDao()
        // Cargar BASE DE DATOS CON .allowMainThreadQueries() NO ES ASINCRONA
        database = Room.databaseBuilder(this, Database::class.java,"userRoom").allowMainThreadQueries().build().userRoomDao()

        // ACA UN IF PARA VER SI EL USER DE LA API EXISTE O NO EN LA BBDD Y CREARLO SINO EXISTE
        val userRoomActual = database.getUserRoomData(user.email)
        if (userRoomActual==null){
            val userToInsert =  UserRoom(user!!.email, user!!.first_name, user!!.last_name, user!!.phone, user!!.profile_photo, user!!.password) //,userToDoList)
            database.insert(userToInsert)
        }

        //Se obtienen en la listas de la Api
        //En caso de no tener internet, se cargan las listas que estaban en la BBDD
        getListsApi()


        // Aca una variable que se borra, es para ir viendo la bd en Debug
        val usersInRoomDao  = database.getAllUsers()
        println("inserted in BD")


        //Se agrega un lista al apretar el boton
        ButtonAddList.setOnClickListener(){
            onAddListButtonClick()
        }
        //Nos envia al menu
        //textViewUsername.setOnClickListener(){
        topAppBar.setNavigationOnClickListener {
            onUsernameClicked(user!!)
        }

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logo -> {
                    // Handle favorite icon press
                    Toast.makeText(applicationContext,"Nombre_Empresa. Since 2020",Toast.LENGTH_LONG).show()
                    true
                }
                else -> false
            }
        }
        //Nos envia al menu
        //imageViewLogoUsername.setOnClickListener(){
        //    onUsernameClicked(user!!)
        //}

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

                //Se actualiza la posicion de las listas en la BBDD y Api
                userToDoList.forEach{
                    database_list.insertList(it)
                    putListApi(it)
                }
                //userLog?.let { updateUserRoom(it) }
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                // Para intercambiar los index del item al moverse
                var counter = viewHolder.adapterPosition
                while (counter+1<userToDoList.size){

                    var updateList = userToDoList[counter+1]
                    updateList.position=counter
                    //Se actualizan las posiciones en la bbdd
                    database_list.insertList(updateList)
                    //Se actualizan las posiciones en la api
                    userToDoList.set(counter+1, updateList)
                    recyclerViewLists.adapter?.notifyItemChanged(counter+1)
                    counter++
                }
                listsCreatedCounter-=1
                // index de todos hacia abajo cambiados
                val list = ListsAdapter(userToDoList,this@ToDoListsActivity,this@ToDoListsActivity).getList(position)
                ListsAdapter(userToDoList,this@ToDoListsActivity,this@ToDoListsActivity).deleteList(viewHolder.adapterPosition)
                recyclerViewLists.adapter?.notifyItemRemoved(position)
                deleteListApi(list)
                database_list.deleteList(list)
                Log.d("ELIMINA",database_list.getAllListOrdered().toString())
                val snackbar = Snackbar.make(listLayout,"Eliminaste una Lista",Snackbar.LENGTH_LONG)
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
    override fun onItemClicked(list: ListRoom) {
        Log.d("LISTA SEND",list.toString())
        val intent2 = Intent(this, ListActivity::class.java)
        intent2.putExtra(LIST,list.id.toString()) // se pasa el primer nombre no el del item apretado :/
        Log.d("UPDATE desp",database_list.getAllListOrdered().toString())
        startActivity(intent2)
    }

    //Se abre el menu aplicacion
    fun onUsernameClicked(user: UserRoom){
        val intent3 = Intent(this,AppMenuActivity::class.java)
        intent3.putExtra(USER,user)
        startActivityForResult(intent3,1)
    }

    //Abre el dialog para cambiar el nombre de la lista
    override fun onButtonClicked(list: ListRoom) {
        val dialogList = DialogList2()
        val indexAsParameter = Bundle()
        indexAsParameter.putInt("KEY1",list.position)
        dialogList.arguments = indexAsParameter
        dialogList.show(supportFragmentManager, "dialogProduct")
    }

    //Se añade un lista a userToDoList
    override fun addList(nameList: String){
        var list = ListRoom(null,nameList,listsCreatedCounter)
        AsyncTask.execute{
            database_list.insertList(list)
            var add_list = database_list.getLastList()
            Log.d("UPDATE ant",database_list.getAllListOrdered().toString())
            postListApi(add_list)
        }
        recyclerViewLists.adapter?.notifyItemInserted(userToDoList.size)
        listsCreatedCounter++
    }
    //Cambia el nombre de la lista
    override fun changeName(nameList: String,indexRec : Int){
        Toast.makeText(applicationContext,"Se cambió el nombre a:  "+nameList,Toast.LENGTH_LONG).show()
        val updateList = userToDoList.get(indexRec)
        updateList.name=nameList
        userToDoList.set(updateList.position,updateList)
        //Se realiza update en la base de datos
        database_list.insertList(updateList)
        putListApi(updateList)
        recyclerViewLists.adapter?.notifyItemChanged(updateList.position)

        //userLog?.let { updateUserRoom(it) }
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
            userToDoList = savedInstanceState.getParcelableArrayList<ListRoom>("UserList")!!
        }
    }

    //Guarda las variables para cuando se gire la pantalla
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("user",userLog)
        outState.putParcelableArrayList("UserList",userToDoList)
    }

    //Recibe una lista actualizada con todos los items añadidos en el detalle de cada lista
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
    }*/

    /*
    // FUNCION PARA ACTUALIZAR EL USERROOM DE LA BBDD
    fun updateUserRoom(user: User){
        AsyncTask.execute{
            // LE PASAMOS EL USUARIO CON EL DATO YA CAMBIADO
            val userRoomActual = database.getUserRoomData(user.email)
            val userToUpdate =  UserRoom(userRoomActual.email, userRoomActual.name, userRoomActual.last_name, userRoomActual.phone, userRoomActual.profile_photo,
                                        userRoomActual.password,userRoomActual.to_do_lists)
            database.updateUser(userToUpdate)
            // ESTO ESTA SOLO PARA VER DEBUG Y VER QUE FUNCIONA
            val usersInRoomDao  = database.getAllUsers()
            println("update Database")
        }
    }

     */
    //Funcion que hace POST en la lista
    fun postListApi(list: ListRoom){
        val request = ApiService.buildService(ListApi::class.java)
        val call = request.postList(TOKEN,list)
        call.enqueue(object : Callback<ListRoom> {
            override fun onResponse(call: Call<ListRoom>, response: Response<ListRoom>) {
                Log.d("RESPONSE",response.body().toString())
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        if(response.message() == "Created"){
                            //Se hace update al id de la lista para que quede igual al de la api
                            database_list.updateIdList(response.body()!!.id,list.id)
                            //Luego se busca el item y se añada a la lista userToDOList
                            var add_list = database_list.getLastList()
                            userToDoList.add(add_list)
                            recyclerViewLists.adapter?.notifyItemInserted(userToDoList.size)
                            Toast.makeText(this@ToDoListsActivity, "Datos Actualizados", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Log.d("HOLAAAAAAAAA","NO recibe respuesta else")
                    Toast.makeText(this@ToDoListsActivity, "${response.errorBody()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ListRoom>, t: Throwable) {
                Log.d("HOLAAAAAAAAA","NO recibe respuesta onfaliure")
                userToDoList.add(list)
                recyclerViewLists.adapter?.notifyItemInserted(userToDoList.size)
                Toast.makeText(this@ToDoListsActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }
    //Funcion que hace el post de la lista en la api, pero no hace nada en la base de datos local
    fun postUpdateListApi(list: ListRoom){
        val request = ApiService.buildService(ListApi::class.java)
        val call = request.postList(TOKEN,list)
        call.enqueue(object : Callback<ListRoom> {
            override fun onResponse(call: Call<ListRoom>, response: Response<ListRoom>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        if(response.message() == "Created"){
                            Toast.makeText(this@ToDoListsActivity, "Datos Actualizados en la Api", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this@ToDoListsActivity, "${response.errorBody()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ListRoom>, t: Throwable) {
                Toast.makeText(this@ToDoListsActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    //Funcion que actualiza las listas ya ingresadas en la api
    fun putListApi(list: ListRoom){
        val request = ApiService.buildService(ListApi::class.java)
        val call = request.updateListApi(TOKEN,list.id,list)
        call.enqueue(object : Callback<ListRoom> {
            override fun onResponse(call: Call<ListRoom>, response: Response<ListRoom>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        Toast.makeText(this@ToDoListsActivity, "Datos Actualizados en la Api", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this@ToDoListsActivity, "${response.errorBody()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ListRoom>, t: Throwable) {
                Toast.makeText(this@ToDoListsActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }
    //Elimina una lista de la api
    fun deleteListApi(list: ListRoom){
        val request = ApiService.buildService(ListApi::class.java)
        val call = request.deleteListApi(TOKEN,list.id)
        call.enqueue(object : Callback<ListRoom> {
            override fun onResponse(call: Call<ListRoom>, response: Response<ListRoom>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        Toast.makeText(this@ToDoListsActivity, "Datos Actualizados en la Api", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this@ToDoListsActivity, "${response.errorBody()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ListRoom>, t: Throwable) {
                Toast.makeText(this@ToDoListsActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    //Se obtienen todas las listas de la api y si se esta ofline se ingresan las listas que estan en la bbdd
    fun getListsApi(){
        val request = ApiService.buildService(ListApi::class.java)
        val call = request.getListsApi(TOKEN)
        call.enqueue(object : Callback<List<ListRoom>> {
            override fun onResponse(call: Call<List<ListRoom>>, response: Response<List<ListRoom>>) {
                Log.d("RESPONSE",response.body().toString())
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        var bbdd_size = database_list.getAllList().size
                        var api_size = response.body()!!.size
                        //Si el largo de la bbdd no coincide con la de la api, se actualizan los datos de la api
                        if(bbdd_size != api_size){
                            for( i in api_size..(bbdd_size-1)){
                                var list = database_list.getAllList()[i]
                                postUpdateListApi(list)
                            }
                        }
                        //Se consume de la Api y se agregan las listas a la BBDD
                        response.body()!!.forEach {
                            database_list.insertList(it)
                            recyclerViewLists.adapter?.notifyItemInserted(userToDoList.size)
                        }
                        //Si hay listas en la BBDD, se agregan a userToDoList
                        if(database_list.getAllList() != null){
                            if(userToDoList != null){
                                var countLists = 0
                                database_list.getAllListOrdered().forEach{
                                    userToDoList.add(it)
                                    countLists++
                                }
                                listsCreatedCounter = countLists
                            }
                        }
                    }
                }
                else{
                    Log.d("HOLAAAAAAAAA","NO recibe respuesta else")
                    Toast.makeText(this@ToDoListsActivity, "${response.errorBody()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ListRoom>>, t: Throwable) {
                //En el caso de que no hay conexion a internet, se utilizan las listas que ya están en la BBDD
                //Si hay listas en la BBDD, se agregan a userToDoList
                if(database_list.getAllList() != null){
                    if(userToDoList != null){
                        var countLists = 0
                        database_list.getAllListOrdered().forEach{
                            userToDoList.add(it)
                            countLists++
                        }
                        listsCreatedCounter = countLists
                        Log.d("Hola",countLists.toString())
                    }
                }
                recyclerViewLists.adapter!!.notifyDataSetChanged()
                Toast.makeText(this@ToDoListsActivity, "No hay conexion a Internet", Toast.LENGTH_SHORT).show()
            }
        })

    }

}