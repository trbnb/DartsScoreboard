package de.trbnb.darts.domain.logic.finish

import de.trbnb.darts.domain.models.Field
import de.trbnb.darts.domain.models.InOutRule
import de.trbnb.darts.domain.models.PotentialThrow
import de.trbnb.darts.domain.models.points

abstract class AbstractFinishSuggestionLogic : FinishSuggestionLogic {
    protected abstract val priority: List<Field>
    protected abstract val outRule: InOutRule

    private val finishPotentialThrows by lazy {
        priority.flatMap { field ->
            field.multipliers.filter(outRule.multipliers::contains).map { field to it }
        }.map { (field, multiplier) ->
            PotentialThrow(field, multiplier)
        }
    }

    private val otherPotentialThrows by lazy {
        priority.flatMap { field -> field.multipliers.map { PotentialThrow(field, it) } }
    }

    override fun getAll(points: Int, remainingThrows: Int): List<List<PotentialThrow>> {
        return when (remainingThrows) {
            0 -> emptyList()
            else -> mutableListOf<List<PotentialThrow>>()
                .also { calc(null, points, remainingThrows, it) }
        }
    }

    private fun calc(previousThrows: List<PotentialThrow>?, points: Int, remainingThrows: Int, solutions: MutableList<List<PotentialThrow>>) {
        val potentialThrows = when (previousThrows) {
            null -> finishPotentialThrows
            else -> otherPotentialThrows
        }

        potentialThrows.forEach { potentialThrow ->
            val potentialResult = points - potentialThrow.points - (previousThrows?.sumOf { it.points } ?: 0)

            when {
                potentialResult < 0 -> return@forEach
                remainingThrows == 1 && potentialResult != 0 -> return@forEach
                else -> {
                    val list = listOf(previousThrows, potentialThrow)
                    if (potentialResult == 0) {
                        solutions.add(list)
                        return@forEach
                    } else {
                        calc(list, points, remainingThrows - 1, solutions)
                    }
                }
            }
        }
    }

    private fun <T> listOf(list: List<T>?, newItem: T): List<T> = when (list) {
        null -> listOf(newItem)
        else -> mutableListOf(newItem).apply { addAll(list) }
    }
}