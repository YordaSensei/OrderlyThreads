package com.example.orderlythreads

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderlythreads.Database.Accounts

class AccountsAdapter(
    private var accountsList: List<Accounts>,
    private val onItemClick: (Accounts) -> Unit
) : RecyclerView.Adapter<AccountsAdapter.AccountViewHolder>() {

    inner class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val container: View = itemView.findViewById(R.id.itemContainer)
        val usernameText: TextView = itemView.findViewById(R.id.txtUsername)
        val positionText: TextView = itemView.findViewById(R.id.txtPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.account_item, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accountsList[position]
        holder.usernameText.text = account.username
        holder.positionText.text = account.position

        // Set the click on the entire item
        holder.container.setOnClickListener {
            onItemClick(account)
        }
    }

    override fun getItemCount(): Int = accountsList.size

    // Update the list dynamically
    fun setData(newList: List<Accounts>) {
        accountsList = newList
        notifyDataSetChanged()
    }
}
