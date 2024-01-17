package com.krystian.chessclock.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krystian.chessclock.model.CustomMatch
import com.krystianrymonlipinski.chessclock.R
import com.krystianrymonlipinski.chessclock.databinding.ListItemCustomMatchBinding

class CustomMatchAdapter(
    private var matches: List<CustomMatch> = emptyList(),
    private val callback: Callback

) : RecyclerView.Adapter<CustomMatchAdapter.CustomMatchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomMatchViewHolder {
        val binding = ListItemCustomMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomMatchViewHolder(binding).apply {
            binding.root.setOnClickListener { callback.onMatchClicked(matches[absoluteAdapterPosition].id) }
            binding.root.setOnLongClickListener {
                callback.onMatchLongClicked(matches[absoluteAdapterPosition])
                true
            }
        }
    }

    override fun onBindViewHolder(holder: CustomMatchViewHolder, position: Int) {
        holder.bind(matches[position])
    }

    override fun getItemCount(): Int {
        return matches.size
    }

    fun setData(matches: List<CustomMatch>) {
        this.matches = matches
    }


    interface Callback {
        fun onMatchClicked(matchId: Long)
        fun onMatchLongClicked(match: CustomMatch)
    }



    inner class CustomMatchViewHolder(
        private val _binding: ListItemCustomMatchBinding
    ) : RecyclerView.ViewHolder(_binding.root) {

        fun bind(item: CustomMatch) {
            _binding.apply {
                customMatchName.text = item.name
                customMatchGames.text = String.format(
                    itemView.context.getString(R.string.number_of_games), item.games.size)
            }
        }
    }

}