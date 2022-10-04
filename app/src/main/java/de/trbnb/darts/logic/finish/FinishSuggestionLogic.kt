package de.trbnb.darts.logic.finish

import de.trbnb.darts.models.InOutRule
import de.trbnb.darts.models.PotentialThrow

interface FinishSuggestionLogic {
    companion object {
        operator fun invoke(outRule: InOutRule): FinishSuggestionLogic = when (outRule) {
            InOutRule.STRAIGHT -> StraightFinishSuggestionLogic()
            InOutRule.DOUBLE -> DoubleFinishSuggestionLogic()
            InOutRule.TRIPLE -> TripleFinishSuggestionLogic()
            InOutRule.MASTER -> MasterFinishSuggestionLogic()
        }
    }

    fun isPossible(points: Int, remainingThrows: Int): Boolean = points <= (remainingThrows * 60)

    fun getAll(points: Int, remainingThrows: Int): List<List<PotentialThrow>>

    fun getOne(points: Int, remainingThrows: Int): List<PotentialThrow>? {
        return getAll(points, remainingThrows).minByOrNull { it.size }
    }
}