package de.trbnb.darts.ui.main.newplayer

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.trbnb.darts.players.PlayerRepository
import de.trbnb.darts.resources.ResourceProvider
import de.trbnb.darts.ui.events.CloseEvent
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.bindableproperty.bindable
import de.trbnb.mvvmbase.bindableproperty.bindableULong
import de.trbnb.mvvmbase.bindableproperty.distinct
import de.trbnb.mvvmbase.commands.ruleCommand
import de.trbnb.mvvmbase.commands.simpleCommand
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewPlayerViewModel @Inject constructor(
    playerRepository: PlayerRepository,
    resourceProvider: ResourceProvider
) : BaseViewModel() {
    var name by bindable("").distinct()

    var color by bindableULong(defaultValue = 0UL).distinct()

    val cancelCommand = simpleCommand { eventChannel(CloseEvent) }

    val createCommand = ruleCommand(
        enabledRule = { name.isNotBlank() },
        action = {
            viewModelScope.launch {
                playerRepository.create(name, color.toInt())
                eventChannel(CloseEvent)
            }
        },
        dependencyProperties = listOf(::name)
    )
}

