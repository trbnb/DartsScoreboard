package de.trbnb.darts.ui.match

import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.trbnb.darts.domain.logic.MatchLogic
import de.trbnb.darts.domain.models.average
import de.trbnb.darts.domain.utils.average
import de.trbnb.darts.persistence.database.models.Player

@Composable
fun PlayerParticipationList(
    showSets: Boolean,
    currentParticipationStats: List<MatchLogic.CurrentParticipationStats>,
    currentPlayer: Player,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        item {
            PlayerParticipationHeader(showSets)
        }
        items(currentParticipationStats, key = { it.player.id }) { participationStats ->
            PlayerParticipationItemTemplate(
                showSets = showSets,
                participationStats = participationStats,
                isCurrentTurn = participationStats.player == currentPlayer
            )
        }
    }
}

@Composable
fun PlayerParticipationHeader(showSets: Boolean) {
    Column {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(28.dp).alignByBaseline())
            Spacer(Modifier.weight(1f))
            Text(
                Typography.average.toString(),
                Modifier
                    .alignByBaseline()
            )
            if (showSets) {
                Text(
                    "S",
                    Modifier
                        .width(28.dp)
                        .alignByBaseline(),
                    textAlign = TextAlign.End
                )
            }
            Text(
                "L",
                Modifier
                    .width(28.dp)
                    .alignByBaseline(),
                textAlign = TextAlign.End
            )
            Text(
                "Punkte",
                Modifier
                    .width(56.dp)
                    .alignByBaseline(),
                textAlign = TextAlign.End
            )
        }
        Divider()
    }
}

@Composable
fun PlayerParticipationItemTemplate(
    showSets: Boolean,
    participationStats: MatchLogic.CurrentParticipationStats,
    isCurrentTurn: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(16.dp)) {
            androidx.compose.animation.AnimatedVisibility(
                isCurrentTurn,
                enter = slideInHorizontally { -it },
                exit = fadeOut() + slideOutHorizontally { it.div(1.5f).toInt() }
            ) {
                Icon(
                    Icons.Default.DoubleArrow,
                    null,
                    Modifier.matchParentSize()
                )
            }
        }
        Box(
            Modifier
                .padding(horizontal = 4.dp)
                .background(Color(participationStats.player.color.toULong()))
                .width(4.dp)
                .height(24.dp)
        )
        Text(
            participationStats.player.name,
            Modifier.weight(1f),
            fontWeight = if (isCurrentTurn) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            String.format("%.2f", participationStats.matchParticipation.average)
        )
        if (showSets) {
            Text(
                participationStats.matchParticipation.wonSets.toString(),
                Modifier.width(28.dp),
                textAlign = TextAlign.End
            )
        }
        Text(
            participationStats.currentSet.wonLegs.toString(),
            Modifier.width(28.dp),
            textAlign = TextAlign.End
        )
        Text(
            participationStats.remainingPoints.toString(),
            Modifier.width(56.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}