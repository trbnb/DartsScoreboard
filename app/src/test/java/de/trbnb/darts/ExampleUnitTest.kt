package de.trbnb.darts

import de.trbnb.darts.logic.finish.DoubleFinishSuggestionLogic
import de.trbnb.darts.models.description
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val suggestionLogic = DoubleFinishSuggestionLogic()

        (1..180).forEach { points ->
            suggestionLogic.getOne(points, 3)
                ?.map { it.description }
                .let { println(points.toString() + "\t" + (it?.joinToString() ?: "-")) }
        }
    }

    @Test
    fun foo() {
        runBlocking {
            flowOf(*(1..10).toList().toTypedArray())
                .map {
                    println(Thread.currentThread())
                    it * 2
                }
                .flowOn(Dispatchers.IO)
                .onEach { println(it) }
                .launchIn(this)
        }
    }
}