package com.example.todolistproject.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistproject.R
import com.example.todolistproject.ToDoListsActivity
import com.example.todolistproject.model.ListRoom
import kotlinx.android.synthetic.main.recyclerview_list_row.view.*
import java.text.FieldPosition

class ListsAdapter(private var userlist: ArrayList<ListRoom>,val itemClickListener: ToDoListsActivity,val buttonClickListener: ToDoListsActivity): RecyclerView.Adapter<ListsAdapter.ListViewHolder>() {

    fun getList(position: Int): ListRoom{
        return userlist[position]
    }

    fun deleteList(position: Int){
        userlist.removeAt(position)
    }

    fun restoreList(position: Int, list: ListRoom){
        userlist.add(position,list)
        notifyItemInserted(position)
    }

    fun changeListPosition(initialPosition: Int, finalPosition: Int){
        val list = userlist[initialPosition]
        userlist.removeAt(initialPosition)
        userlist.add(finalPosition,list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_list_row,parent,false)
        return ListViewHolder(itemView)
    }

    override fun getItemCount() = userlist.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentItem = userlist[position]
        holder.bindContact(currentItem,itemClickListener,buttonClickListener)
    }

    class ListViewHolder(v: View): RecyclerView.ViewHolder(v), View.OnClickListener{
        private var view: View = v
        private var list: ListRoom? = null

        init {
            view.setOnClickListener(this)
        }

        fun bindContact(list: ListRoom,clickListener: OnItemClickListener,clickListener2: OnButtonClickListener){
            this.list = list
            view.textViewList.text = list.name

            if(list.shared == true){
                view.rowList.setBackgroundColor(Color.GREEN)
            }
            else{
                view.rowList.setBackgroundColor(Color.BLUE)
            }

            itemView.setOnClickListener {
                clickListener.onItemClicked(list)
            }
            view.renameListButton.setOnClickListener{
                clickListener2.onButtonClicked(list) //list
            }
        }

        override fun onClick(v: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}

interface OnItemClickListener{
    fun onItemClicked(list: ListRoom)
}
interface OnButtonClickListener{
    fun onButtonClicked(list: ListRoom) //
}