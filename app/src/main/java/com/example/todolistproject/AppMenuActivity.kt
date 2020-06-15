package com.example.todolistproject
// Nueva activity creada para ver el perfil o cerrar sesion
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.abs.clase11.utils.loadPhoto
import com.example.todolistproject.model.UserRoom
import kotlinx.android.synthetic.main.activity_app_menu.*
import kotlinx.android.synthetic.main.activity_app_menu.topAppBarMenu
import kotlinx.android.synthetic.main.activity_to_do_lists.*

class AppMenuActivity : AppCompatActivity() {

    companion object {
        var USER = "USER"
    }

    var userLog1: UserRoom? = null//Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_menu)

        topAppBarMenu.title = "Menu"

        // Muestra en su view el nombre y apellidos, el mail y la foto gracias a GlideAppModule en ImageViewExtension
        var user:UserRoom = intent.getParcelableExtra(USER)
        userLog1 = user
        textViewUsernameInAppMenu.text=(user.first_name+" "+user.last_name)
        textViewtextViewEmailInAppMenu.text=user.email
        imageViewAccountBox.loadPhoto(user.profile_photo)

        ButtonLogOut.setOnClickListener(){
            LogOut()
        }

        topAppBarMenu.setNavigationOnClickListener{
            backToToDoLists(user)
            //onBackPressed()
        }
        topAppBarMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logo -> {
                    // Handle favorite icon press
                    Toast.makeText(applicationContext,"Nombre_Empresa. Since 2020", Toast.LENGTH_LONG).show()
                    true
                }
                else -> false
            }
        }

        ButtonGoToMyProfile.setOnClickListener(){
            onMyProfileClicked(user)
        }
    }

    // una funcion para cerrar sesion
    fun LogOut(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    // funcion para ir a la View del perfil
    fun onMyProfileClicked(user1: UserRoom){
        val intent4 = Intent(this,MyProfileActivity::class.java)
        intent4.putExtra(USER,user1)
        startActivityForResult(intent4,1)
    }

    // boton flecha para volver a las ToDoLists
    fun backToToDoLists(user: UserRoom){
        val intent3 = Intent(this,ToDoListsActivity::class.java)
        intent3.putExtra(USER,user)
        startActivityForResult(intent3,1)
    }
}
