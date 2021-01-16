package de.trbnb.darts.ui.main

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.trbnb.darts.R
import de.trbnb.darts.databinding.ItemPlayerBinding
import de.trbnb.mvvmbase.recyclerview.BindingListAdapter

class PlayerAdapter : BindingListAdapter<PlayerViewModel, ItemPlayerBinding>(
    R.layout.item_player,
    object : DiffUtil.ItemCallback<PlayerViewModel>() {
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: PlayerViewModel, newItem: PlayerViewModel) = oldItem === newItem
        override fun areItemsTheSame(oldItem: PlayerViewModel, newItem: PlayerViewModel): Boolean {
            return oldItem.player.id == newItem.player.id
        }
    }
)

@BindingAdapter("players")
fun RecyclerView.setPlayers(players: List<PlayerViewModel>?) {
    (adapter as? PlayerAdapter ?: PlayerAdapter().also { adapter = it }).submitList(players)
}