package de.trbnb.darts.ui.match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import de.trbnb.darts.models.Field
import de.trbnb.darts.models.Multiplier
import de.trbnb.darts.models.Throw
import de.trbnb.darts.models.plus
import de.trbnb.darts.models.toField

/*
@Suppress("EXPERIMENTAL_API_USAGE")
@AndroidEntryPoint
class MatchActivity : MvvmBindingActivity<MatchViewModel, ActivityMatchBinding>(R.layout.activity_match) {
    private var undoMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.subtitle = viewModel.subtitle
    }

    override fun onPostResume() {
        super.onPostResume()

        binding.playerList.post {
            val isScrollable = binding.playerList.isScrollable()
            viewModel.isPlayerListScrollable = isScrollable
            binding.playerList.overScrollMode = if (isScrollable) View.OVER_SCROLL_ALWAYS else View.OVER_SCROLL_NEVER
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_match, menu)
        menu.findItem(R.id.undoTurn).apply { undoMenuItem = this }.isEnabled = viewModel.canUndoTurn
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.undoTurn -> viewModel.undoTurn()
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onViewModelPropertyChanged(viewModel: MatchViewModel, fieldId: Int) {
        super.onViewModelPropertyChanged(viewModel, fieldId)

        when (fieldId) {
            BR.currentPlayerIndex -> binding.playerList.smoothScrollToPosition(viewModel.currentPlayerIndex)
            BR.canUndoTurn -> undoMenuItem?.isEnabled = viewModel.canUndoTurn
        }
    }

    override fun onEvent(event: Event) {
        super.onEvent(event)

        when (event) {
            is CloseEvent -> finish()
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this, R.style.Theme_Darts_Alert)
            .setTitle("Beenden?")
            .setPositiveButton(android.R.string.ok) { d, _ -> d.dismiss(); finish() }
            .setNegativeButton(android.R.string.cancel) { d, _ -> d.dismiss() }
            .show()
    }

    private fun RecyclerView.isScrollable(): Boolean {
        return computeHorizontalScrollRange() > width || computeVerticalScrollRange() > height
    }
}
*/

object BoardColors {
    val white = Color(0xFFD6BB90)
    val black = Color(0xFF000000)
    val red = Color(0xFFBB0F25)
    val green = Color(0xFF096B5E)
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun Input(
    modifier: Modifier = Modifier,
    onClick: (Throw) -> Unit = {}
) {
    Column(
        modifier.fillMaxWidth().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Row(
            Modifier.height(48.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {},
                Modifier.weight(1f).fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(backgroundColor = BoardColors.black, contentColor = Color.White)
            ) {
                Text("MISS", fontSize = 18.sp)
            }

            BullButton(onClick, Modifier.weight(2f).fillMaxHeight())
        }

        listOf(
            listOf(12, 5, 20, 1, 8),
            listOf(14, 9, 11, 4, 13),
            listOf(16, 8, 6, 15, 10),
            listOf(7, 19, 3, 17, 2)
        ).forEach { row ->
            Row(
                Modifier.height(96.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { number ->
                    FieldButton(number.toField(), onClick)
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
            content = { Text("25", fontSize = 18.sp) }
        )
        Button(
            onClick = { onClick(Field.BULL + Multiplier.DOUBLE) },
            colors = ButtonDefaults.buttonColors(BoardColors.red, Color.White),
            shape = RoundedCornerShape(0.dp, 4.dp, 4.dp, 0.dp),
            modifier = Modifier.weight(1f).fillMaxHeight(),
            content = { Text("BULL", fontSize = 18.sp) }
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
            Row(Modifier.fillMaxWidth().height(40.dp)) {
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