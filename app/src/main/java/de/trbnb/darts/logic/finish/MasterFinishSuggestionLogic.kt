package de.trbnb.darts.logic.finish

import de.trbnb.darts.models.PotentialThrow

class MasterFinishSuggestionLogic : FinishSuggestionLogic {
    private val doubleLogic = DoubleFinishSuggestionLogic()
    private val tripleLogic = TripleFinishSuggestionLogic()

    override fun getAll(points: Int, remainingThrows: Int): List<List<PotentialThrow>> {
        return doubleLogic.getAll(points, remainingThrows) + tripleLogic.getAll(points, remainingThrows)
    }
}