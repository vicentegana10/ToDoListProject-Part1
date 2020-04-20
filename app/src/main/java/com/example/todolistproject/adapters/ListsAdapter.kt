package com.example.todolistproject.adapters

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistproject.ListActivity
import com.example.todolistproject.R
import com.example.todolistproject.classes.ToDoList
import kotlinx.android.synthetic.main.recyclerview_list_row.*
import kotlinx.android.synthetic.main.recyclerview_list_row.view.*


class ListsAdapter(private val list: ArrayList<ToDoList>) :
    RecyclerView.Adapter<ListsAdapter.ToDoListCard>() {

    override fun onBindViewHolder(holder: ToDoListCard, position: Int) {
        val toDoList = list[position]
        holder.bindToDoList(toDoList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoListCard {
        val inflatedView = parent.inflate(R.layout.recyclerview_list_row, false)
        return ToDoListCard(inflatedView)
    }

    override fun getItemCount(): Int = list.count()

    class ToDoListCard(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var todolist: ToDoList? = null

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            print("button clicked") // es un mensaje para el terminal
        }

        fun bindToDoList(list: ToDoList) {
            this.todolist = list
            view.buttonListNameRecyclerView.text = list.name
        }
    }

}



