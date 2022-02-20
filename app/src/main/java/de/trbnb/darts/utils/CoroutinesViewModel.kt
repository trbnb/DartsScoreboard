package de.trbnb.darts.utils

import de.trbnb.mvvmbase.observableproperty.ObservableProperty
import de.trbnb.mvvmbase.observableproperty.StateSaveOption
import kotlinx.coroutines.flow.Flow

interface CoroutinesViewModel {
    fun <T> Flow<T>.observe(defaultValue: T): ObservableProperty.Provider<T> {
        return ObservableProperty.Provider(defaultValue, StateSaveOption.None)
    }

    fun <T> Flow<T>.observe(): ObservableProperty.Provider<T?> {
        return ObservableProperty.Provider(null, StateSaveOption.None)
    }
}