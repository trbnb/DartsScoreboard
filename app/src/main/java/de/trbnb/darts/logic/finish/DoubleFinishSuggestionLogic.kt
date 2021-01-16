package de.trbnb.darts.logic.finish

import de.trbnb.darts.models.Field
import de.trbnb.darts.models.InOutRule

class DoubleFinishSuggestionLogic : AbstractFinishSuggestionLogic() {
    override val priority = listOf(
        Field.SIXTEEN,
        Field.TWENTY,
        Field.EIGHTEEN,
        Field.SEVENTEEN,
        Field.BULL,
        Field.NINETEEN,
        Field.EIGHT,
        Field.TWELVE,
        Field.TEN,
        Field.NINE,
        Field.FOURTEEN,
        Field.THIRTEEN,
        Field.ELEVEN,
        Field.FIFTEEN,
        Field.FOUR,
        Field.SIX,
        Field.FIVE,
        Field.SEVEN,
        Field.TWO,
        Field.THREE,
        Field.ONE
    )

    override val outRule: InOutRule
        get() = InOutRule.DOUBLE
}