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
import com.example.todolistproject.model.UserRoom
import com.example.todolistproject.networking.ApiService
import com.example.todolistproject.networking.UserApi
import com.example.todolistproject.utils.TOKEN
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    var user: UserRoom ?= null
    var confirmResponse: Boolean = false

    // FIREBASE GOOGLE
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val TAG = "MainActivity"
    private var mAuth: FirebaseAuth? = null
    //private var btnSignOut: Button? = null
    private val RC_SIGN_IN = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Se consume el usuario desde la API
        val request = ApiService.buildService(UserApi::class.java)
        val call = request.getUser(TOKEN)
        call.enqueue(object : Callback<UserRoom> {
            override fun onResponse(call: Call<UserRoom>, response: Response<UserRoom>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        var userResponse:UserRoom = UserRoom(
                            response.body()!!.email,
                            response.body()!!.first_name,
                            response.body()!!.last_name,
                            response.body()!!.phone,
                            response.body()!!.profile_photo,
                            password = "password"
                        )
                        user = userResponse
                        confirmResponse = true
                    }
                }
                else{
                    Toast.makeText(this@MainActivity, "${response.errorBody()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserRoom>, t: Throwable) {
                Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // FIREBASE GOOGLE
        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        sign_in_button.setOnClickListener { signIn() }

    }

    // FIREBASE GOOGLE
    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val acc = completedTask.getResult(
                ApiException::class.java
            )
            //Toast.makeText(this@MainActivity, "Inicio de Sesión exitoso", Toast.LENGTH_SHORT).show()
            FirebaseGoogleAuth(acc)

        } catch (e: ApiException) {
            Toast.makeText(this@MainActivity, "Sign In Failed", Toast.LENGTH_SHORT).show()
            FirebaseGoogleAuth(null)
        }
    }

    private fun FirebaseGoogleAuth(acct: GoogleSignInAccount?) { //check if the account is null
        if (acct != null) {
            val authCredential =
                GoogleAuthProvider.getCredential(acct.idToken, null)
            mAuth!!.signInWithCredential(authCredential).addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Inicio de Sesión exitoso", Toast.LENGTH_SHORT).show()
                    val user = mAuth!!.currentUser
                    updateUI(user)


                } else {
                    //Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
        } else {
            Toast.makeText(this@MainActivity, "acc failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(fUser: FirebaseUser?) {
        val account =
            GoogleSignIn.getLastSignedInAccount(applicationContext)
        if (account != null) {
            val personName = account.displayName
            val personGivenName = account.givenName
            val personFamilyName = account.familyName
            val personEmail = account.email
            val personId = account.id
            val personPhoto = account.photoUrl
            Toast.makeText(this@MainActivity, personEmail, Toast.LENGTH_SHORT).show()

            val intent = Intent(this@MainActivity, ToDoListsActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            if(confirmResponse){
                intent.putExtra(USER, user)
                //startActivity(intent)
            }
            else{
                var userDefault:UserRoom = UserRoom("sinconexion@gmail.com","name","last name", "+56989022776", "", "password")
                intent.putExtra(USER, userDefault)
                //startActivity(intent)
                Toast.makeText(applicationContext,"Usuario Default", Toast.LENGTH_LONG).show()
            }

            applicationContext.startActivity(intent)
        }
    }

    //Al apretar ingresar, lleva a la vista de lista
    fun onLoginButtonClick(view: View) {
        val intent = Intent(view.context, ToDoListsActivity::class.java)
        if(confirmResponse){
            intent.putExtra(USER, user)
            startActivity(intent)
        }
        else{
            var userDefault:UserRoom = UserRoom("sinconexion@gmail.com","name","last name", "+56989022776", "", "password")
            intent.putExtra(USER, userDefault)
            startActivity(intent)
            Toast.makeText(view.context,"Usuario Default", Toast.LENGTH_LONG).show()
        }
    }

    fun onForgotPasswordButtonClick(v: View){
        Toast.makeText(v.context,"No implementado aun", Toast.LENGTH_LONG).show()
    } // Para olvidé contraseña

    fun onRegisterButtonClick(v: View){
            Toast.makeText(v.context,"No implementado aun", Toast.LENGTH_LONG).show()
    } // Para registrarse

}

