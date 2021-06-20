package de.trbnb.darts.ui.main

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.trbnb.darts.R
import de.trbnb.darts.databinding.ItemPlayerBinding

@BindingAdapter("players")
fun RecyclerView.setPlayers(players: List<PlayerViewModel>?) {
}