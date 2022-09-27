package de.trbnb.darts.logic

import de.trbnb.darts.logic.finish.FinishSuggestionLogic
import de.trbnb.darts.models.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

class MatchLogicImpl(
    override val match: Match,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : MatchLogic, CoroutineScope by coroutineScope {
    private var currentSet = 0
    private var currentLeg = 0

    override val finishSuggestionLogic = FinishSuggestionLogic(match.matchOptions.outRule)

    private val _state = run<MutableStateFlow<StateImpl>> {
        val playerOrder = match.players.let { players ->
            when (match.matchOptions.playerStartOrder) {
                PlayerStartOrder.AS_GIVEN -> players
                PlayerStartOrder.SHUFFLE -> players.shuffled()
            }
        }
        val turn = Turn()
        MutableStateFlow(
            StateImpl(
                match = match,
                currentPlayerIndex = 0,
                playerOrder = playerOrder,
                currentTurn = turn,
                suggestedFinishes = null
            )
        )
    }
    override val state: StateFlow<MatchLogic.State> = _state.asStateFlow()

    private var currentPlayerIndex: Int
        get() = _state.value.currentPlayerIndex
        set(value) {
            _state.value = _state.value.copy(
                currentPlayerIndex = if (value > match.players.lastIndex) 0 else value
            )
        }

    private var suggestionCalcJob: Job? = null

    override val gameEnded = CompletableDeferred<Unit>()

    private val previousTurns: Deque<Turn> = ArrayDeque()

    init {
        _state.distinctUntilChanged { old, new -> old.currentTurn == new.currentTurn }
            .onEach { calcSuggestedFinish() }
            .launchIn(this)
    }

    private fun nextPlayer() {
        currentPlayerIndex++

        newTurn()
    }

    private fun newTurn() {
        _state.value = _state.value.copy(
            currentTurn = previousTurns.poll() ?: Turn()
        )
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

    private fun Turn.turnState(currentPlayer: Player): TurnState {
        return when {
            first?.state == ThrowState.BUST -> TurnState.Bust
            second?.state == ThrowState.BUST -> TurnState.Bust
            third?.state == ThrowState.BUST -> TurnState.Bust
            remainingPoints(currentPlayer) - value == 0 -> TurnState.Done
            first == null -> TurnState.Open(ThrowNumber.ONE)
            second == null -> TurnState.Open(ThrowNumber.TWO)
            third == null -> TurnState.Open(ThrowNumber.THREE)
            else -> TurnState.Done
        }
    }

    private fun calcSuggestedFinish() {
        suggestionCalcJob?.cancel()
        suggestionCalcJob = launch {
            val turn = _state.value.currentTurn
            val remainingThrows = listOf(turn.first, turn.second, turn.third).count { it == null }

            if (!isActive) {
                return@launch
            }

            val suggestedFinishes = finishSuggestionLogic.getAll(
                remainingPoints(),
                remainingThrows.takeUnless { it == 0 } ?: 3
            ).sortedBy { it.size }

            if (!isActive) {
                return@launch
            }

            _state.value = _state.value.copy(suggestedFinishes = suggestedFinishes)
        }
    }

    override fun addThrow(_throw: Throw) {
        val oldTurn = _state.value.currentTurn

        _state.value = _state.value.copy(
            currentTurn = when {
                oldTurn.first == null -> oldTurn.copy(first = _throw)
                oldTurn.second == null -> oldTurn.copy(second = _throw)
                oldTurn.third == null -> oldTurn.copy(third = _throw)
                else -> return
            }.let(::reevaluate)
        )
    }

    override fun fellOff(throwNumber: ThrowNumber, fellOff: Boolean) {
        val turn = _state.value.currentTurn

        val throwState = if (fellOff) ThrowState.FALLEN_OFF else ThrowState.OK

        _state.value = _state.value.copy(
            currentTurn = turn.copy(
                first = turn.first.takeUnless { throwNumber == ThrowNumber.ONE } ?: turn.first?.copy(state = throwState),
                second = turn.second.takeUnless { throwNumber == ThrowNumber.TWO } ?: turn.second?.copy(state = throwState),
                third = turn.third.takeUnless { throwNumber == ThrowNumber.THREE } ?: turn.third?.copy(state = throwState)
            ).let(::reevaluate)
        )
    }

    override fun undoThrow() {
        val turn = _state.value.currentTurn
        removeThrow(when {
            turn.third != null -> ThrowNumber.THREE
            turn.second != null -> ThrowNumber.TWO
            turn.first != null -> ThrowNumber.ONE
            else -> return
        })
    }

    override fun removeThrow(throwNumber: ThrowNumber) {
        val currentTurn = _state.value.currentTurn
        _state.value = _state.value.copy(
            currentTurn = when (throwNumber) {
                ThrowNumber.ONE -> currentTurn.copy(first = null)
                ThrowNumber.TWO -> currentTurn.copy(second = null)
                ThrowNumber.THREE -> currentTurn.copy(third = null)
            }.let(::reevaluate)
        )
    }

    override fun confirmTurn() {
        val currentPlayer = _state.value.currentPlayer
        val participation = match[currentPlayer]
        val set = participation[currentSet]
        val leg = set[currentLeg]
        leg.addTurn(_state.value.currentTurn)

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
            val playerOrder = _state.value.playerOrder
            _state.value = _state.value.copy(
                playerOrder = when (match.matchOptions.playerOrder) {
                    PlayerOrder.SHUFFLE -> playerOrder.shuffled()
                    PlayerOrder.WORST_STARTS -> playerOrder.sortedByDescending(::remainingPoints)
                }
            )

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

    private fun remainingPoints(): Int {
        return remainingPoints(_state.value.currentPlayer) - _state.value.currentTurn.value
    }

    private fun reevaluate(turn: Turn): Turn {
        val throws = listOf(turn.first, turn.second, turn.third)
        val currentPlayer = _state.value.currentPlayer

        var scoredPoints = 0
        var wasBust = false
        val previouslyScoredPoints = scoredPoints(currentPlayer)
        val previouslyRemainingPoints = remainingPoints(currentPlayer)

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

    override fun canUndoTurnConfirmation(): Boolean {
        val previousPlayerIndex = (currentPlayerIndex - 1).takeUnless { it < 0 } ?: match.players.lastIndex
        val previousPlayer = _state.value.playerOrder[previousPlayerIndex]

        val (_, leg) = currentParticipation(previousPlayer)

        return leg.turns.isNotEmpty()
    }

    override fun undoTurnConfirmation() {
        if (!canUndoTurnConfirmation()) return

        val previousPlayerIndex = (currentPlayerIndex - 1).takeUnless { it < 0 } ?: match.players.lastIndex

        val (_, leg) = currentParticipation(_state.value.playerOrder[previousPlayerIndex])

        val lastTurn = leg.removeLast() ?: return

        val currentTurn = _state.value.currentTurn

        if (currentTurn.first != null && currentTurn.second != null && currentTurn.third != null) {
            previousTurns.push(currentTurn)
        }

        currentPlayerIndex = previousPlayerIndex
        _state.value = _state.value.copy(currentTurn = lastTurn)
    }

    private inner class StateImpl(
        override val match: Match,
        val currentPlayerIndex: Int,
        override val playerOrder: List<Player>,
        override val currentTurn: Turn,
        override val suggestedFinishes: List<List<PotentialThrow>>?
    ) : MatchLogic.State {
        override val turnState: TurnState = currentTurn.turnState(currentPlayer)

        override val currentPlayer: Player
            get() = playerOrder[currentPlayerIndex]

        override val remainingPoints: Int
            get() = remainingPoints()

        override val currentParticipationStats = playerOrder.map { player ->
            val (set, leg) = currentParticipation(player)
            CurrentParticipationStatsImpl(player, match[player], set, leg, remainingPoints(player))
        }

        override val canUndoTurnConfirmation: Boolean = canUndoTurnConfirmation()

        fun copy(
            currentPlayerIndex: Int = this.currentPlayerIndex,
            playerOrder: List<Player> = this.playerOrder,
            currentTurn: Turn = this.currentTurn,
            suggestedFinishes: List<List<PotentialThrow>>? = this.suggestedFinishes
        ): StateImpl = StateImpl(
            match = match,
            currentPlayerIndex = currentPlayerIndex,
            playerOrder = playerOrder,
            currentTurn = currentTurn,
            suggestedFinishes = suggestedFinishes?.takeUnless {
                this.currentTurn != currentTurn && this.suggestedFinishes === suggestedFinishes
            }
        )
    }

    class CurrentParticipationStatsImpl(
        override val player: Player,
        override val matchParticipation: MatchParticipation,
        override val currentSet: SetParticipation,
        override val currentLeg: LegParticipation,
        override val remainingPoints: Int
    ) : MatchLogic.CurrentParticipationStats
}