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
    var gameState: GameState

    private var BOARD_ROWS = 20
    private var BOARD_COLUMNS = 10

    private val BOUNDARY_COLOR = Color.BLACK
    private val BOUNDARY_WIDTH = 10f

    private val GRID_COLOR = Color.rgb(61, 90, 128)
    private val GRID_WIDTH = 7f

    private val BAKCGROUND_COLOR = Color.rgb(224, 251, 252);

    private var parentHeight: Int? = null
    private var parentWidth: Int? = null
    private var horizontalRatio: Int? = null
    private var verticalRatio: Int? = null
    private var paint: Paint

    init {
        paint = Paint()
        paint.color = Color.BLUE
        this.gameState = gameState
    }



    private fun getBlockColorCode(color: Int): Int {
        return when (color) {
            1 -> Color.rgb(255, 214, 10)
            2 -> Color.rgb(29, 53, 87)
            3 -> Color.rgb(247, 184, 1)
            4 -> Color.rgb(92, 0, 139)
            5 -> Color.rgb(208, 0, 0)
            6 -> Color.rgb(85, 166, 48)
            7 -> Color.rgb(0, 180, 216)
            else -> Color.TRANSPARENT
        }
    }

    private fun DrawMatrix(matrix: Array<Array<BasicBlock?>>, canvas: Canvas) {
        for (i in 0..(BOARD_ROWS - 1)) {
            for (j in 0..(BOARD_COLUMNS - 1)) {
                if (matrix[i][j]!!.state === BasicBlockState.ON_EMPTY) continue
                val color = getBlockColorCode(matrix[i][j]!!.color)
                val p = Paint()
                p.color = color

                DrawBlock(canvas, i, j, p)
            }
        }
    }

    private fun Clear(matrix: Array<Array<BasicBlock?>>, canvas: Canvas) {
        val p = Paint()
        p.color = BAKCGROUND_COLOR

        for (i in 0..(BOARD_ROWS - 1)) {
            for (j in 0..(BOARD_COLUMNS - 1)) {
                DrawBlock(canvas, i, j, p)
            }
        }
    }

    private fun DrawTetramino(tetramino: Tetramino, canvas: Canvas) {
        for (block in tetramino.blocks) {
            val color = getBlockColorCode(block!!.color)
            val p = Paint()
            p.color = color

            DrawBlock(canvas, block.coordinate.y, block!!.coordinate.x, p)
        }
    }

    private fun Boundary(canvas: Canvas) {
        paint.color = BOUNDARY_COLOR
        paint.strokeWidth = BOUNDARY_WIDTH

        //left line
        canvas.drawLine(0f, 0f, 0f, parentHeight!!.toFloat(), paint)

        //top line
        canvas.drawLine(0f, 0f, parentWidth!!.toFloat(), 0f, paint)

        //bottom line
        canvas.drawLine(
            parentWidth!!.toFloat(),
            0f,
            parentWidth!!.toFloat(),
            parentHeight!!.toFloat(),
            paint
        )

        //right line
        canvas.drawLine(
            parentWidth!!.toFloat(),
            parentHeight!!.toFloat(),
            0f,
            parentHeight!!.toFloat(),
            paint
        )
    }

    private fun grid(canvas: Canvas) {
        paint.strokeWidth = GRID_WIDTH
        paint.color = GRID_COLOR

        drawVerticalLines(canvas)
        drawHorizontalLines(canvas)
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
        parentHeight = 1300;//(parent as View).height
        parentWidth = 850//(parent as View).width
        verticalRatio = (parentHeight!! / BOARD_ROWS)
        horizontalRatio = (parentWidth!! / BOARD_COLUMNS)

        paint.color = Color.BLACK
        paint.strokeWidth = 1f

        Boundary(canvas)
        grid(canvas)

        if (gameState.gameIsRunning) {
            Clear(gameState.board, canvas)
            DrawMatrix(gameState.board, canvas)
            DrawTetramino(gameState.falling, canvas)
            //PrintScore(gameState.score, canvas)
        } else {
            val paint = Paint()
            DrawMatrix(gameState.board, canvas)
            DrawTetramino(gameState.falling, canvas)
            paint.color = Color.BLACK
            paint.textSize = 200f
            canvas.drawText(resources.getString(R.string.game_over), 60f, 800f, paint)
            //PrintScore(gameState.score, canvas)
        }

    }


    //**Utility**\\
    private fun drawHorizontalLines(canvas: Canvas) {
        var j = 0
        while (j < parentHeight!!) {
            canvas.drawLine(
                0f,
                j.toFloat(),
                parentWidth!!.toFloat(),
                j.toFloat(),
                paint
            )
            j = j + verticalRatio!!
        }
    }

    private fun drawVerticalLines(canvas: Canvas) {
        var i = 0;
        while (i < parentWidth!!) {
            canvas.drawLine(
                i.toFloat(),
                0f,
                i.toFloat(),
                parentHeight!!.toFloat(),
                paint
            )
            i = i + horizontalRatio!!
        }
    }

    private fun DrawBlock(canvas: Canvas, i: Int, j: Int, p: Paint) {
        canvas.drawRect(
            (j * horizontalRatio!! + GRID_WIDTH / 2),
            (i * verticalRatio!! + GRID_WIDTH / 2),
            (horizontalRatio!! + j * horizontalRatio!! - GRID_WIDTH / 2),
            ((i + 1) * verticalRatio!! - GRID_WIDTH / 2),
            p
        )
    }
}