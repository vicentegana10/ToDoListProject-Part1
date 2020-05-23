package com.example.todolistproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.abs.clase11.utils.loadPhoto
import kotlinx.android.synthetic.main.activity_app_menu.*

class AppMenuActivity : AppCompatActivity() {

    companion object {
        var USER = "USER"
    }

    var userLog1: User? = null//Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_menu)

        var user:User = intent.getParcelableExtra(USER)
        userLog1 = user
        textViewUsernameInAppMenu.text=(user.name+" "+user.last_name)
        textViewtextViewEmailInAppMenu.text=user.email
        imageViewAccountBox.loadPhoto(user.profile_photo)    //ESO PARA EL FINAL PORQUE CREO QUE SACA UN URL DE LA API, LO DEJE ASI MIENTRAS

        ButtonLogOut.setOnClickListener(){
            LogOut()
        }

        buttonBackAppMenu.setOnClickListener(){
            backToToDoLists(user)
            //onBackPressed()
        }

        ButtonGoToMyProfile.setOnClickListener(){
            onMyProfileClicked(user)
        }
    }

    fun LogOut(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun onMyProfileClicked(user1: User){
        val intent4 = Intent(this,MyProfileActivity::class.java)
        intent4.putExtra(USER,user1)
        startActivityForResult(intent4,1)
    }

    fun backToToDoLists(user: User){
        val intent3 = Intent(this,ToDoListsActivity::class.java)
        intent3.putExtra(USER,user)
        startActivityForResult(intent3,1)
    }
}
