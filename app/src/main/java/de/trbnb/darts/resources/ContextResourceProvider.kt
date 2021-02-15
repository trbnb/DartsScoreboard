package de.trbnb.darts.resources

import android.content.Context
import android.util.TypedValue
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * [ResourceProvider] implementation base off on [Context].
 */
class ContextResourceProvider @Inject constructor(@ApplicationContext private val context: Context) : ResourceProvider {
    override fun getBoolean(booleanId: Int): Boolean = context.resources.getBoolean(booleanId)

    override fun getColor(colorId: Int): Int = context.getColorCompat(colorId)

    override fun getDimension(dimenId: Int): Float = context.resources.getDimension(dimenId)

    override fun getFraction(fractionId: Int, base: Int, pbase: Int): Float {
        return context.resources.getFraction(fractionId, base, pbase)
    }

    override fun getString(stringId: Int, vararg formatArgs: Any?): String {
        return if (formatArgs.isEmpty()) {
            context.getString(stringId)
        } else {
            context.getString(stringId, *formatArgs)
        }
    }

    override fun getCharSequence(stringId: Int): CharSequence = context.getText(stringId)

    override fun getIntArray(arrayId: Int): IntArray = context.resources.getIntArray(arrayId)

    override fun getStringArray(arrayId: Int): Array<out String> {
        return context.resources.getStringArray(arrayId)
    }

    override fun getTextArray(arrayId: Int): Array<out CharSequence> {
        return context.resources.getTextArray(arrayId)
    }

    override fun getPluralString(pluralId: Int, quantitiy: Int, vararg formatArgs: Any?): String {
        return context.resources.run {
            if (formatArgs.isEmpty()) {
                getQuantityString(pluralId, quantitiy)
            } else {
                getQuantityString(pluralId, quantitiy, *formatArgs)
            }
        }
    }

    override fun getPluralCharSequence(pluralId: Int, quantitiy: Int): CharSequence {
        return context.resources.getQuantityText(pluralId, quantitiy)
    }

    /**
     * Resolves an attribute to a [TypedValue] and converts it to a [ResourceValue].
     */
    override fun resolveAttribute(attrId: Int, resolveReferences: Boolean): ResourceValue {
        val typedValue = context.resolveAttribute(attrId, resolveReferences)
        val resourceId = typedValue.resourceId
        return when (typedValue.type) {
            TypedValue.TYPE_NULL -> ResourceValue.None
            TypedValue.TYPE_REFERENCE -> ResourceValue.Reference(resourceId)
            TypedValue.TYPE_ATTRIBUTE -> ResourceValue.Attribute(typedValue.data)
            TypedValue.TYPE_STRING -> ResourceValue.Text(typedValue.string, resourceId)
            TypedValue.TYPE_FLOAT -> ResourceValue.Float(typedValue.float, resourceId)
            TypedValue.TYPE_DIMENSION -> {
                ResourceValue.Dimension(typedValue.getDimension(context.resources.displayMetrics), resourceId)
            }
            TypedValue.TYPE_FRACTION -> ResourceValue.Fraction(resourceId)
            TypedValue.TYPE_FIRST_INT, TypedValue.TYPE_LAST_INT -> ResourceValue.Integer(typedValue.data, resourceId)
            TypedValue.TYPE_INT_BOOLEAN -> ResourceValue.Bool(typedValue.data == 1, resourceId)
            in TypedValue.TYPE_FIRST_COLOR_INT..TypedValue.TYPE_LAST_COLOR_INT -> {
                ResourceValue.Color(typedValue.data, resourceId)
            }

            else -> throw IllegalArgumentException("Type ${typedValue.type} is not supported.")
        }
    }
}
