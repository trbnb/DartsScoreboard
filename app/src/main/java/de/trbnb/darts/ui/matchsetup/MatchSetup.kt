package de.trbnb.darts.ui.matchsetup

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.trbnb.darts.R
import de.trbnb.darts.models.InOutRule
import de.trbnb.darts.utils.infinity

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun MatchSetupLayoutPreview() {
    MatchSetupLayout(
        canStartGame = true,
        points = 301,
        sets = 2,
        legs = 3,
        outRule = InOutRule.STRAIGHT,
        inRule = InOutRule.DOUBLE,
        onPointsChanged = {},
        onSetsChanged = {},
        onLegsChanged = {},
        onOutRuleChanged = {},
        onInRuleChanged = {},
        onStartMatchClick = {}
    )
}

@Composable
fun MatchSetupLayout(
    canStartGame: Boolean,
    points: Int,
    sets: Int,
    legs: Int,
    outRule: InOutRule,
    inRule: InOutRule,
    onPointsChanged: (points: Int) -> Unit,
    onSetsChanged: (sets: Int) -> Unit,
    onLegsChanged: (legs: Int) -> Unit,
    onOutRuleChanged: (outRule: InOutRule) -> Unit,
    onInRuleChanged: (inRule: InOutRule) -> Unit,
    onStartMatchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            DropdownSelection(
                title = "Punkte",
                value = points,
                values = (101..1001 step 100).toList(),
                valueConverter = Int::toString,
                onValueChanged = onPointsChanged,
                modifier = Modifier.weight(1f)
            )
            DropdownSelection(
                title = "Sets",
                value = sets,
                values = (1..10).toList(),
                valueConverter = Int::toString,
                onValueChanged = onSetsChanged,
                modifier = Modifier.weight(1f)
            )
            DropdownSelection(
                title = "Legs pro Set",
                value = legs,
                values = (0..10).toList(),
                valueConverter = { (if (it == 0) Typography.infinity else it).toString() },
                onValueChanged = onLegsChanged,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            OutOrInRuleDropdownSelection(
                title = "In rule",
                value = inRule,
                onValueChanged = onInRuleChanged,
                modifier = Modifier.weight(1f)
            )
            OutOrInRuleDropdownSelection(
                title = "Out rule",
                value = outRule,
                onValueChanged = onOutRuleChanged,
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            enabled = canStartGame,
            onClick = onStartMatchClick,
            modifier = Modifier
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
}

@Composable
fun OutOrInRuleDropdownSelection(
    title: String,
    value: InOutRule,
    onValueChanged: (value: InOutRule) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownSelection(
        title = title,
        value = value,
        values = InOutRule.values().toList(),
        onValueChanged = onValueChanged,
        valueConverter = { it.name.lowercase() },
        modifier = modifier
    )
}


@Composable
fun <T> DropdownSelection(
    title: String,
    value: T,
    values: List<T>,
    onValueChanged: (value: T) -> Unit,
    valueConverter: (value: T) -> String,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .wrapContentHeight()
            .border(1.dp, MaterialTheme.colors.secondary, RoundedCornerShape(4.dp))
            .clickable(onClick = { isExpanded = true })
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(8.dp)
        ) {
            Text(title.uppercase(), style = MaterialTheme.typography.overline)
            Row(
                modifier = Modifier.wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(valueConverter(value), Modifier.weight(1f))
                Icon(Icons.Default.ArrowDropDown, "")
            }
        }

        DropdownMenu(
            isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            values.forEach { possibleValue ->
                DropdownMenuItem(
                    onClick = {
                        onValueChanged(possibleValue)
                        isExpanded = false
                    }
                ) {
                    Text(valueConverter(possibleValue), Modifier.weight(1f))
                    if (possibleValue == value) {
                        Icon(Icons.Default.Check, "", Modifier.padding(start = 4.dp))
                    }
                }
            }
        }
    }
}