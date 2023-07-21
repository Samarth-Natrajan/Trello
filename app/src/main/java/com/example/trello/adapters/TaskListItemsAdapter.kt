package com.example.trello.adapters

import android.content.Context
import android.content.res.Resources
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trello.R
import com.example.trello.activities.TaskListActivity
import com.example.trello.models.Task

open class TaskListItemsAdapter(private val context: Context,private var list: ArrayList<Task>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task,parent,false)
        val layoutparams = LinearLayout.LayoutParams(
            (parent.width*0.7).toInt(),LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutparams.setMargins((15.toDp()).toPx(),0,(40.toDp()).toPx(),0)
        view.layoutParams= layoutparams
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if(position==list.size-1){
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility = View.VISIBLE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility = View.GONE
            }
            else{
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility = View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility = View.VISIBLE
            }
            holder.itemView.findViewById<TextView>(R.id.tv_task_list_title).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).setOnClickListener {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility = View.GONE
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name).visibility = View.VISIBLE
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_close_list_name).setOnClickListener {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility = View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name).visibility = View.GONE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_done_list_name).setOnClickListener {
                val listName = holder.itemView.findViewById<EditText>(R.id.et_task_list_name).text.toString()
                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.createTaskList(listName)
                    }
                }
                else{
                    Toast.makeText(context,"Please enter list name.",Toast.LENGTH_SHORT).show()
                }
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_edit_list_name).setOnClickListener {
                holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name).setText(model.title)
                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility = View.GONE
                holder.itemView.findViewById<CardView>(R.id.cv_edit_task_list_name).visibility = View.VISIBLE

            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_close_editable_view).setOnClickListener {
                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility = View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_edit_task_list_name).visibility = View.GONE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_done_edit_list_name).setOnClickListener {
                val listName = holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name).text.toString()
                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.updateTaskList(position,listName,model)
                    }
                }
                else{
                    Toast.makeText(context,"Please enter list name.",Toast.LENGTH_SHORT).show()
                }
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_list).setOnClickListener {
                if(context is TaskListActivity) {
                    context.deleteTaskList(position)//can implement it using alert dialog
                }
            }
            holder.itemView.findViewById<TextView>(R.id.tv_add_card).setOnClickListener {
                holder.itemView.findViewById<TextView>(R.id.tv_add_card).visibility = View.GONE
                holder.itemView.findViewById<CardView>(R.id.cv_add_card).visibility = View.VISIBLE

            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_close_card_name).setOnClickListener {
                holder.itemView.findViewById<TextView>(R.id.tv_add_card).visibility = View.VISIBLE
                holder.itemView.findViewById<CardView>(R.id.cv_add_card).visibility = View.GONE
            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_done_card_name).setOnClickListener {
                val cardName = holder.itemView.findViewById<EditText>(R.id.et_card_name).text.toString()
                if(cardName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.addCardToTaskList(position,cardName)
                    }
                }
                else{
                    Toast.makeText(context,"Please enter a card name.",Toast.LENGTH_SHORT).show()
                }
            }
            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).layoutManager = LinearLayoutManager(context)
            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).setHasFixedSize(true)
            val adapter = CardListItemsAdapter(context,model.cards)
            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).adapter = adapter
            adapter.setOnClickListener(
                object:CardListItemsAdapter.OnClickListener{
                    override fun onClick(cardPosition: Int) {
                        if(context is TaskListActivity){
                            context.cardDetails(holder.adapterPosition, cardPosition)
                        }
                    }
                }
            )
        }
    }
    private fun Int.toDp():Int = (this/Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx():Int = (this*Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(view:View):RecyclerView.ViewHolder(view)
}