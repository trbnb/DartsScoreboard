package de.trbnb.darts.ui.match

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.trbnb.darts.models.Player
import java.util.UUID

@Preview
@OptIn(ExperimentalUnsignedTypes::class)
@Composable
fun PlayerParticipationItemTemplate(
    player: State<Player> = mutableStateOf(Player(UUID.randomUUID(), "Thorben", Color.Black.value.toInt())),
    color: State<Color> = mutableStateOf(Color.Black),
    isCurrentTurn: State<Boolean> = mutableStateOf(true)
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Default.DoubleArrow,
            null,
            Modifier.width(16.dp).alpha(if (isCurrentTurn.value) 1f else 0f)
        )
        Box(Modifier.padding(horizontal = 4.dp).background(color.value).width(4.dp).height(24.dp))
        Text(
            player.value.name,
            Modifier.weight(1f),
            fontWeight = FontWeight.Bold
        )
        Text("22.22", Modifier.padding(end = 24.dp))
        Text("2", Modifier.width(20.dp), textAlign = TextAlign.End)
        Text("1", Modifier.width(20.dp), textAlign = TextAlign.End)
        Text("501", Modifier.width(48.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
    }
}
