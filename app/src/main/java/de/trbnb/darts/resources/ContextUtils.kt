package de.trbnb.darts.resources

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

/**
 * Utility function to get a color from the app resources with backwards compatibility.
 */
@ColorInt
fun Context.getColorCompat(@ColorRes colorId: Int) = ContextCompat.getColor(this, colorId)

/**
 * Utility function to get a [Drawable] from the app resources with backwards compatibility.
 */
fun Context.getDrawableCompat(@DrawableRes drawableId: Int): Drawable? = ContextCompat.getDrawable(this, drawableId)

/**
 * Utility function to tint a drawable with the compat library.
 */
fun Drawable.setTintCompat(@ColorInt color: Int): Drawable = DrawableCompat.wrap(this).apply {
    DrawableCompat.setTint(this, color)
}

/**
 * Resolves an attribute id to a [TypedValue].
 * References will also be resolved so the TypedValue will not contain another attribute id.
 */
fun Context.resolveAttribute(@AttrRes attrId: Int, resolveReferences: Boolean = true) = TypedValue().apply {
    theme.resolveAttribute(attrId, this, resolveReferences)
}
