package de.trbnb.darts.ui.main.newplayer

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.trbnb.darts.domain.players.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewPlayerViewModel @Inject constructor(
    private val playerRepository: PlayerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState("", Color.Black.value))
    val uiState = _uiState.asStateFlow()

    fun create() {
        if (!uiState.value.canCreate) return

        val (name, color) = uiState.value
        viewModelScope.launch {
            playerRepository.create(name, color)
            _uiState.value = _uiState.value.copy(shouldClose = true)
        }
    }

    fun set(
        name: String = _uiState.value.name,
        color: ULong = _uiState.value.color
    ) {
        _uiState.value = _uiState.value.copy(name = name, color = color)
    }

    data class UiState(
        val name: String,
        val color: ULong,
        val shouldClose: Boolean = false
    ) {
        val canCreate: Boolean get() = name.isNotBlank()
    }
}

