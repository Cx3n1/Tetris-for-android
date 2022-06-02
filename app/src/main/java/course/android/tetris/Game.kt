package course.android.tetris

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import course.android.tetris.data.TetraminoType
import java.util.zip.Inflater


class Game : /*Fragment(R.layout.fragment_game),*/AppCompatActivity(), View.OnClickListener {

    //TODO: This needs to be discussed
    companion object{
        /***
         * standard board size is 10x20
         */
        val BOARD_ROWS = 20
        val BOARD_COLUMNS = 10

        fun getMiddleReference(): Int{
            return BOARD_COLUMNS/2; //integer division is intentional
        }
    }

    var drawView: DrawView? = null
    var gameState: GameState? = null
    var left: ImageButton? = null
    var right: ImageButton? = null
    var rotateAc: ImageButton? = null
    var game: RelativeLayout? = null
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
        setContentView(R.layout.activity_testris);

        gameState = GameState(BOARD_ROWS, BOARD_COLUMNS, TetraminoType.getRandomTetramino())

        //TODO how to add draw view inside ftagment
        drawView = DrawView(this, gameState!!)

        var  param: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(850,1300);

        drawView!!.layoutParams = param;

        drawView!!.setBackgroundColor(Color.rgb(224, 251, 252))

        score = findViewById(R.id.txtv_score);

        game = findViewById(R.id.main);
        game!!.setBackgroundColor(Color.rgb(61, 90, 128))

        delay = 500
        delayLowerLimit = 200
        delayFactor = 2


        game!!.addView(drawView)

        left = findViewById(R.id.btn_left)
        right = findViewById(R.id.btn_right)
        rotateAc = findViewById(R.id.btn_rotate)


       /* left = Button(this)
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
*/


        left!!.setOnClickListener(this)

        right!!.setOnClickListener(this)

        rotateAc!!.setOnClickListener(this)

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
                            //TODO:Send new score to main thread to display
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