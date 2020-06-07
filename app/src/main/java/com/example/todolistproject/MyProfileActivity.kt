package com.example.todolistproject
// Nueva Activity para ver el perfil del usuario y poder editar su informacion
import Dialogs.DialogChangeProfileName
import Dialogs.dialogChangeListener
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.abs.clase11.utils.loadPhoto
import com.example.todolistproject.model.Database
import com.example.todolistproject.model.UserRoom
import com.example.todolistproject.model.UserRoomDao
import kotlinx.android.synthetic.main.activity_my_profile.*

class MyProfileActivity : AppCompatActivity(),dialogChangeListener {

    companion object {
        var USER = "USER"
    }

    var userLog1: User? = null//Usuario

    lateinit var database: UserRoomDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)


        // Se muestra en la View los datos del usuario
        var user: User = intent.getParcelableExtra(USER)
        userLog1 = user

        // Cargar BASE DE DATOS CON allowMainThreadQueries() ASIQUE NO ES ASINCRONA
        database = Room.databaseBuilder(this, Database::class.java,"userRoom").allowMainThreadQueries().build().userRoomDao()

        // userRoomActual sera el usuario actual ya que le pasamos el mail que captamos de la API
        val userRoomActual = database.getUserRoomData(user.email)
        // COMO EJEMPLO PARA VER SI FUNCIONA SOLO JUGUE CON EL TEXTVIEW DE ESTA VISTA
        textViewProfileLastName.text=(textViewProfileLastName.text.toString()+userRoomActual.last_name)

        textViewProfileEmail.text=(textViewProfileEmail.text.toString()+user.email)
        textViewProfileName.text=(textViewProfileName.text.toString()+user.name)
        //textViewProfileLastName.text=(textViewProfileLastName.text.toString()+user.last_name)
        textViewProfilePhone.text=(textViewProfilePhone.text.toString()+user.phone)
        imageViewAccountCircle.loadPhoto(user.profile_photo)

        buttonBackMyProfile.setOnClickListener(){
            backToAppMenu(user)
            //onBackPressed()
        }

        // CLICKLISTENERS para cuando se quiera editar un campo, se abrirá un Dialog que recibe como parametro que se quiere cambiar
        imageViewEditName.setOnClickListener() {
            onChangeProfileAtributeButtonClick(user,"name")
        }
        imageViewEditLastName.setOnClickListener() {
            onChangeProfileAtributeButtonClick(user,"lastName")
        }
        imageViewEditPhone.setOnClickListener() {
            onChangeProfileAtributeButtonClick(user,"phone")
        }
    }

    // Aca se abre el Dialog recibiendo usuario y lo que se desea cambiar
    fun onChangeProfileAtributeButtonClick(user: User, type: String){
        val dialogChange = DialogChangeProfileName()
        val userAndTypeAsParameter = Bundle()
        userAndTypeAsParameter.putParcelable("KEY1",user)
        userAndTypeAsParameter.putString("KEY2",type)
        dialogChange.arguments = userAndTypeAsParameter
        dialogChange.show(supportFragmentManager, "dialogChange")
    }

    // Segun el tipo que haya recibido el Dialog, se cambiará lo inidcado Y SE ACTUALIZA BBDD DE ROOM
    override fun changeName(newName: String, user: User,type: String){
        if (type=="name") {
            user.name = newName
            textViewProfileName.text = ("Nombre: " + newName)
            updateUserRoom(user)

        }
        if (type=="lastName") {
            user.last_name = newName
            textViewProfileLastName.text = ("Apellido: " + newName)
            updateUserRoom(user)
        }
        if (type=="phone") {
            user.phone = newName
            textViewProfilePhone.text = ("Teléfono: " + newName)
            updateUserRoom(user)
        }
    }

    fun backToAppMenu(user: User){
        val intent3 = Intent(this,AppMenuActivity::class.java)
        intent3.putExtra(USER,user)
        startActivityForResult(intent3,1)
    }

    // FUNCION PARA ACTUALIZAR EL USERROOM DE LA BBDD
    fun updateUserRoom(user: User){
        AsyncTask.execute{
            // LE PASAMOS EL USUARIO CON EL DATO YA CAMBIADO
            val userRoomActual = database.getUserRoomData(user.email)
            val userToUpdate =  UserRoom(user!!.email, user!!.name, user!!.last_name, user!!.phone, user!!.profile_photo, user!!.password)//,userRoomActual.to_do_lists)
            database.updateUser(userToUpdate)
            // ESTO ESTA SOLO PARA VER DEBUG Y VER QUE FUNCIONA
            val usersInRoomDao  = database.getAllUsers()
            println("update Database")
        }
    }



}
