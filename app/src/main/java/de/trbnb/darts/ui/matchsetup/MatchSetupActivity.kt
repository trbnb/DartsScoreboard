package de.trbnb.darts.ui.matchsetup

import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import de.trbnb.darts.R
import de.trbnb.darts.di.HiltMvvmActivity
import de.trbnb.darts.ui.events.StartMatchEvent
import de.trbnb.darts.ui.match.MatchActivity
import de.trbnb.mvvmbase.events.Event
import java.util.UUID

@AndroidEntryPoint
class MatchSetupActivity : HiltMvvmActivity<MatchSetupViewModel>(R.layout.activity_match_setup) {
    companion object {
        private const val PLAYER_IDS_KEY = "player_ids"

        fun newIntent(context: Context, playerIds: List<UUID>): Intent = Intent(context, MatchSetupActivity::class.java).apply {
            putExtra(PLAYER_IDS_KEY, playerIds.map(UUID::toString).toTypedArray())
        }
    }

    override fun onEvent(event: Event) {
        super.onEvent(event)

        when (event) {
            is StartMatchEvent -> {
                finish()
                startActivity(Intent(this, MatchActivity::class.java))
            }
        }
    }

    override fun onViewModelLoaded(viewModel: MatchSetupViewModel) {
        super.onViewModelLoaded(viewModel)
        viewModel.playerIds = intent.getStringArrayExtra(PLAYER_IDS_KEY)?.map { UUID.fromString(it) } ?: emptyList()
    }
}