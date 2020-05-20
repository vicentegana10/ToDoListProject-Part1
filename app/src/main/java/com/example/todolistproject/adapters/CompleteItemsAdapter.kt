package com.example.todolistproject.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistproject.R
import com.example.todolistproject.classes.Item
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*

class CompleteItemsAdapter(private val itemList: ArrayList<Item>) :
    RecyclerView.Adapter<CompleteItemsAdapter.ItemCard>() {

    override fun onBindViewHolder(holder: ItemCard, position: Int) {
        val itemList = itemList[position]
        holder.bindItemList(itemList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCard {
        val inflatedView = parent.inflate(R.layout.recyclerview_item_row, false)
        return ItemCard(inflatedView)
    }

    override fun getItemCount(): Int = itemList.count()

    class ItemCard(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var itemList: Item? = null

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            Toast.makeText(view.context,"No implementado aun",Toast.LENGTH_LONG).show()
            print("button clicked")
        }

        fun bindItemList(itemList: Item) {
            this.itemList = itemList
            view.textViewItemName.text = itemList.name
        }
    }

}
