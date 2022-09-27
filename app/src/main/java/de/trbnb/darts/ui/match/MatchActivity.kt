package de.trbnb.darts.ui.match

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.trbnb.darts.models.*

object BoardColors {
    val white = Color(0xFFD6BB90)
    val black = Color(0xFF000000)
    val red = Color(0xFFBB0F25)
    val green = Color(0xFF096B5E)
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun InputPreview() {
    Input({})
}

@Composable
fun Input(
    onThrow: (Throw) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxWidth().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            Modifier.height(48.dp).weight(0.5f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onThrow(Field.MISS + Multiplier.SINGLE) },
                Modifier.weight(1f).fillMaxHeight(),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = BoardColors.black, contentColor = Color.White)
            ) {
                Text("MISS", fontSize = 18.sp)
            }

            BullButton(onThrow, Modifier.weight(2f).fillMaxHeight())
        }

        listOf(
            listOf(12, 5, 20, 1, 8),
            listOf(14, 9, 11, 4, 13),
            listOf(16, 8, 6, 15, 10),
            listOf(7, 19, 3, 17, 2)
        ).forEach { row ->
            Row(
                Modifier.height(96.dp).weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { number ->
                    FieldButton(number.toField(), onThrow)
                }
            }
        }
    }
}

@Composable
fun BullButton(onClick: (Throw) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier) {
        Button(
            onClick = { onClick(Field.BULL + Multiplier.SINGLE) },
            colors = ButtonDefaults.buttonColors(BoardColors.green, Color.White),
            shape = RoundedCornerShape(4.dp, 0.dp, 0.dp, 4.dp),
            modifier = Modifier.weight(1f).fillMaxHeight(),
            content = { Text("25", fontSize = 18.sp) },
            contentPadding = PaddingValues(0.dp)
        )
        Button(
            onClick = { onClick(Field.BULL + Multiplier.DOUBLE) },
            colors = ButtonDefaults.buttonColors(BoardColors.red, Color.White),
            shape = RoundedCornerShape(0.dp, 4.dp, 4.dp, 0.dp),
            modifier = Modifier.weight(1f).fillMaxHeight(),
            content = { Text("BULL", fontSize = 18.sp) },
            contentPadding = PaddingValues(0.dp)
        )
    }
}

@Composable
fun RowScope.FieldButton(field: Field, onClick: (Throw) -> Unit) {
    Button(
        onClick = { onClick(field + Multiplier.SINGLE) },
        Modifier.weight(1f).fillMaxHeight(),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = BoardColors.white)
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(field.value.toString(), fontSize = 18.sp)
            }
            Row(Modifier.fillMaxWidth().height(40.dp).weight(1f)) {
                Button(
                    onClick = { onClick(field + Multiplier.DOUBLE) },
                    Modifier.weight(1f).fillMaxHeight(),
                    elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(backgroundColor = BoardColors.green, contentColor = Color.White)
                ) { Text ("D") }

                Button(
                    onClick = { onClick(field + Multiplier.TRIPLE) },
                    Modifier.weight(1f).fillMaxHeight(),
                    elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(backgroundColor = BoardColors.red, contentColor = Color.White)
                ) { Text ("T") }
            }
        }
    }
}