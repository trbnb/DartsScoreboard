package de.trbnb.darts.logic

import de.trbnb.darts.models.ThrowNumber

sealed class TurnState {
    object Bust : TurnState()
    object Done : TurnState()
    class Open(val nextThrow: ThrowNumber) : TurnState()
}