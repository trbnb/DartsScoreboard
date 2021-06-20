package de.trbnb.darts.compose

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import de.trbnb.mvvmbase.commands.Command

@BindingAdapter("android:visible")
fun View.setVisible(visible: Boolean) { isVisible = visible }

/**
 * Binds the given [Command] as command that will be invoked when the View has been clicked.
 * This will also bind the [View.isEnabled] property to the [Command.isEnabled] property.
 */
@BindingAdapter("clickCommand")
fun View.bindClickCommand(command: Command<Unit, *>?) {
    if (command == null) {
        setOnClickListener(null)
        return
    }
    bindEnabled(command)

    setOnClickListener {
        command.invokeSafely(Unit)
    }
}

/**
 * Binds the [View.isEnabled] property to the [Command.isEnabled] property of the given instances.
 */
fun View.bindEnabled(command: Command<*, *>?) {
    command ?: return
    isEnabled = command.isEnabled
}