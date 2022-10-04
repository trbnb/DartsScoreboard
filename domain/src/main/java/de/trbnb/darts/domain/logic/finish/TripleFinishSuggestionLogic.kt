package de.trbnb.darts.domain.logic.finish

import de.trbnb.darts.domain.models.Field
import de.trbnb.darts.domain.models.InOutRule

class TripleFinishSuggestionLogic : AbstractFinishSuggestionLogic() {
    override val priority = listOf(
        Field.EIGHTEEN,
        Field.NINETEEN,
        Field.TWENTY,
        Field.FIFTEEN,
        Field.FOURTEEN,
        Field.TWELVE,
        Field.SIXTEEN,
        Field.THIRTEEN,
        Field.SEVENTEEN,
        Field.NINE,
        Field.TEN,
        Field.EIGHT,
        Field.ELEVEN,
        Field.SIX,
        Field.FIVE,
        Field.SEVEN,
        Field.THREE,
        Field.FOUR,
        Field.TWO,
        Field.ONE,
        Field.BULL
    )

    override val outRule: InOutRule
        get() = InOutRule.TRIPLE
}