package de.trbnb.darts.ui.match

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