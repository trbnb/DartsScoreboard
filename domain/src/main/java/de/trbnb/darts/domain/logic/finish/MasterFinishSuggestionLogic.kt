package de.trbnb.darts.domain.logic.finish

import de.trbnb.darts.domain.models.PotentialThrow

class MasterFinishSuggestionLogic : FinishSuggestionLogic {
    private val doubleLogic = DoubleFinishSuggestionLogic()
    private val tripleLogic = TripleFinishSuggestionLogic()

    override fun getAll(points: Int, remainingThrows: Int): List<List<PotentialThrow>> {
        return doubleLogic.getAll(points, remainingThrows) + tripleLogic.getAll(points, remainingThrows)
    }
}