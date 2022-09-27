package de.trbnb.darts.ui.matchsetup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.RadioButton
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.trbnb.darts.R
import de.trbnb.darts.models.InOutRule
import de.trbnb.darts.ui.main.MainViewModel
import de.trbnb.darts.utils.infinity
import kotlin.text.Typography

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun MatchSetupDialog(
    sheetState: ModalBottomSheetState,
    onStartMatchClick: () -> Unit
) {
    val viewModel = hiltViewModel<MainViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            MatchSetupDialogTemplate(
                points = uiState.points,
                sets = uiState.sets,
                legs = uiState.legs,
                outRule = uiState.outRule,
                inRule = uiState.inRule,
                onPointsChanged = viewModel::setPoints,
                onSetsChanged = viewModel::setSets,
                onLegsChanged = viewModel::setLegs,
                onOutRuleChanged = viewModel::setOutRule,
                onInRuleChanged = viewModel::setInRule,
                onStartMatchClick = {
                    viewModel.createMatch()
                    onStartMatchClick()
                }
            )
        },
        content = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun MatchSetupDialogTemplate(
    points: Int = 301,
    onPointsChanged: (points: Int) -> Unit = {},
    sets: Int = 2,
    onSetsChanged: (sets: Int) -> Unit = {},
    legs: Int = 3,
    onLegsChanged: (legs: Int) -> Unit = {},
    outRule: InOutRule = InOutRule.STRAIGHT,
    onOutRuleChanged: (outRule: InOutRule) -> Unit = {},
    inRule: InOutRule = InOutRule.DOUBLE,
    onInRuleChanged: (inRule: InOutRule) -> Unit = {},
    onStartMatchClick: () -> Unit = {}
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(BorderStroke(1.dp, MaterialTheme.colors.secondary), MaterialTheme.shapes.medium)
            .padding(8.dp)
    ) {
        Text(
            "Punkte".uppercase(),
            Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.subtitle2
        )

        //Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colors.secondary).padding(top = 4.dp))

        Row {
            Text(
                points.toString(),
                Modifier
                    .width(64.dp)
                    .align(Alignment.CenterVertically),
                textAlign = TextAlign.End
            )
            Slider(
                points.toFloat(),
                { onPointsChanged(it.toInt()) },
                valueRange = 101f..1001f,
                steps = 100
            )
        }
        SetOrLegSetting("Sets", sets, onSetsChanged, 1f..10f)
        SetOrLegSetting(if (legs == 0) "∞" else legs.toString(), legs, onLegsChanged, 0f..20f)
    }

    OutOrInRule("Out rule", outRule, onOutRuleChanged)
    OutOrInRule("In rule", inRule, onInRuleChanged)

    Button(
        onClick = onStartMatchClick,
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Icon(
            painterResource(R.drawable.ic_darts_board_24dp),
            contentDescription = null,
            Modifier.padding(end = 8.dp)
        )
        Text("GAME ON!")
    }
}

@Composable
fun SetOrLegSetting(
    text: String,
    value: Int,
    onValueChanged: (value: Int) -> Unit,
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
            value.takeUnless { it == 0 }?.toString() ?: Typography.infinity.toString(),
            Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )
    }
    Slider(
        value.toFloat(),
        { onValueChanged(it.toInt()) },
        valueRange = range,
        steps = 1
    )
}

@Composable
fun OutOrInRule(text: String, value: InOutRule, onValueChanged: (value: InOutRule) -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(BorderStroke(1.dp, MaterialTheme.colors.secondary), MaterialTheme.shapes.medium)
            .padding(8.dp)
    ) {
        Text(
            text.uppercase(),
            Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.subtitle2
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            InOutRule.values().forEach { rule ->
                RadioButton(
                    text = rule.toString().lowercase(),
                    selected = value == rule,
                    onClick = { onValueChanged(rule) }
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