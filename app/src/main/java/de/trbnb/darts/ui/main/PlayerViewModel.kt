package de.trbnb.darts.ui.main

import de.trbnb.darts.models.Player
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.bindableproperty.afterSet
import de.trbnb.mvvmbase.bindableproperty.bindableBoolean
import de.trbnb.mvvmbase.bindableproperty.distinct

class PlayerViewModel (
    val player: Player,
    selectedListener: (PlayerViewModel) -> Unit
) : BaseViewModel() {
    var isSelected by bindableBoolean(defaultValue = false)
        .distinct()
        .afterSet { _, _ -> selectedListener(this) }
}