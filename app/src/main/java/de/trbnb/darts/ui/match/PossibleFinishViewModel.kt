package de.trbnb.darts.ui.match

import de.trbnb.darts.models.PotentialThrow
import de.trbnb.darts.models.description
import de.trbnb.mvvmbase.BaseViewModel

class PossibleFinishViewModel(val finish: List<PotentialThrow>) : BaseViewModel() {
    val text = finish.joinToString(separator = "\n", transform = PotentialThrow::description)
}