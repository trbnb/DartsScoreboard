package de.trbnb.darts.ui.main

import de.trbnb.mvvmbase.events.Event
import java.util.UUID

sealed class MainEvent : Event {
    class ConfigureMatch(val playerIds: List<UUID>) : MainEvent()
}