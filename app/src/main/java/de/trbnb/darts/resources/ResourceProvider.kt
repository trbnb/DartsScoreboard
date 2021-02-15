package de.trbnb.darts.resources

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.annotation.AttrRes
import androidx.annotation.BoolRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.annotation.FractionRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

/**
 * Interface for access on app resources.
 * A production app would most likely use an implementation based on [Context].
 *
 * @see ContextResourceProvider
 */
interface ResourceProvider {
    /**
     * Gets a [Boolean] value.
     */
    fun getBoolean(@BoolRes booleanId: Int): Boolean

    /**
     * Gets a color as an integer value.
     *
     * @see android.graphics.Color
     */
    @ColorInt
    fun getColor(@ColorRes colorId: Int): Int

    /**
     * Gets a dimension in pixel size.
     */
    @Dimension(unit = Dimension.PX)
    fun getDimension(@DimenRes dimenId: Int): Float

    /**
     * Gets a fraction and multiplies it by the given base.
     *
     * @param base Base used if fraction is a normal fraction ("xx%").
     * @param pbase Base used if fraction is a parent fraction ("xx%p").
     */
    fun getFraction(@FractionRes fractionId: Int, base: Int, pbase: Int): Float

    /**
     * Gets a [String] from the resources.
     * @see String.format
     */
    fun getString(@StringRes stringId: Int, vararg formatArgs: Any?): String

    /**
     * Gets a text as [CharSequence] from the resources.
     * This can be useful to get a styled text (e.g. [android.text.SpannedString])
     */
    fun getCharSequence(@StringRes stringId: Int): CharSequence

    /**
     * Gets an [IntArray] from the resources.
     */
    fun getIntArray(@ArrayRes arrayId: Int): IntArray

    /**
     * Gets a [String] from the resources.
     */
    fun getStringArray(@ArrayRes arrayId: Int): Array<out String>

    /**
     * Gets a text as [CharSequence] from the resources.
     */
    fun getTextArray(@ArrayRes arrayId: Int): Array<out CharSequence>

    /**
     * Gets a [String] that can be different depending on [quantitiy].
     */
    fun getPluralString(@PluralsRes pluralId: Int, quantitiy: Int, vararg formatArgs: Any?): String

    /**
     * Gets a text as [CharSequence] that can be different depending on [quantitiy].
     */
    fun getPluralCharSequence(@PluralsRes pluralId: Int, quantitiy: Int): CharSequence

    /**
     * Resolves an attribute to a value.
     *
     * @param resolveReferences If set to true [ResourceValue.Attribute] is not a possible return value.
     */
    fun resolveAttribute(@AttrRes attrId: Int, resolveReferences: Boolean = true): ResourceValue
}

inline  fun <reified T : ResourceValue> ResourceProvider.resolveAttributeAs(
    @AttrRes attrId: Int,
    resolveReferences: Boolean = true
): T = resolveAttribute(attrId, resolveReferences) as T
