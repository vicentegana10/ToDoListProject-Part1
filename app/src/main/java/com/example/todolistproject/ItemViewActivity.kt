package com.example.todolistproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class ItemViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_view)
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

    fun onBackToListViewButtonClick(view: View) { //falta pasarle info a ListActivity
        val myIntent = Intent(baseContext, ListActivity::class.java)
        startActivity(myIntent)
    }
}
