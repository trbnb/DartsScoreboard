package de.trbnb.darts.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import de.trbnb.darts.R
import de.trbnb.darts.databinding.ActivityMainBinding
import de.trbnb.darts.di.HiltMvvmBindingActivity
import de.trbnb.darts.ui.events.StartMatchEvent
import de.trbnb.darts.ui.main.newplayer.NewPlayerDialogFragment
import de.trbnb.darts.ui.match.MatchActivity
import de.trbnb.mvvmbase.events.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : HiltMvvmBindingActivity<MainViewModel, ActivityMainBinding>(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.playerList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return menuInflater.inflate(R.menu.activity_main, menu).let { true }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_player -> NewPlayerDialogFragment().show(supportFragmentManager, null)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onEvent(event: Event) {
        super.onEvent(event)

        when (event) {
            is StartMatchEvent -> startActivity(Intent(this, MatchActivity::class.java))
        }
    }
}