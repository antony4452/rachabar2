package com.example.rachabar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rachabar.data.Main
import com.example.rachabar.databinding.ItemMainBinding

class MainAdapter(private var mains: List<Main>) :
    RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    class MainViewHolder(private val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(main: Main) {
            binding.textViewDate.text = main.date
            binding.textViewTotalAmount.text = "Total: R$ %.2f".format(main.totalAmount)
            binding.textViewFriendsCount.text = "Amigos: ${main.numberOfFriends}"
            binding.textViewAmountPerPerson.text = "Por pessoa: R$ %.2f".format(main.amountPerPerson)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val binding = ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(mains[position])
    }

    override fun getItemCount() = mains.size

    fun updateMains(newMains: List<Main>) {
        mains = newMains
        notifyDataSetChanged()
    }
}