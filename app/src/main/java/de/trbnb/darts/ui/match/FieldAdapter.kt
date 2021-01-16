package de.trbnb.darts.ui.match

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.trbnb.darts.R
import de.trbnb.darts.databinding.ItemFieldBinding
import de.trbnb.mvvmbase.recyclerview.BindingListAdapter

class FieldAdapter : BindingListAdapter<FieldViewModel, ItemFieldBinding>(
    R.layout.item_field,
    object : DiffUtil.ItemCallback<FieldViewModel>() {
        override fun areItemsTheSame(oldItem: FieldViewModel, newItem: FieldViewModel) = oldItem === newItem
        override fun areContentsTheSame(oldItem: FieldViewModel, newItem: FieldViewModel): Boolean {
            return oldItem.field == newItem.field && oldItem.multiplier == newItem.multiplier
        }
    }
)

@BindingAdapter("android:fields")
fun RecyclerView.setFields(fields: List<FieldViewModel>) {
    (adapter as? FieldAdapter ?: FieldAdapter().also { adapter = it }).submitList(fields)
}