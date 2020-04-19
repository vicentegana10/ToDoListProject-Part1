package com.example.todolistproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


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

    fun onLoginButtonClick(v: View?) { //falta pasarle info a ToDoListsActivity
        val myIntent = Intent(baseContext, ToDoListsActivity::class.java)
        startActivity(myIntent)
    }

    fun onForgotPasswordButtonClick(v: View?){} // Para olvidé contraseña

    fun onRegisterButtonClick(v: View?){} // Para registrarse


}
