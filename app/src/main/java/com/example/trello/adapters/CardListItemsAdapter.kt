package com.example.trello.adapters

import android.content.Context
import android.graphics.Color
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.view.Display.Mode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trello.R
import com.example.trello.activities.TaskListActivity
import com.example.trello.models.Card
import com.example.trello.models.SelectedMembers

class CardListItemsAdapter(private val context: Context,private var list:ArrayList<Card>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener:OnClickListener?=null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card,parent,false))

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if(model.labelcolor.isNotEmpty()){
                holder.itemView.findViewById<View>(R.id.view_label_color).visibility=View.VISIBLE
                holder.itemView.findViewById<View>(R.id.view_label_color)
                    .setBackgroundColor(Color.parseColor(model.labelcolor))
            }
            else{
                holder.itemView.findViewById<View>(R.id.view_label_color).visibility=View.GONE
            }
            holder.itemView.findViewById<TextView>(R.id.tv_card_name).text = model.name
            if((context as TaskListActivity).mAssignedMembersDetailsList.size>0){
                val selectedMembersList:ArrayList<SelectedMembers> = ArrayList()
                for(i in context.mAssignedMembersDetailsList.indices){
                    for(j in model.assignedTo){
                        if(context.mAssignedMembersDetailsList[i].id == j){
                            val selectedMember = SelectedMembers(context.mAssignedMembersDetailsList[i].id,
                            context.mAssignedMembersDetailsList[i].image)
                            selectedMembersList.add(selectedMember)
                        }
                    }
                }
                if(selectedMembersList.size>0){
                    if(selectedMembersList.size==1&&selectedMembersList[0].id==model.createdBy){
                        holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list).visibility = View.GONE
                    }
                    else{
                        holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list).visibility = View.VISIBLE
                        holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list).layoutManager =
                            GridLayoutManager(context,4)
                        val adapter = CardMembersListItemsAdapter(context,selectedMembersList,false)
                        holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list).adapter=adapter
                        adapter.setOnClickListener(object:CardMembersListItemsAdapter.OnClickListener{
                            override fun onClick() {
                                if(onClickListener!=null){
                                    onClickListener?.onClick(holder.adapterPosition)
                                }
                            }

                        })

                    }
                }
                else{
                    holder.itemView.findViewById<RecyclerView>(R.id.rv_card_selected_members_list).visibility = View.GONE
                }
            }

            holder.itemView.setOnClickListener {
                if(onClickListener!=null){
                    onClickListener?.onClick(position)
                }
            }
        }
    }

    fun setOnClickListener(onClickListener:OnClickListener){
        this.onClickListener=onClickListener
    }
    interface OnClickListener{
        fun onClick(position:Int)
    }
    class MyViewHolder(view:View):RecyclerView.ViewHolder(view)

}