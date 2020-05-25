package com.example.todolistproject
// Nueva Activity para ver el perfil del usuario y poder editar su informacion
import Dialogs.DialogChangeProfileName
import Dialogs.dialogChangeListener
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.abs.clase11.utils.loadPhoto
import kotlinx.android.synthetic.main.activity_my_profile.*

class MyProfileActivity : AppCompatActivity(),dialogChangeListener {

    companion object {
        var USER = "USER"
    }

    var userLog1: User? = null//Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        // Se muestra en la View los datos del usuario
        var user: User = intent.getParcelableExtra(USER)
        userLog1 = user
        textViewProfileEmail.text=(textViewProfileEmail.text.toString()+user.email)
        textViewProfileName.text=(textViewProfileName.text.toString()+user.name)
        textViewProfileLastName.text=(textViewProfileLastName.text.toString()+user.last_name)
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

    // Segun el tipo que haya recibido el Dialog, se cambiaraá lo inidcado
    override fun changeName(newName: String, user: User,type: String){
        if (type=="name") {
            user.name = newName
            textViewProfileName.text = ("Nombre: " + newName)
        }
        if (type=="lastName") {
            user.last_name = newName
            textViewProfileLastName.text = ("Apellido: " + newName)
        }
        if (type=="phone") {
            user.phone = newName
            textViewProfilePhone.text = ("Teléfono: " + newName)
        }
    }

    fun backToAppMenu(user: User){
        val intent3 = Intent(this,AppMenuActivity::class.java)
        intent3.putExtra(USER,user)
        startActivityForResult(intent3,1)
    }



}
