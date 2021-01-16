package de.trbnb.darts.ui.match

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.databinding.Bindable
import de.trbnb.darts.R
import de.trbnb.darts.logic.MatchLogic
import de.trbnb.darts.logic.TurnState
import de.trbnb.darts.models.Field
import de.trbnb.darts.models.Multiplier
import de.trbnb.darts.models.buildDescription
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.coroutines.CoroutineViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map

class FieldViewModel(
    val field: Field,
    val multiplier: Multiplier,
    val onSelected: (Field, Multiplier) -> Unit,
    logic: MatchLogic
) : BaseViewModel(), CoroutineViewModel {
    val text = buildDescription(field, multiplier)

    @ExperimentalCoroutinesApi
    @get:Bindable
    val isEnabled by logic.turnState.map { it is TurnState.Open }.toBindable(defaultValue = false)

    @get:ColorRes
    val backgroundColorRes: Int = when {
        field == Field.MISS -> R.color.field_tint_black
        field == Field.BULL && multiplier == Multiplier.SINGLE -> R.color.field_tint_green
        field == Field.BULL && multiplier == Multiplier.DOUBLE -> R.color.field_tint_red
        else -> when (multiplier) {
            Multiplier.SINGLE -> R.color.field_tint_white
            Multiplier.DOUBLE -> R.color.field_tint_green
            Multiplier.TRIPLE -> R.color.field_tint_red
        }
    }

    @get:ColorInt
    val textColorRes: Int = when (backgroundColorRes) {
        R.color.field_tint_white -> R.color.field_text_white
        else -> R.color.field_text_other
    }

    fun onSelected() = onSelected(field, multiplier)

    val isSpecialField = when (field) {
        Field.MISS, Field.BULL -> true
        else -> false
    }
}