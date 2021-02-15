package de.trbnb.darts.resources

import androidx.annotation.AnyRes
import androidx.annotation.AttrRes
import androidx.annotation.BoolRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.FractionRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes

/**
 * Sealed class containing all possible values for [ResourceProvider.resolveAttribute].
 *
 * Represents all values that can be resolved from an attribute ID.
 */
sealed class ResourceValue {
    /**
     * Represents an unset attribute.
     */
    object None : ResourceValue()

    /**
     * Represents a resource ID (not an attribute ID!).
     */
    data class Reference(@AnyRes val resourceId: Int) : ResourceValue()

    /**
     * Represents an attribute ID.
     */
    data class Attribute(@AttrRes val attrId: Int) : ResourceValue()

    /**
     * Represents a [CharSequence] value.
     */
    data class Text(val string: CharSequence, @StringRes val resourceId: Int) : ResourceValue()

    /**
     * Represents a [kotlin.Float] value.
     */
    data class Float(val value: kotlin.Float, val resourceId: Int) : ResourceValue()

    /**
     * Represets a dimension (pixel as unit).
     */
    data class Dimension(
        @androidx.annotation.Dimension(unit = androidx.annotation.Dimension.PX)
        val value: kotlin.Float,
        @DimenRes
        val resourceId: Int
    ) : ResourceValue()

    /**
     * Represents a fraction resource.
     * Can be resolved with [ResourceProvider.getFraction].
     */
    data class Fraction(@FractionRes val resourceId: Int) : ResourceValue()

    /**
     * Represents an [Int] value.
     */
    data class Integer(val value: Int, @IntegerRes val resourceId: Int) : ResourceValue()

    /**
     * Represents a [Boolean] value.
     */
    data class Bool(val value: Boolean, @BoolRes val resourceId: Int) : ResourceValue()

    /**
     * Represents a color value.
     *
     * @see android.graphics.Color
     */
    data class Color(@ColorInt val color: Int, @ColorRes val resourceId: Int) : ResourceValue()
}
