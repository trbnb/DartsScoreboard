package de.trbnb.darts.ui.main.newplayer

import androidx.databinding.Bindable
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import de.trbnb.darts.R
import de.trbnb.darts.players.PlayerRepository
import de.trbnb.darts.resources.ResourceProvider
import de.trbnb.darts.ui.events.CloseEvent
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.bindableproperty.bindable
import de.trbnb.mvvmbase.bindableproperty.bindableInt
import de.trbnb.mvvmbase.bindableproperty.distinct
import de.trbnb.mvvmbase.commands.ruleCommand
import de.trbnb.mvvmbase.commands.simpleCommand
import kotlinx.coroutines.launch

class NewPlayerViewModel @ViewModelInject constructor(
    playerRepository: PlayerRepository,
    resourceProvider: ResourceProvider
) : BaseViewModel() {
    @get:Bindable
    var name by bindable("").distinct()

    @get:Bindable
    var color by bindableInt(defaultValue = resourceProvider.getColor(R.color.board_red)).distinct()

    val cancelCommand = simpleCommand { eventChannel(CloseEvent) }

    val createCommand = ruleCommand(
        enabledRule = { name.isNotBlank() },
        action = {
            viewModelScope.launch {
                playerRepository.create(name, color)
                eventChannel(CloseEvent)
            }
        },
        dependentFields = listOf(::name)
    )
}

