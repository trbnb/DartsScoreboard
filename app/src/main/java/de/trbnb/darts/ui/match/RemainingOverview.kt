package de.trbnb.darts.ui.match

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.trbnb.darts.domain.logic.TurnState
import de.trbnb.darts.domain.models.Field
import de.trbnb.darts.domain.models.Multiplier
import de.trbnb.darts.domain.models.PotentialThrow
import de.trbnb.darts.domain.models.description

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun RemainingOverviewPreview() {
    RemainingOverview(
        remainingPoints = 301,
        turnState = TurnState.Bust,
        suggestedFinishes = List(10) {
            listOf(
                PotentialThrow(Field.EIGHT, Multiplier.TRIPLE),
                PotentialThrow(Field.BULL, Multiplier.DOUBLE),
                PotentialThrow(Field.TWENTY, Multiplier.DOUBLE)
            )
        }.takeIf { true }
    )
}

@Composable
fun RemainingOverview(uiState: MatchViewModel.UiState, modifier: Modifier = Modifier) {
    RemainingOverview(
        remainingPoints = uiState.remainingPoints,
        turnState = uiState.turnState,
        suggestedFinishes = uiState.suggestedFinishes,
        modifier = modifier
    )
}

@Composable
fun RemainingOverview(
    remainingPoints: Int,
    turnState: TurnState,
    suggestedFinishes: List<List<PotentialThrow>>?,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.height(64.dp)
    ) {
        RemainingPoints(remainingPoints)
        Icon(Icons.Default.ChevronRight, "")
        Box(
            Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            when {
                turnState == TurnState.Bust -> BustText()
                suggestedFinishes == null -> LoadingSuggestionsRow()
                suggestedFinishes.isEmpty() -> NoFinishText()
                else -> FinishSuggestionRow(
                    suggestedFinishes,
                    Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun FinishSuggestionRow(
    finishSuggestions: List<List<PotentialThrow>>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(finishSuggestions) { finish ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .width(48.dp)
                    .fillMaxHeight()
                    .background(
                        BoardColors.green,
                        RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = finish.joinToString(separator = "\n") { it.description },
                    lineHeight = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun RemainingPoints(remainingPoints: Int) {
    Column(
        Modifier
            .width(96.dp)
            .wrapContentHeight()
            .padding(start = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Übrig".uppercase(),
            style = MaterialTheme.typography.overline,
            fontSize = 13.sp
        )
        Text(
            remainingPoints.toString(),
            style = MaterialTheme.typography.subtitle1,
            fontSize = 24.sp
        )
    }
}

@Composable
private fun NoFinishText() {
    Text(
        "Kein Finish verfügbar",
        style = MaterialTheme.typography.subtitle1,
        fontStyle = FontStyle.Italic,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun BustText() {
    Text(
        "Bust!".uppercase(),
        style = MaterialTheme.typography.subtitle1,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = BoardColors.red,
        letterSpacing = 4.sp,
        fontSize = 20.sp
    )
}

@Composable
private fun LoadingSuggestionsRow() {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp, alignment = Alignment.CenterHorizontally)
    ) {
        CircularProgressIndicator(Modifier.size(32.dp))
        Text(
            "Lade Vorschläge...",
            style = MaterialTheme.typography.subtitle1,
            fontStyle = FontStyle.Italic
        )
    }
}