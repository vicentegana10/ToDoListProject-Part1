package com.example.todolistproject.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistproject.List
import com.example.todolistproject.R
import com.example.todolistproject.ToDoListsActivity
import com.example.todolistproject.classes.Item
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*


class UncompleteItemsAdapter(private val itemList: ArrayList<Item>,val itemClickListener: OnUnCompleteItemClickListener) : RecyclerView.Adapter<UncompleteItemsAdapter.ItemCard>() {

    fun getItem(position: Int): Item {
        return itemList[position]
    }

    fun deleteItem(position: Int){
        itemList.removeAt(position)
    }

    fun restoreItem(position: Int, item: Item){
        itemList.add(position,item)
    }

    fun changeListPosition(initialPosition: Int, finalPosition: Int){
        val list = itemList[initialPosition]
        itemList.removeAt(initialPosition)
        itemList.add(finalPosition,list)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCard {
        val inflatedView = parent.inflate(R.layout.recyclerview_item_row, false)
        return ItemCard(inflatedView)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ItemCard, position: Int) {
        val itemList = itemList[position]
        holder.bindItemList(itemList,itemClickListener)
    }

    class ItemCard(v: View) : RecyclerView.ViewHolder(v){
        private var view: View = v
        private var itemList: Item? = null


        fun bindItemList(itemList: Item,clickListener: OnUnCompleteItemClickListener) {
            this.itemList = itemList
            view.textViewItemName.text = itemList.name
            view.checkBoxItem.text = itemList.boolCheckBox
            view.imagePriority.text = itemList.boolPriority

            view.setOnClickListener{
                clickListener.onItemClicked(itemList)
            }
        }
    }

}

interface OnUnCompleteItemClickListener{
    fun onItemClicked(item: Item)
}