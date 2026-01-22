package com.example.colormastermind.ui

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.colormastermind.R
import com.example.colormastermind.game.GameColor
import com.example.colormastermind.game.GameEngine
import com.example.colormastermind.game.GameEngine.Result
import com.example.colormastermind.game.GameOutcome
import android.util.Log
import androidx.appcompat.app.AlertDialog
import android.view.View
import android.widget.TextView
import kotlin.text.clear

class MainActivity : AppCompatActivity() {

    private lateinit var engine: GameEngine

    private lateinit var guessList: RecyclerView
    private lateinit var currentGuessRow: LinearLayout
    private lateinit var submitButton: Button

    private val currentGuess = mutableListOf<GameColor>()
    private val guesses = mutableListOf<Pair<List<GameColor>, Result>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        engine = GameEngine(codeLength = 6)

        Log.d("GameDebug", "A megoldás: ${engine.getSecretCode().joinToString(", ")}")

        guessList = findViewById(R.id.guessList)
        currentGuessRow = findViewById(R.id.currentGuessRow)
        submitButton = findViewById(R.id.submitGuess)

        setupRecyclerView()
        setupCurrentGuess()
        setupSubmitButton()
    }

    private fun setupRecyclerView() {
        guessList.layoutManager = LinearLayoutManager(this)
        guessList.adapter = GuessAdapter(guesses)
    }

    private fun setupCurrentGuess() {
        repeat(6) {
            currentGuess.add(GameColor.RED) // default
        }

        redrawCurrentGuess()
    }

    private fun redrawCurrentGuess() {
        currentGuessRow.removeAllViews()

        for (color in currentGuess) {
            val view = createColorView(color)
            currentGuessRow.addView(view)
        }
    }

    private fun createColorView(color: GameColor): android.view.View {
        val size = resources.getDimensionPixelSize(R.dimen.color_slot_size)
        return android.view.View(this).apply {
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                marginEnd = 12
            }
            setBackgroundColor(getColor(color.colorRes))

            setOnClickListener {
                cycleColor(this)
            }
        }
    }

    private fun setupSubmitButton() {
        submitButton.setOnClickListener {
            if (guesses.size >= 10) return@setOnClickListener

            val guessCopy = currentGuess.toList()
            val result = engine.evaluateGuess(guessCopy)

            guesses.add(guessCopy to result)
            guessList.adapter?.notifyItemInserted(guesses.size - 1)

            if (result.correctPosition == 6) {
                showGameResultDialog(GameOutcome.WIN)
                return@setOnClickListener
            }

            if (guesses.size >= 10) {
                showGameResultDialog(GameOutcome.LOSE)
                return@setOnClickListener
            }

            resetCurrentGuess()
        }
    }

    private fun resetCurrentGuess() {
        currentGuess.clear()
        repeat(6) {
            currentGuess.add(GameColor.RED)
        }
        redrawCurrentGuess()
    }

    private fun cycleColor(view: android.view.View) {
        val index = currentGuessRow.indexOfChild(view)
        if (index == -1) return

        val current = currentGuess[index]
        val nextOrdinal = (current.ordinal + 1) % GameColor.values().size
        val next = GameColor.values()[nextOrdinal]

        currentGuess[index] = next
        view.setBackgroundColor(getColor(next.colorRes))
    }

    private fun restartGame() {
        engine = GameEngine(codeLength = 6)
        Log.d("GameDebug", "A megoldás: ${engine.getSecretCode().joinToString(", ")}")
        guesses.clear()
        guessList.adapter?.notifyDataSetChanged()
        resetCurrentGuess()
    }

    private fun showGameResultDialog(outcome: GameOutcome) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_game_result, null)

        val titleText = dialogView.findViewById<TextView>(R.id.titleText)
        val messageText = dialogView.findViewById<TextView>(R.id.messageText)
        val secretRow = dialogView.findViewById<LinearLayout>(R.id.secretRow)

        when (outcome) {
            GameOutcome.WIN -> {
                titleText.setText(R.string.win_title)
                messageText.setText(R.string.win_message)
                secretRow.visibility = View.GONE
            }

            GameOutcome.LOSE -> {
                titleText.setText(R.string.lose_title)
                messageText.setText(R.string.lose_message)

                for (color in engine.reveal()) {
                    secretRow.addView(createSecretColorView(color))
                }
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<Button>(R.id.restartButton)
            .setOnClickListener {
                dialog.dismiss()
                restartGame()
            }

        dialog.show()
    }

    private fun createSecretColorView(color: GameColor): View {
        val size = resources.getDimensionPixelSize(R.dimen.secret_color_size)

        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                marginEnd = 8
            }
            setBackgroundColor(getColor(color.colorRes))
        }
    }

}