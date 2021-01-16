package de.trbnb.darts.ui.main.newplayer

import android.os.Bundle
import android.view.WindowInsets
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import de.trbnb.darts.R
import de.trbnb.darts.databinding.DialogNewPlayerBinding
import de.trbnb.darts.di.HiltMvvmBindingBottomSheetFragment
import de.trbnb.darts.ui.events.CloseEvent
import de.trbnb.mvvmbase.events.Event

@AndroidEntryPoint
class NewPlayerDialogFragment : HiltMvvmBindingBottomSheetFragment<NewPlayerViewModel, DialogNewPlayerBinding>(
    R.layout.dialog_new_player
) {
    override fun onCreateDialog(savedInstanceState: Bundle?) = super.onCreateDialog(savedInstanceState).also { dialog ->
        (dialog as? BottomSheetDialog)?.behavior?.isHideable = false
    }

    override fun onBindingCreated(binding: DialogNewPlayerBinding) {
        super.onBindingCreated(binding)

        binding.nameInputEditText.post {
            binding.nameInputEditText.windowInsetsController?.show(WindowInsets.Type.ime())
        }
    }

    override fun onEvent(event: Event) {
        super.onEvent(event)

        when(event) {
            is CloseEvent -> dismiss()
        }
    }
}