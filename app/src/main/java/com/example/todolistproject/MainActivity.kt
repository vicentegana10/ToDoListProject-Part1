package com.example.todolistproject
// Aca el manejo del login.   Es dummy pero recuerda el nombre del usuario y lo muestra despues.
// Se crea la clase user.
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todolistproject.ToDoListsActivity.Companion.USER
import com.example.todolistproject.networking.ApiService
import com.example.todolistproject.networking.UserApi
import com.example.todolistproject.utils.TOKEN
import kotlinx.android.parcel.Parcelize
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    var user: User ?= null
    var confirmResponse: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Se consume el usuario desde la API
        val request = ApiService.buildService(UserApi::class.java)
        val call = request.getUser(TOKEN)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        var userResponse:User = User(
                            response.body()!!.email,
                            response.body()!!.first_name,
                            response.body()!!.last_name,
                            response.body()!!.phone,
                            response.body()!!.profile_photo,
                            response.body()!!.api_key,
                            "password"
                        )
                        user = userResponse
                        confirmResponse = true
                    }
                }
                else{
                    Toast.makeText(this@MainActivity, "${response.errorBody()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })


    }

    //Al apretar ingresar, lleva a la vista de lista
    fun onLoginButtonClick(view: View) {
        if(confirmResponse){
            val intent = Intent(view.context, ToDoListsActivity::class.java)
            intent.putExtra(USER, user)
            startActivity(intent)
        }
        else{
            Toast.makeText(view.context,"Esperando Respuesta", Toast.LENGTH_LONG).show()
        }
    }

    fun onForgotPasswordButtonClick(v: View){
        Toast.makeText(v.context,"No implementado aun", Toast.LENGTH_LONG).show()
    } // Para olvidé contraseña

    fun onRegisterButtonClick(v: View){
            Toast.makeText(v.context,"No implementado aun", Toast.LENGTH_LONG).show()
    } // Para registrarse

}

@Parcelize
data class User(var email: String,var first_name: String, var last_name: String, var phone: String, var profile_photo: String,var api_key: String,val password:String): Parcelable
