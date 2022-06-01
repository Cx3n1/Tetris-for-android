package course.android.tetris

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import course.android.tetris.data.BasicBlock
import course.android.tetris.data.BasicBlockState
import course.android.tetris.data.Tetramino

class DrawView(context: Context?, gameState: GameState) :
    View(context) {
    var yOffset: Int
    var paint: Paint
    var gameState: GameState


    private fun getBlockColorCode(color: Int): Int {
        return when (color) {
            1 -> Color.rgb(255, 214, 10)
            2 -> Color.rgb(29,53,87)
            3 -> Color.rgb(247,184,1)
            4 -> Color.rgb(92,0,139)
            5 -> Color.rgb(208, 0, 0)
            6 -> Color.rgb(85, 166, 48)
            7 -> Color.rgb(0, 180, 216)
            else -> Color.TRANSPARENT
        }
    }

    private fun DrawMatrix(matrix: Array<Array<BasicBlock?>>, canvas: Canvas) {
        for (i in 0..(Game.BOARD_ROWS-1)) {
            for (j in 0..(Game.BOARD_COLUMNS - 1)) {
                if (matrix[i][j]!!.state === BasicBlockState.ON_EMPTY) continue

                val color = getBlockColorCode(matrix[i][j]!!.color)
                val p = Paint()

                p.color = color

                canvas.drawRect(
                    (42 + j * 50).toFloat(),
                    (yOffset + i * 50 + 2).toFloat(),
                    (88 + j * 50).toFloat(),
                    (yOffset + (i + 1) * 50 - 2).toFloat(),
                    p
                )
            }
        }
    }

    private fun Clear(matrix: Array<Array<BasicBlock?>>, canvas: Canvas) {
        val p = Paint()
        p.color = Color.WHITE
        for (i in 0..(Game.BOARD_ROWS-1)) {
            for (j in 0..(Game.BOARD_COLUMNS - 1)) {
                canvas.drawRect(
                    (42 + j * 50).toFloat(),
                    (yOffset + i * 50 + 2).toFloat(),
                    (88 + j * 50).toFloat(),
                    (yOffset + (i + 1) * 50 - 2).toFloat(),
                    p
                )
            }
        }
    }

    private fun DrawTetramino(tetramino: Tetramino, canvas: Canvas) {
        for (block in tetramino.blocks) {
            val color = getBlockColorCode(block!!.color)
            val p = Paint()
            p.color = color
            canvas.drawRect(
                (42 + block!!.coordinate.x * 50).toFloat(),
                (yOffset + block.coordinate.y * 50 + 2).toFloat(),
                (88 + block.coordinate.x * 50).toFloat(),
                (yOffset + (block.coordinate.y + 1) * 50 - 2).toFloat(), p
            )
        }
    }

    private fun Boundary(canvas: Canvas) {
        paint.color = Color.BLACK
        paint.strokeWidth = 5f
        canvas.drawLine(40f, yOffset.toFloat(), 40f, (yOffset + 1200).toFloat(), paint)
        canvas.drawLine(40f, yOffset.toFloat(), 1040f, yOffset.toFloat(), paint)
        canvas.drawLine(1040f, yOffset.toFloat(), 1040f, (yOffset + 1200).toFloat(), paint)
        canvas.drawLine(1040f, (yOffset + 1200).toFloat(), 40f, (yOffset + 1200).toFloat(), paint)
    }

    private fun grid(canvas: Canvas) {
        paint.strokeWidth = 2f
        var i = 90
        while (i < 1040) {
            canvas.drawLine(
                i.toFloat(),
                yOffset.toFloat(),
                i.toFloat(),
                (yOffset + 1200).toFloat(),
                paint
            )
            i = i + 50
        }
        var j = 50
        while (j < 1200) {
            canvas.drawLine(40f, (yOffset + j).toFloat(), 1040f, (yOffset + j).toFloat(), paint)
            j = j + 50
        }
    }

    private fun PrintScore(score: Int, canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.TRANSPARENT
        canvas.drawRect(0f, 100f, 200f, 200f, paint)
        paint.color = Color.BLACK
        paint.textSize = 100f
        canvas.drawText(Integer.toString(score), 80f, 170f, paint)
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Color.BLACK
        paint.strokeWidth = 5f
        Boundary(canvas)
        grid(canvas)
        if (gameState.gameIsRunning) {
            Clear(gameState.board, canvas)
            DrawMatrix(gameState.board, canvas)
            DrawTetramino(gameState.falling, canvas)
            PrintScore(gameState.score, canvas)
        } else {
            val paint = Paint()
            DrawMatrix(gameState.board, canvas)
            DrawTetramino(gameState.falling, canvas)
            paint.color = Color.BLACK
            paint.textSize = 200f
            canvas.drawText(resources.getString(R.string.game_over), 60f, 800f, paint)
            PrintScore(gameState.score, canvas)
        }
    }

    init {
        paint = Paint()
        paint.color = Color.BLUE
        yOffset = 200
        this.gameState = gameState
    }
}