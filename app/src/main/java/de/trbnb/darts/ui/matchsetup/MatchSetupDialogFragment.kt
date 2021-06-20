package de.trbnb.darts.ui.matchsetup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.RadioButton
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import de.trbnb.darts.R
import de.trbnb.darts.models.InOutRule
import de.trbnb.darts.utils.infinity
import de.trbnb.mvvmbase.utils.observeAsMutableState
import java.util.Locale

@Composable
fun MatchSetupDialog() {
    val viewModel = hiltNavGraphViewModel<MatchSetupViewModel>()
    MatchSetupDialogTemplate(
        pointsState = viewModel::points.observeAsMutableState(),
        setsState = viewModel::sets.observeAsMutableState(),
        legsState = viewModel::legs.observeAsMutableState(),
        outRuleSelection = viewModel::outRule.observeAsMutableState(),
        inRuleSelection = viewModel::inRule.observeAsMutableState()
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MatchSetupDialogTemplate(
    pointsState: MutableState<Int> = mutableStateOf(301),
    setsState: MutableState<Int> = mutableStateOf(2),
    legsState: MutableState<Int> = mutableStateOf(3),
    outRuleSelection: MutableState<InOutRule> = mutableStateOf(InOutRule.STRAIGHT),
    inRuleSelection: MutableState<InOutRule> = mutableStateOf(InOutRule.DOUBLE),
) {
    ModalBottomSheetLayout(
        sheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded),
        sheetContent = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(BorderStroke(1.dp, MaterialTheme.colors.secondary), MaterialTheme.shapes.medium)
                    .padding(8.dp)
            ) {
                Text(
                    "Punkte".toUpperCase(Locale.getDefault()),
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.subtitle2
                )

                //Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colors.secondary).padding(top = 4.dp))

                Row {
                    Text(
                        pointsState.value.toString(),
                        Modifier
                            .width(64.dp)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.End
                    )
                    Slider(
                        pointsState.value.toFloat(),
                        { pointsState.value = it.toInt() },
                        valueRange = 101f..1001f,
                        steps = 100
                    )
                }
                SetOrLegSetting("Sets", setsState, 1f..10f)
                SetOrLegSetting("Legs", legsState, 0f..20f)
            }

            OutOrInRule("Out rule", outRuleSelection)
            OutOrInRule("In rule", inRuleSelection)

            Button(onClick = {}, Modifier.fillMaxWidth().padding(8.dp)) {
                Icon(
                    painterResource(R.drawable.ic_darts_board_24dp),
                    contentDescription = null,
                    Modifier.padding(end = 8.dp)
                )
                Text("GAME ON!")
            }
        },
        content = {}
    )
}

@Composable
fun SetOrLegSetting(
    text: String = "Sets",
    numberSettings: MutableState<Int> = mutableStateOf(2),
    range: ClosedFloatingPointRange<Float>
) = Row(verticalAlignment = Alignment.CenterVertically) {
    Row(Modifier.width(64.dp)) {
        Text(
            text,
            Modifier
                .fillMaxWidth(0.7f)
                .background(MaterialTheme.colors.primary, MaterialTheme.shapes.medium),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onPrimary
        )
        Text(
            numberSettings.value.takeUnless { it == 0 }?.toString() ?: Typography.infinity.toString(),
            Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )
    }
    Slider(
        numberSettings.value.toFloat(),
        { numberSettings.value = it.toInt() },
        valueRange = range,
        steps = 1
    )
}

@Composable
fun OutOrInRule(text: String, selectedState: MutableState<InOutRule>) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(BorderStroke(1.dp, MaterialTheme.colors.secondary), MaterialTheme.shapes.medium)
            .padding(8.dp)
    ) {
        Text(
            text.toUpperCase(Locale.getDefault()),
            Modifier.fillMaxWidth().padding(bottom = 8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.subtitle2
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            InOutRule.values().forEach { rule ->
                RadioButton(
                    text = rule.toString().toLowerCase(Locale.getDefault()),
                    selected = selectedState.value == rule,
                    onClick = { selectedState.value = rule }
                )
            }
        }
    }
}

@Composable
fun RadioButton(
    text: String,
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) = Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
    val interactionSource = remember { MutableInteractionSource() }
    RadioButton(selected, onClick, interactionSource = interactionSource)

    val textModifier = if (onClick != null) {
        Modifier.selectable(
            selected = selected,
            onClick = onClick,
            enabled = true,
            role = Role.RadioButton,
            interactionSource = interactionSource,
            indication = null
        )
    } else Modifier

    Text(
        text = text,
        textModifier.padding(horizontal = 4.dp)
    )
}