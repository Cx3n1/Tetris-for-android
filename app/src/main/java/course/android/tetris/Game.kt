package course.android.tetris

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import course.android.tetris.data.TetraminoType


class Game : AppCompatActivity(), View.OnClickListener {

    //TODO: This needs to be discussed
    companion object{
        /***
         * standard board size is 10x20
         */
        val BOARD_ROWS = 24//10
        val BOARD_COLUMNS = 20

        fun getMiddleReference(): Int{
            return BOARD_COLUMNS/2; //integer division is intentional
        }
    }

    var drawView: DrawView? = null
    var gameState: GameState? = null
    var gameButtons: RelativeLayout? = null
    var left: Button? = null
    var right: Button? = null
    var rotateAc: Button? = null
    var game: FrameLayout? = null
    var pause: Button? = null
    var score: TextView? = null
    var difficultyToggle: Button? = null
    var handler: Handler? = null
    var loop: Runnable? = null
    var delayFactor = 0
    var delay = 0
    var delayLowerLimit = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameState = GameState(BOARD_ROWS, BOARD_COLUMNS, TetraminoType.getRandomTetramino())

        drawView = DrawView(this, gameState!!)
        drawView!!.setBackgroundColor(Color.rgb(165, 165, 141))

        game = FrameLayout(this)

        gameButtons = RelativeLayout(this)

        delay = 500
        delayLowerLimit = 200
        delayFactor = 2

        left = Button(this)
        left!!.setText(R.string.left)
        left!!.id = R.id.left

        right = Button(this)
        right!!.setText(R.string.right)
        right!!.id = R.id.right

        rotateAc = Button(this)
        rotateAc!!.setText(R.string.rotate_ac)
        rotateAc!!.id = R.id.rotate_ac

        pause = Button(this)
        pause!!.setText(R.string.pause)
        pause!!.id = R.id.pause

        score = TextView(this)
        score!!.setText(R.string.score)
        score!!.id = R.id.score
        score!!.textSize = 30f

        difficultyToggle = Button(this)
        difficultyToggle!!.setText(R.string.easy)
        difficultyToggle!!.id = R.id.difficulty

        val rl = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        val leftButton = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        val rightButton = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        val downButton = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        val pausebutton = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        val scoretext = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        val speedbutton = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        gameButtons!!.layoutParams = rl


        gameButtons!!.addView(left)
        gameButtons!!.addView(right)
        gameButtons!!.addView(rotateAc)
        gameButtons!!.addView(pause)
        gameButtons!!.addView(score)
        gameButtons!!.addView(difficultyToggle)

        leftButton.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
        leftButton.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rightButton.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
        rightButton.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        downButton.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
        downButton.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        pausebutton.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
        pausebutton.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
        scoretext.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
        scoretext.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
        speedbutton.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
        speedbutton.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)

        left!!.layoutParams = leftButton
        right!!.layoutParams = rightButton
        rotateAc!!.layoutParams = downButton
        pause!!.layoutParams = pausebutton
        score!!.layoutParams = scoretext
        difficultyToggle!!.layoutParams = speedbutton
        game!!.addView(drawView)
        game!!.addView(gameButtons)

        setContentView(game)

        val leftButtonListener: View = findViewById(R.id.left)
        leftButtonListener.setOnClickListener(this)

        val rightButtonListener: View = findViewById(R.id.right)
        rightButtonListener.setOnClickListener(this)

        val rotateACButtonListener: View = findViewById(R.id.rotate_ac)
        rotateACButtonListener.setOnClickListener(this)

        val pauseButtonListener: View = findViewById(R.id.pause)
        pauseButtonListener.setOnClickListener(this)

        val speedButtonListener: View = findViewById(R.id.difficulty)
        speedButtonListener.setOnClickListener(this)

        handler = Handler(Looper.getMainLooper())

        loop = object : Runnable {
            override fun run() {
                if (gameState!!.gameIsRunning) {
                    if (!gameState!!.pause) {
                        val success: Boolean = gameState!!.moveFallingTetraminoDown()
                        if (!success) {
                            gameState!!.paintTetramino(gameState!!.falling)
                            gameState!!.lineRemove()
                            gameState!!.pushNewTetramino(TetraminoType.getRandomTetramino())
                            if (gameState!!.score % 10 === 9 && delay >= delayLowerLimit) {
                                delay = delay / delayFactor + 1
                            }
                            gameState!!.incrementScore()
                        }
                        drawView!!.invalidate()
                        handler!!.postDelayed(this, delay.toLong())
                    } else {
                        handler!!.postDelayed(this, delay.toLong())
                    }
                } else {
                    pause!!.setText(R.string.start_new_game)
                }
            }
        }
        (loop as Runnable).run()
    }

    override fun onClick(action: View) {
        if (action === left) {
            gameState?.moveFallingTetraminoLeft()
        } else if (action === right) {
            gameState?.moveFallingTetraminoRight()
        } else if (action === rotateAc) {
            gameState?.rotateFallingTetraminoAntiClock()
        } else if (action === pause) {
            if (gameState?.gameIsRunning!!) {
                if (gameState!!.pause) {
                    gameState!!.pause = false
                    pause!!.setText(R.string.pause)
                } else {
                    pause!!.setText(R.string.play)
                    gameState!!.pause = true
                }
            } else {
                pause!!.setText(R.string.start_new_game)
                val intent = Intent(this@Game, MainActivity::class.java)
                startActivity(intent)
            }
        } else if (action === difficultyToggle) {
            if (!gameState?.difficultMode!!) {
                delay = delay / delayFactor
                gameState?.difficultMode = true
                difficultyToggle!!.setText(R.string.hard)
            } else {
                delay = delay * delayFactor
                difficultyToggle!!.setText(R.string.easy)
                gameState?.difficultMode = false
            }
        }
    }








}