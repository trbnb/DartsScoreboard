package de.trbnb.darts.ui.match

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.trbnb.darts.R
import de.trbnb.darts.databinding.ItemPlayerParticipationBinding
import de.trbnb.mvvmbase.recyclerview.BindingListAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class PlayersAdapter : BindingListAdapter<PlayerParticipationViewModel, ItemPlayerParticipationBinding>(
    R.layout.item_player_participation,
    object : DiffUtil.ItemCallback<PlayerParticipationViewModel>() {
        override fun areItemsTheSame(oldItem: PlayerParticipationViewModel, newItem: PlayerParticipationViewModel) = oldItem === newItem
        override fun areContentsTheSame(oldItem: PlayerParticipationViewModel, newItem: PlayerParticipationViewModel) = oldItem.player == newItem.player
    }
)

@ExperimentalCoroutinesApi
@BindingAdapter("android:players")
fun RecyclerView.setPlayers(playerParticipationViewModels: List<PlayerParticipationViewModel>?) {
    (adapter as? PlayersAdapter ?: PlayersAdapter().also { adapter = it }).submitList(playerParticipationViewModels)
}