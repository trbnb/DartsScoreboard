package de.trbnb.darts.ui.match

import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import de.trbnb.darts.R
import de.trbnb.darts.databinding.ActivityMatchBinding
import de.trbnb.darts.di.HiltMvvmBindingActivity

@Suppress("EXPERIMENTAL_API_USAGE")
@AndroidEntryPoint
class MatchActivity : HiltMvvmBindingActivity<MatchViewModel, ActivityMatchBinding>(R.layout.activity_match) {
    override fun onPostResume() {
        super.onPostResume()

        binding.playerList.post {
            val isScrollable = binding.playerList.isScrollable()
            viewModel.isPlayerListScrollable = isScrollable
            binding.playerList.isVerticalFadingEdgeEnabled = isScrollable
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
