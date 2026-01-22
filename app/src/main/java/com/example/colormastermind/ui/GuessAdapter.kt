package com.example.colormastermind.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.colormastermind.R
import com.example.colormastermind.game.GameColor
import com.example.colormastermind.game.GameEngine.Result

class GuessAdapter(
    private val guesses: List<Pair<List<GameColor>, Result>>
) : RecyclerView.Adapter<GuessAdapter.GuessViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuessViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_guess, parent, false)
        return GuessViewHolder(view)
    }

    override fun onBindViewHolder(holder: GuessViewHolder, position: Int) {
        val (colors, result) = guesses[position]
        holder.bind(colors, result)
    }

    override fun getItemCount(): Int = guesses.size

    class GuessViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val colorRow: LinearLayout = view.findViewById(R.id.colorRow)
        private val resultText: TextView = view.findViewById(R.id.resultText)

        fun bind(colors: List<GameColor>, result: Result) {
            colorRow.removeAllViews()

            for (color in colors) {
                val v = View(itemView.context).apply {
                    layoutParams = LinearLayout.LayoutParams(76, 76).apply {
                        marginEnd = 8
                    }
                    setBackgroundColor(
                        ContextCompat.getColor(context, color.colorRes)
                    )
                }
                colorRow.addView(v)
            }

            resultText.text = "✔ ${result.correctPosition}  ● ${result.correctColor}"
        }
    }
}