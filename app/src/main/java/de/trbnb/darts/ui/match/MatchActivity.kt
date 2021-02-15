package de.trbnb.darts.ui.match

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import de.trbnb.darts.BR
import de.trbnb.darts.R
import de.trbnb.darts.databinding.ActivityMatchBinding
import de.trbnb.darts.di.HiltMvvmBindingActivity
import de.trbnb.darts.ui.events.CloseEvent
import de.trbnb.mvvmbase.events.Event

@Suppress("EXPERIMENTAL_API_USAGE")
@AndroidEntryPoint
class MatchActivity : HiltMvvmBindingActivity<MatchViewModel, ActivityMatchBinding>(R.layout.activity_match) {
    override fun onPostResume() {
        super.onPostResume()

        binding.playerList.post {
            val isScrollable = binding.playerList.isScrollable()
            viewModel.isPlayerListScrollable = isScrollable
            binding.playerList.overScrollMode = if (isScrollable) View.OVER_SCROLL_ALWAYS else View.OVER_SCROLL_NEVER
        }
    }

    override fun onViewModelPropertyChanged(viewModel: MatchViewModel, fieldId: Int) {
        super.onViewModelPropertyChanged(viewModel, fieldId)

        when (fieldId) {
            BR.currentPlayerIndex -> binding.playerList.smoothScrollToPosition(viewModel.currentPlayerIndex)
        }
    }

    override fun onEvent(event: Event) {
        super.onEvent(event)

        when (event) {
            is CloseEvent -> finish()
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Beenden?")
            .setPositiveButton(android.R.string.ok) { d, _ -> d.dismiss(); finish() }
            .setNegativeButton(android.R.string.cancel) { d, _ -> d.dismiss() }
            .show()
    }

    private fun RecyclerView.isScrollable(): Boolean {
        return computeHorizontalScrollRange() > width || computeVerticalScrollRange() > height
    }
}
