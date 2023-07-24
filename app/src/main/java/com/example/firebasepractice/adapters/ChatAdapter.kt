package com.example.firebasepractice.adapters

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasepractice.R
import com.example.firebasepractice.models.MessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ChatAdapter(private val messageModel: ArrayList<MessageModel>, private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var database: FirebaseDatabase
    lateinit var auth: FirebaseAuth
    private val SENDER_VIEW_TYPE = 1
    private val RECEIVER_VIEW_TYPE = 2
    private lateinit var recId: String

    constructor(
        messageModel: ArrayList<MessageModel>,
        context: Context,
        recId: String
    ) : this(messageModel, context) {
        this.recId = recId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == RECEIVER_VIEW_TYPE) {
            val view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent, false)
            ReceiverViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false)
            SenderViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message:MessageModel = messageModel[position]
        database = Firebase.database
        auth = Firebase.auth
        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(context).setTitle("Delete").setMessage("Are you sure you wanna delete this Message ?")
                .setPositiveButton("YES",DialogInterface.OnClickListener{ dialogInterface, i ->
                        var senderRoom: String = auth.uid.toString() + recId
                        database.getReference().child("Users").child(senderRoom)
                            .child(message.messageId).setValue(null)


                }).setNegativeButton("NO",DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                }).show()

            return@setOnLongClickListener false

        }

        if (holder.itemViewType == RECEIVER_VIEW_TYPE) {
            val receiverHolder = holder as ReceiverViewHolder
            if (message != null && message.message.isNotEmpty()) {
                receiverHolder.receiverMsg.text = message.message
            }
            receiverHolder.receiverTime.text = message.time.toString()
        } else {
            val senderHolder = holder as SenderViewHolder
            if (message != null && message.message.isNotEmpty()) {
                senderHolder.senderMsg.text = message.message
            }
            senderHolder.senderTime.text = message.time.toString()
        }
    }

    override fun getItemCount(): Int {
        return messageModel.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageModel[position]

        return if (message.uId == FirebaseAuth.getInstance().currentUser?.uid) {
            SENDER_VIEW_TYPE
        } else {
            RECEIVER_VIEW_TYPE
        }
    }


    class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiverMsg = itemView.findViewById<TextView>(R.id.receiverText)
        val receiverTime = itemView.findViewById<TextView>(R.id.receiverTime)
    }

    class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderMsg = itemView.findViewById<TextView>(R.id.senderText)
        val senderTime = itemView.findViewById<TextView>(R.id.senderTime)
    }
}