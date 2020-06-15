package com.example.todolistproject
// Nueva Activity para ver el perfil del usuario y poder editar su informacion
import Dialogs.DialogChangeProfileName
import Dialogs.dialogChangeListener
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.room.Room
import com.abs.clase11.utils.loadPhoto
import com.example.todolistproject.model.Database
import com.example.todolistproject.model.UserRoom
import com.example.todolistproject.model.UserRoomDao
import com.example.todolistproject.networking.ApiService
import com.example.todolistproject.networking.UserApi
import com.example.todolistproject.utils.TOKEN
import kotlinx.android.synthetic.main.activity_app_menu.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.activity_my_profile.topAppBarProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyProfileActivity : AppCompatActivity(),dialogChangeListener {

    companion object {
        var USER = "USER"
    }

    var userLog1: UserRoom? = null//Usuario

    lateinit var database: UserRoomDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        topAppBarProfile.title = "Perfil"

        // Se muestra en la View los datos del usuario
        var user: UserRoom = intent.getParcelableExtra(USER)
        userLog1 = user

        // Cargar BASE DE DATOS CON allowMainThreadQueries() ASIQUE NO ES ASINCRONA
        database = Room.databaseBuilder(this, Database::class.java,"userRoom").allowMainThreadQueries().build().userRoomDao()

        // userRoomActual sera el usuario actual ya que le pasamos el mail que captamos de la API
        val userRoomActual = database.getUserRoomData(user.email)
        // COMO EJEMPLO PARA VER SI FUNCIONA SOLO JUGUE CON EL TEXTVIEW DE ESTA VISTA
        textViewProfileLastName.text=(textViewProfileLastName.text.toString()+userRoomActual.last_name)

        textViewProfileEmail.text=(textViewProfileEmail.text.toString()+user.email)
        textViewProfileName.text=(textViewProfileName.text.toString()+user.first_name)
        //textViewProfileLastName.text=(textViewProfileLastName.text.toString()+user.last_name)
        textViewProfilePhone.text=(textViewProfilePhone.text.toString()+user.phone)
        imageViewAccountCircle.loadPhoto(user.profile_photo)

        topAppBarProfile.setNavigationOnClickListener{
            backToAppMenu(user)
        }
        topAppBarProfile.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logo -> {
                    // Handle favorite icon press
                    Toast.makeText(applicationContext,"Nombre_Empresa. Since 2020", Toast.LENGTH_LONG).show()
                    true
                }
                else -> false
            }
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
    fun onChangeProfileAtributeButtonClick(user: UserRoom, type: String){
        val dialogChange = DialogChangeProfileName()
        val userAndTypeAsParameter = Bundle()
        userAndTypeAsParameter.putParcelable("KEY1",user)
        userAndTypeAsParameter.putString("KEY2",type)
        dialogChange.arguments = userAndTypeAsParameter
        dialogChange.show(supportFragmentManager, "dialogChange")
    }

    // Segun el tipo que haya recibido el Dialog, se cambiará lo inidcado Y SE ACTUALIZA BBDD DE ROOM
    override fun changeName(newName: String, user: UserRoom,type: String){
        if (type=="name") {
            user.first_name = newName
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

    fun backToAppMenu(user: UserRoom){
        val intent3 = Intent(this,AppMenuActivity::class.java)
        intent3.putExtra(USER,user)
        startActivityForResult(intent3,1)
    }

    // FUNCION PARA ACTUALIZAR EL USERROOM DE LA BBDD
    fun updateUserRoom(user: UserRoom){
        AsyncTask.execute{
            // LE PASAMOS EL USUARIO CON EL DATO YA CAMBIADO
            val userRoomActual = database.getUserRoomData(user.email)
            val userToUpdate =  UserRoom(user!!.email, user!!.first_name, user!!.last_name, user!!.phone, user!!.profile_photo, user!!.password)//,userRoomActual.to_do_lists)
            database.updateUser(userToUpdate)
            // ESTO ESTA SOLO PARA VER DEBUG Y VER QUE FUNCIONA
            val usersInRoomDao  = database.getAllUsers()
            Log.d("Update",usersInRoomDao.toString())
        }
        updateUserApi(user)
    }

    fun updateUserApi(user: UserRoom){
        val request = ApiService.buildService(UserApi::class.java)
        val call = request.updateUser(TOKEN,user)
        call.enqueue(object : Callback<UserRoom> {
            override fun onResponse(call: Call<UserRoom>, response: Response<UserRoom>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        if(response.message() == "OK"){
                            Toast.makeText(this@MyProfileActivity, "Datos Actualizados", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this@MyProfileActivity, "${response.errorBody()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserRoom>, t: Throwable) {
                Toast.makeText(this@MyProfileActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }



}
