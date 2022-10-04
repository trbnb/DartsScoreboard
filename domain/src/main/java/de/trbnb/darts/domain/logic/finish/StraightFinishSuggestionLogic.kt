package de.trbnb.darts.domain.logic.finish

import de.trbnb.darts.domain.models.Field
import de.trbnb.darts.domain.models.InOutRule

class StraightFinishSuggestionLogic : AbstractFinishSuggestionLogic() {
    override val priority = (1..20).map { number -> Field.values().first { it.value == number } }
        .reversed()
        .toMutableList()
        .apply { add(Field.BULL) }

    override val outRule: InOutRule
        get() = InOutRule.STRAIGHT
}