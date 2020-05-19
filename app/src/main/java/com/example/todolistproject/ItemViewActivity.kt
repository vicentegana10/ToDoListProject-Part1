package com.example.todolistproject
// Este es el manejo de la vista personaliza de cada item, no se usa para esta entrega.
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.todolistproject.classes.Item
import kotlinx.android.synthetic.main.activity_item_view.*
import kotlinx.android.synthetic.main.dialog_list.*

class ItemViewActivity : AppCompatActivity() {

    companion object {
        var ITEM = "ITEM"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_view)

        var item: Item = intent.getParcelableExtra(ITEM)!!

        textViewItemName.text = item.name

    }


}
