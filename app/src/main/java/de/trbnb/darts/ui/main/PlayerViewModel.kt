package de.trbnb.darts.ui.main

import de.trbnb.darts.models.Player
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.observableproperty.observable

class PlayerViewModel (
    val player: Player,
    selectedListener: (PlayerViewModel) -> Unit
) : BaseViewModel() {
    var isSelected by observable(defaultValue = false)
        .distinct()
        .afterSet { _, _ -> selectedListener(this) }
}