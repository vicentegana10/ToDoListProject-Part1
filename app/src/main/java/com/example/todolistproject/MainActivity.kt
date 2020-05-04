package com.example.todolistproject
// Aca el manejo del login.   Es dummy pero recuerda el nombre del usuario y lo muestra despues.
// Se crea la clase user.
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todolistproject.ToDoListsActivity.Companion.USER
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

    fun onLoginButtonClick(view: View) { //falta pasarle info a ToDoListsActivity
        val user = User(editTextMail.text.toString(),editTextPassword.text.toString())
        val intent = Intent(view.context, ToDoListsActivity::class.java)
        intent.putExtra(USER,user)
        view.context.startActivity(intent)
    }

    fun onForgotPasswordButtonClick(v: View){
        Toast.makeText(v.context,"No implementado aun", Toast.LENGTH_LONG).show()
    } // Para olvidé contraseña

    fun onRegisterButtonClick(v: View){
            Toast.makeText(v.context,"No implementado aun", Toast.LENGTH_LONG).show()
    } // Para registrarse
}

@Parcelize
data class User(val email:String,val password:String):Parcelable
