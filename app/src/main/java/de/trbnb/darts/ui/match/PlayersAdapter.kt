package de.trbnb.darts.ui.match

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@BindingAdapter("android:players")
fun RecyclerView.setPlayers(playerParticipationViewModels: List<PlayerParticipationViewModel>?) {
}