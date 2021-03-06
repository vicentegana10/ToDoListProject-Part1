package com.example.todolistproject.adapters

import Dialogs.DialogChangeItemName
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistproject.R
import com.example.todolistproject.ToDoListsActivity
import com.example.todolistproject.classes.Item
import com.example.todolistproject.model.ItemRoom
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*


class UncompleteItemsAdapter(private var itemList: ArrayList<ItemRoom>,val itemClickListener: OnUnCompleteItemClickListener) : RecyclerView.Adapter<UncompleteItemsAdapter.ItemCard>() {

    fun getItem(position: Int): ItemRoom {
        return itemList[position]
    }

    fun deleteItem(position: Int){
        itemList.removeAt(position)
    }

    fun restoreItem(position: Int, item: ItemRoom){
        itemList.add(position,item)
    }

    fun changeListPosition(initialPosition: Int, finalPosition: Int){
        val list = itemList[initialPosition]
        itemList.removeAt(initialPosition)
        itemList.add(finalPosition,list)
    }

    fun refereshListItems(listItem: ArrayList<ItemRoom>){
        itemList = listItem
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCard {
        val inflatedView = parent.inflate(R.layout.recyclerview_item_row, false)
        return ItemCard(inflatedView)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ItemCard, position: Int) {
        Log.d("POSi",itemList.toString())
        val itemList = itemList[position]
        holder.bindItemList(itemList,itemClickListener)
    }

    class ItemCard(v: View) : RecyclerView.ViewHolder(v){
        private var view: View = v
        private var itemList: ItemRoom? = null


        fun bindItemList(itemList: ItemRoom,clickListener: OnUnCompleteItemClickListener) {
            this.itemList = itemList
            view.textViewItemName.text = itemList.name
            if (itemList.starred==false){
                view.imageViewStar.setVisibility(View.INVISIBLE)
            }
            else{view.imageViewStar.setVisibility(View.VISIBLE)}

            if(itemList.done){
                view.imageViewItemDone.setImageResource(R.drawable.ic_done_green_24dp)
            }

            view.setOnClickListener{
                clickListener.onItemClicked(itemList,adapterPosition)
            }
            view.imageViewItemDone.setOnClickListener(){
                clickListener.changeStateItem(itemList,adapterPosition)
            }


        }
    }

}

interface OnUnCompleteItemClickListener{
    fun onItemClicked(item: ItemRoom, position: Int)
    fun changeStateItem(item: ItemRoom, position: Int)
}