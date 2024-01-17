package com.krystian.chessclock.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krystian.chessclock.model.CustomGame
import com.krystianrymonlipinski.chessclock.R
import com.krystianrymonlipinski.chessclock.databinding.ListItemCustomGameBinding

class CustomGameAdapter(
    private var games: List<CustomGame> = emptyList(),
    private val callback: Callback
) : RecyclerView.Adapter<CustomGameAdapter.CustomGameViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomGameViewHolder {
        val binding = ListItemCustomGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomGameViewHolder(binding).apply {
            binding.root.setOnClickListener { callback.onGameClicked(games[absoluteAdapterPosition].id) }
        }
    }

    override fun onBindViewHolder(holder: CustomGameViewHolder, position: Int) {
        holder.bind(games[position])
    }

    override fun getItemCount(): Int {
        return games.size
    }


    fun setData(games: List<CustomGame>) {
        this.games = games
    }

    interface Callback {
        fun onGameClicked(gameId: Long)
    }


    inner class CustomGameViewHolder(
        private val _binding: ListItemCustomGameBinding
    ) : RecyclerView.ViewHolder(_binding.root) {

        fun bind(item: CustomGame) {
            _binding.apply {
                customGameNumber.text = StringBuilder().append(absoluteAdapterPosition + 1).toString()
                customGame.text = String.format(
                    itemView.context.getString(R.string.custom_game),
                    item.whiteTime,
                    item.whiteIncrement,
                    item.blackTime,
                    item.blackIncrement
                )
            }
        }
    }
}