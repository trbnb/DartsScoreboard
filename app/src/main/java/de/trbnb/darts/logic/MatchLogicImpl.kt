package de.trbnb.darts.logic

import de.trbnb.darts.logic.finish.FinishSuggestionLogic
import de.trbnb.darts.models.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class MatchLogicImpl(override val match: Match) : MatchLogic {
    private var currentPlayerIndex = 0
        set(value) {
            field = if (value > match.players.lastIndex) 0 else value

            currentPlayer.value = playerOrder.value[field]
        }

    private var currentSet = 0
    private var currentLeg = 0

    override val finishSuggestionLogic = FinishSuggestionLogic(match.matchOptions.outRule)

    override val playerOrder = MutableStateFlow(match.players.let { players ->
        when (match.matchOptions.playerStartOrder) {
            PlayerStartOrder.AS_GIVEN -> players
            PlayerStartOrder.SHUFFLE -> players.shuffled()
        }
    })
    override val currentPlayer = MutableStateFlow(playerOrder.value[currentPlayerIndex])

    override val gameEnded = CompletableDeferred<Unit>()

    private fun nextPlayer() {
        currentPlayerIndex++

        newTurn()
    }

    private fun newTurn() {
        turn.value = Turn()
    }

    override fun currentParticipation(player: Player): Pair<SetParticipation, LegParticipation> {
        return match[player][currentSet].let { it to it[currentLeg] }
    }

    private fun scoredPoints(player: Player): Int {
        val (_, leg) = currentParticipation(player)

        return leg.turns
            .filterNot { turn -> ThrowState.BUST in arrayOf(turn.first, turn.second, turn.third).map { it?.state } }
            .sumOf { it.value }
    }

    override fun remainingPoints(player: Player) = match.matchOptions.points - scoredPoints(player)

    override val turn = MutableStateFlow(Turn())
    override val remainingPoints: Flow<Int> = turn.map { remainingPoints() }
    override val turnState: Flow<TurnState> = turn.map { turn ->
        when {
            turn.first?.state == ThrowState.BUST -> TurnState.Bust
            turn.second?.state == ThrowState.BUST -> TurnState.Bust
            turn.third?.state == ThrowState.BUST -> TurnState.Bust
            remainingPoints(currentPlayer.value) - turn.value == 0 -> TurnState.Done
            turn.first == null -> TurnState.Open(ThrowNumber.ONE)
            turn.second == null -> TurnState.Open(ThrowNumber.TWO)
            turn.third == null -> TurnState.Open(ThrowNumber.THREE)
            else -> TurnState.Done
        }
    }

    override val suggestedFinish: Flow<List<PotentialThrow>?> = turn.map { turn ->
        val remainingThrows = listOf(turn.first, turn.second, turn.third).count { it == null }
        finishSuggestionLogic.getOne(remainingPoints(), remainingThrows.takeUnless { it == 0 } ?: 3)
    }.flowOn(Dispatchers.IO)

    override val suggestedFinishes: Flow<List<List<PotentialThrow>>> = turn.map { turn ->
        val remainingThrows = listOf(turn.first, turn.second, turn.third).count { it == null }
        finishSuggestionLogic.getAll(remainingPoints(), remainingThrows.takeUnless { it == 0 } ?: 3)
            .sortedBy { it.size }
    }

    override fun addThrow(_throw: Throw) {
        val oldTurn = turn.value

        turn.value = when {
            oldTurn.first == null -> oldTurn.copy(first = _throw)
            oldTurn.second == null -> oldTurn.copy(second = _throw)
            oldTurn.third == null -> oldTurn.copy(third = _throw)
            else -> return
        }.let(::reevaluate)
    }

    override fun fellOff(throwNumber: ThrowNumber, fellOff: Boolean) {
        val turn = turn.value

        val throwState = if (fellOff) ThrowState.FALLEN_OFF else ThrowState.OK

        this.turn.value = turn.copy(
            first = turn.first.takeUnless { throwNumber == ThrowNumber.ONE } ?: turn.first?.copy(state = throwState),
            second = turn.second.takeUnless { throwNumber == ThrowNumber.TWO } ?: turn.second?.copy(state = throwState),
            third = turn.third.takeUnless { throwNumber == ThrowNumber.THREE } ?: turn.third?.copy(state = throwState)
        ).let(::reevaluate)
    }

    override fun undo() {
        val turn = turn.value
        remove(when {
            turn.third != null -> ThrowNumber.THREE
            turn.second != null -> ThrowNumber.TWO
            turn.first != null -> ThrowNumber.ONE
            else -> return
        })
    }

    override fun remove(throwNumber: ThrowNumber) {
        turn.value = when (throwNumber) {
            ThrowNumber.ONE -> turn.value.copy(first = null)
            ThrowNumber.TWO -> turn.value.copy(second = null)
            ThrowNumber.THREE -> turn.value.copy(third = null)
        }.let(::reevaluate)
    }

    override fun confirmTurn() {
        val currentPlayer = currentPlayer.value
        val participation = match[currentPlayer]
        val set = participation[currentSet]
        val leg = set[currentLeg]
        leg.addTurn(turn.value)

        val legWon = if (remainingPoints(currentPlayer) == 0) {
            match.participations
                .forEach { it[currentSet][currentLeg].result = if (it.player === currentPlayer) ParticipationResult.WON else ParticipationResult.LOST }
            true
        } else false

        val setWon = if (set.wonLegs == match.matchOptions.legs) {
            match.participations
                .forEach { it[currentSet].result = if (it.player === currentPlayer) ParticipationResult.WON else ParticipationResult.LOST }
            true
        } else false

        val matchWon = if (participation.wonSets == match.matchOptions.sets) {
            match.participations
                .forEach { it.result = if (it.player === currentPlayer) ParticipationResult.WON else ParticipationResult.LOST }
            true
        } else false

        if (legWon) {
            playerOrder.value = when (match.matchOptions.playerOrder) {
                PlayerOrder.SHUFFLE -> playerOrder.value.shuffled()
                PlayerOrder.WORST_STARTS -> playerOrder.value.sortedByDescending(::remainingPoints)
            }

            currentPlayerIndex = 0
        }

        when {
            matchWon -> {
                gameEnded.complete(Unit)
            }
            //new set
            setWon -> {
                match.participations.forEach { it.addSet() }
                currentSet++
                currentLeg = 0
                newTurn()
            }
            //new leg
            legWon -> {
                match.participations.forEach { it[currentSet].addLeg() }
                currentLeg++
                newTurn()
            }
            else -> nextPlayer()
        }

    }

    private fun remainingPoints() = remainingPoints(currentPlayer.value) - turn.value.value

    private fun reevaluate(turn: Turn): Turn {
        val throws = listOf(turn.first, turn.second, turn.third)

        var scoredPoints = 0
        var wasBust = false
        val previouslyScoredPoints = scoredPoints(currentPlayer.value)
        val previouslyRemainingPoints = remainingPoints(currentPlayer.value)

        val (newFirst, newSecond, newThird) = throws.map { _throw ->
            if (_throw == null) return@map null
            if (_throw.state == ThrowState.FALLEN_OFF) return@map _throw
            if (wasBust) return@map _throw.copy(state = ThrowState.PREVIOUSLY_BUST)

            if (scoredPoints == 0 && previouslyScoredPoints == 0) {
                if (_throw.multiplier !in match.matchOptions.inRule.multipliers) {
                    return@map _throw.copy(state = ThrowState.WRONG_IN)
                }
            }

            val throwValue = _throw.field.value * _throw.multiplier.value
            scoredPoints += throwValue

            val remainingPoints = previouslyRemainingPoints - scoredPoints

            val newThrow = _throw.copy(state = when {
                remainingPoints == 0 -> when (_throw.multiplier) {
                    !in match.matchOptions.outRule.multipliers -> ThrowState.BUST
                    else -> ThrowState.OK
                }
                remainingPoints < 0 -> ThrowState.BUST
                else -> when (match.matchOptions.outRule) {
                    InOutRule.MASTER, InOutRule.DOUBLE -> if (remainingPoints < 2) ThrowState.BUST else ThrowState.OK
                    InOutRule.TRIPLE -> if (remainingPoints < 3) ThrowState.BUST else ThrowState.OK
                    else -> ThrowState.OK
                }
            })

            if (newThrow.state == ThrowState.BUST) {
                wasBust = true
            }

            return@map newThrow
        }

        return Turn(newFirst, newSecond, newThird)
    }
}