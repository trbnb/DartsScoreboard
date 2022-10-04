package de.trbnb.darts.domain.logic

import de.trbnb.darts.domain.models.ThrowNumber

sealed class TurnState {
    object Bust : TurnState()
    object Done : TurnState()
    class Open(val nextThrow: ThrowNumber) : TurnState()
}