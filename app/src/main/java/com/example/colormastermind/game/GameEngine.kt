package com.example.colormastermind.game

class GameEngine( codeLength: Int = 6) {
    private val secret: List<GameColor> =
        List(codeLength) { GameColor.values().random() }

    fun evaluateGuess(guess: List<GameColor>): Result {
        var correctPosition = 0
        var correctColor = 0
        
        val secretMatch = arrayOfNulls<GameColor>(secret.size)
        for (i in secret.indices) secretMatch[i] = secret[i]

        val guessMatch = arrayOfNulls<GameColor>(guess.size)
        for (i in guess.indices) guessMatch[i] = guess[i]

        for (i in guess.indices) {
            if (guess[i] == secret[i]) {
                correctPosition++
                secretMatch[i] = null
                guessMatch[i] = null
            }
        }

        for (i in guessMatch.indices) {
            val g = guessMatch[i]
            if (g != null) {
                for (j in secretMatch.indices) {
                    if (secretMatch[j] != null && secretMatch[j] == g) {
                        correctColor++
                        secretMatch[j] = null
                        break
                    }
                }
            }
        }

        return Result(correctPosition, correctColor)
    }


    fun getSecretCode(): List<GameColor> = secret

    fun reveal(): List<GameColor> = secret.toList()

    data class Result(val correctPosition: Int,
                      val correctColor: Int)
}