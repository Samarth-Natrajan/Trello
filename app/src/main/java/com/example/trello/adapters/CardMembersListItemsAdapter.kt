package com.example.trello.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trello.R
import com.example.trello.models.SelectedMembers


open class CardMembersListItemsAdapter(private val context: Context,
private val list:ArrayList<SelectedMembers>,private val assignMembers:Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private var onClickListener: OnClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card_selected_member,parent,false))

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if(position==list.size-1&&assignMembers){
                holder.itemView.findViewById<ImageView>(R.id.iv_add_member).visibility = View.VISIBLE
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_member_image).visibility = View.GONE
            }
            else{
                holder.itemView.findViewById<ImageView>(R.id.iv_add_member).visibility = View.GONE
                val sel_mem = holder.itemView.findViewById<ImageView>(R.id.iv_selected_member_image)
                sel_mem?.visibility = View.VISIBLE
                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.user_place_holder).into(sel_mem)

                holder.itemView.setOnClickListener{
                    if(onClickListener!=null){
                        onClickListener?.onClick()
                    }
                }
            }
        }
    }

    fun setOnClickListener(onClickListener:OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick()
    }
    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}