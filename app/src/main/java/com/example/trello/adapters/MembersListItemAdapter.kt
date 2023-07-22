package com.example.trello.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trello.R
import com.example.trello.models.User //!@Sync: 30698 heap: 121 / 134 FD: 981 [2023-07-22 14:31:35.294]  Received HALT command code 2 [NativeCFMS] BpCustomFrequencyManager::acquire()
import com.example.trello.utils.Constants  //E  win=Window{98dd532 u0 com.example.trello/com.example.trello.activities.TaskListActivity EXITING} destroySurfaces: appStopped=false cleanupOnResume=false win.mWindowRemovalAllowed=true win.mRemoveOnExit=true win.mViewVisibility=0 caller=com.android.server.wm.WindowState.onExitAnimationDone:5969 com.android.server.wm.WindowStateAnimator.onAnimationFinished:225 com.android.server.wm.WindowState.onAnimationFinished:6194 com.android.server.wm.WindowContainer$$ExternalSyntheticLambda4.onAnimationFinished:2 com.android.server.wm.SurfaceAnimator.lambda$getFinishedCallback$0:140 com.android.server.wm.SurfaceAnimator.$r8$lambda$lRxTVOJy8fX752UbrFno9INW9hE:0 com.android.server.wm.SurfaceAnimator$$ExternalSyntheticLambda1.run:8


open class MembersListItemAdapter(
    private val context: Context,
    private var list: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener:OnClickListener?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_members,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.user_place_holder)
                .into(holder.itemView.findViewById(R.id.iv_member_image))

            holder.itemView.findViewById<TextView>(R.id.tv_member_name).text = model.name
            holder.itemView.findViewById<TextView>(R.id.tv_member_email).text = model.email
            if(model.selected){
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_member).visibility = View.VISIBLE
            }
            else{
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_member).visibility = View.GONE
            }
            holder.itemView.setOnClickListener {
                if(onClickListener!=null){
                    if(model.selected){
                        onClickListener!!.onClick(position,model,Constants.UN_SELECT)
                    }
                    else{
                        onClickListener!!.onClick(position,model,Constants.SELECT)
                    }
                }
            }

        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface OnClickListener{
        fun onClick(position:Int,user:User,action:String)
    }

}
