package com.example.todolistproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistproject.List
import com.example.todolistproject.R
import com.example.todolistproject.ToDoListsActivity
import kotlinx.android.synthetic.main.recyclerview_list_row.view.*

class ListsAdapter(private var userlist: ArrayList<List>,val itemClickListener: ToDoListsActivity): RecyclerView.Adapter<ListsAdapter.ListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_list_row,parent,false)
        return ListViewHolder(itemView)
    }

    override fun getItemCount() = userlist.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentItem = userlist[position]
        holder.bindContact(currentItem,itemClickListener)
    }

    class ListViewHolder(v: View): RecyclerView.ViewHolder(v), View.OnClickListener{
        private var view: View = v
        private var list: List? = null

        init {
            view.setOnClickListener(this)
        }

        fun bindContact(list: List,clickListener: OnItemClickListener){
            this.list = list
            view.textViewList.text = list.name

            itemView.setOnClickListener {
                clickListener.onItemClicked(list)
            }
        }

        override fun onClick(v: View?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}

interface OnItemClickListener{
    fun onItemClicked(list: List)
}