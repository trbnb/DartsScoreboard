package de.trbnb.darts.ui.match

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.trbnb.darts.R
import de.trbnb.darts.databinding.ItemPossibleFinishBinding
import de.trbnb.mvvmbase.recyclerview.BindingListAdapter

class PossibleFinishesAdapter : BindingListAdapter<PossibleFinishViewModel, ItemPossibleFinishBinding>(
    R.layout.item_possible_finish,
    object : DiffUtil.ItemCallback<PossibleFinishViewModel>() {
        override fun areContentsTheSame(oldItem: PossibleFinishViewModel, newItem: PossibleFinishViewModel): Boolean {
            return oldItem.text == newItem.text
        }

        override fun areItemsTheSame(oldItem: PossibleFinishViewModel, newItem: PossibleFinishViewModel): Boolean {
            return oldItem === newItem
        }
    }
)

@BindingAdapter("items")
fun RecyclerView.setItems(items: List<PossibleFinishViewModel>) {
    (adapter as? PossibleFinishesAdapter ?: PossibleFinishesAdapter().also { adapter = it })
        .submitList(items)
}