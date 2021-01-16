package de.trbnb.darts.resources

import android.content.Context
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ContextResourceProvider @Inject constructor(@ApplicationContext private val context: Context) : ResourceProvider {
    override fun getColor(colorRes: Int): Int {
        return ContextCompat.getColor(context, colorRes)
    }
}