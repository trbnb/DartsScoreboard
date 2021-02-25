package de.trbnb.darts.ui.matchsetup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import de.trbnb.darts.R
import de.trbnb.darts.databinding.ActivityMatchSetupBinding
import de.trbnb.darts.di.HiltMvvmActivity
import de.trbnb.darts.di.HiltMvvmBindingActivity
import de.trbnb.darts.di.HiltMvvmBindingBottomSheetFragment
import de.trbnb.darts.ui.events.StartMatchEvent
import de.trbnb.darts.ui.match.MatchActivity
import de.trbnb.mvvmbase.events.Event
import java.util.UUID

@AndroidEntryPoint
class MatchSetupDialogFragment : HiltMvvmBindingBottomSheetFragment<MatchSetupViewModel, ActivityMatchSetupBinding>(R.layout.activity_match_setup) {
    companion object {
        private const val PLAYER_IDS_KEY = "player_ids"

        operator fun invoke(playerIds: List<UUID>) = MatchSetupDialogFragment().apply {
            arguments = Bundle().apply {
                putStringArray(PLAYER_IDS_KEY, playerIds.map(UUID::toString).toTypedArray())
            }
        }
    }

    override fun onBindingCreated(binding: ActivityMatchSetupBinding) {
        super.onBindingCreated(binding)

        binding.legsSlider.setLabelFormatter {
            if (it == 0f) "∞" else it.toString()
        }
    }

    override fun onEvent(event: Event) {
        super.onEvent(event)

        when (event) {
            is StartMatchEvent -> {
                dismiss()
                startActivity(Intent(context, MatchActivity::class.java))
            }
        }
    }

    override fun onViewModelLoaded(viewModel: MatchSetupViewModel) {
        super.onViewModelLoaded(viewModel)
        viewModel.playerIds = arguments?.getStringArray(PLAYER_IDS_KEY)?.map { UUID.fromString(it) } ?: emptyList()
    }
}