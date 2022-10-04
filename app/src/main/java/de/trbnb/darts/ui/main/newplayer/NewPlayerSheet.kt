package de.trbnb.darts.ui.main.newplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.trbnb.darts.ui.customview.ColorPicker

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun NewPlayerSheet(
    sheetState: ModalBottomSheetState,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit = {}
) {
    val viewModel = hiltViewModel<NewPlayerViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.shouldClose) {
        if (uiState.shouldClose) {
            sheetState.hide()
        }
    }

    NewPlayerDialogTemplate(
        sheetState,
        name = uiState.name,
        onNameChanged = { viewModel.set(name = it) },
        color = Color(uiState.color),
        onColorChanged = { viewModel.set(color = it.value) },
        createButtonEnabled = true,
        onCancel = onDismiss,
        onConfirm = {
            viewModel.create()
            onDismiss()
        },
        content = content
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun NewPlayerDialogTemplate(
    sheetState: ModalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded),
    name: String = "",
    onNameChanged: (name: String) -> Unit = {},
    color: Color = Color.Red,
    onColorChanged: (color: Color) -> Unit = {},
    createButtonEnabled: Boolean = true,
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    ModalBottomSheetLayout(
        sheetState = sheetState,
        content = content,
        sheetContent = {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val focusManager = LocalFocusManager.current
                TextField(
                    name,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = onNameChanged,
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Name") },
                    singleLine = true,
                    keyboardActions = KeyboardActions {
                        focusManager.clearFocus(true)
                    },
                    keyboardOptions = KeyboardOptions(
                        KeyboardCapitalization.Words,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .background(color, CircleShape)
                            .size(64.dp))

                    ColorPicker(
                        color = color,
                        onColorChanged = onColorChanged,
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 32.dp)
                    )
                }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(onClick = onCancel) { Text(stringResource(android.R.string.cancel)) }
                    Button(onClick = onConfirm, enabled = createButtonEnabled) { Text("Erstellen") }
                }
            }
        }
    )
}

