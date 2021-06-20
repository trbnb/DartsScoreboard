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
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.trbnb.darts.ui.customview.ColorPicker

/*
@AndroidEntryPoint
class NewPlayerDialogFragment : HiltMvvmBindingBottomSheetFragment<NewPlayerViewModel, DialogNewPlayerBinding>(
    R.layout.dialog_new_player
) {
    override fun onCreateDialog(savedInstanceState: Bundle?) = super.onCreateDialog(savedInstanceState).also { dialog ->
        (dialog as? BottomSheetDialog)?.behavior?.isHideable = false
    }

    override fun onBindingCreated(binding: DialogNewPlayerBinding) {
        super.onBindingCreated(binding)

        binding.nameInputEditText.post {
            binding.nameInputEditText.windowInsetsController?.show(WindowInsets.Type.ime())
        }
    }

    override fun onEvent(event: Event) {
        super.onEvent(event)

        when(event) {
            is CloseEvent -> dismiss()
        }
    }
}*/

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun NewPlayerDialogTemplate(
    nameState: MutableState<String> = mutableStateOf(""),
    colorState: MutableState<Color> = mutableStateOf(Color.Red),
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    ModalBottomSheetLayout(
        sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded),
        content = {},
        sheetContent = {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    nameState.value,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { nameState.value },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Name") }
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .background(colorState.value, CircleShape)
                            .size(64.dp))

                    ColorPicker(
                        color = colorState.value,
                        onColorChanged = { colorState.value = it },
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
                    Button(onClick = onConfirm) { Text("Erstellen") }
                }
            }
        }
    )
}

