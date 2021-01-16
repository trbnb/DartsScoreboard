package de.trbnb.darts.ui.main

import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import de.trbnb.darts.models.Player
import de.trbnb.darts.players.PlayerRepository
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.bindableproperty.afterSet
import de.trbnb.mvvmbase.bindableproperty.bindableBoolean
import de.trbnb.mvvmbase.bindableproperty.distinct
import de.trbnb.mvvmbase.commands.simpleCommand
import kotlinx.coroutines.launch

class PlayerViewModel @AssistedInject constructor(
    @Assisted val player: Player,
    @Assisted selectedListener: (PlayerViewModel) -> Unit,
    playerRepository: PlayerRepository
) : BaseViewModel() {
    @get:Bindable
    var isSelected by bindableBoolean(defaultValue = false)
        .distinct()
        .afterSet { _, _ -> selectedListener(this) }

    val deleteCommand = simpleCommand {
        viewModelScope.launch {
            playerRepository.delete(player)
        }
    }

    @AssistedInject.Factory
    interface Factory {
        operator fun invoke(player: Player, selectedListener: (PlayerViewModel) -> Unit): PlayerViewModel
    }
}