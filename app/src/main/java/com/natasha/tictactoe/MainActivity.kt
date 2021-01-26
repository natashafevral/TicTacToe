package com.natasha.tictactoe

import android.graphics.Point
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.view.setMargins
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat

import com.natasha.tictactoe.databinding.ActivityMainBinding
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val cellsCount = 9
    private var cellSize: Int = 210
    private val gridSize = 3
    private val cellMargin = R.dimen.cell_margin
    private val cellColor = R.color.light_gray
    private val cellTextColor = R.color.teal_700
    private val cellTextSize = R.dimen.cell_text_size

    private val firstPlayerTitle = "First Player"
    private val secondPlayerTitle = "Second Player"
    private val TAG = "MAIN ACti"

    private val cells: ArrayList<ImageButton> = ArrayList(cellsCount)
    private lateinit var ticTac: TicTacGame
    private lateinit var ticTacCells: Map<Int, Int>
    private var lineImage: ImageView? = null
    private var drawableLine: Drawable? = null
    private lateinit var btnLayoutParams: LinearLayout.LayoutParams


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.gridLayout.columnCount = gridSize

        cellSize = resources.getDimension(R.dimen.cell_size).toInt()
        btnLayoutParams = LinearLayout.LayoutParams(cellSize,cellSize)
        btnLayoutParams.setMargins(15)
        drawableLine = ContextCompat.getDrawable(this, R.drawable.avd_line)


        binding.restartButton.setOnClickListener{ startGame() }
        startGame()

    }




    private fun startGame() {
        /* Clear*/
        binding.gameLayout.removeView(lineImage)
        binding.gridLayout.removeAllViews()
        cells.clear()
        lineImage = null
        /* Clear*/

        ticTac = TicTacGame()
        ticTac.start()
        ticTacCells = ticTac.getCells()
        binding.numberOfPlayer.text = firstPlayerTitle
        createImageLine()

        for (i in 0 until cellsCount) {
            val btn = layoutInflater.inflate(R.layout.button_cell,  null, false) as ImageButton
            btn.layoutParams = btnLayoutParams

            btn.setOnClickListener {
               checkWinner(btn, i)
            }
            cells.add(btn)
            binding.gridLayout.addView(cells[i],i)
        }
    }

    //Listener for Cell Button
    private fun checkWinner(btn: ImageButton, i: Int) {
        val listWinner = ticTac.TicTacTap(i)
        val valueOfCell = setSymbol(ticTacCells[i] ?: 0)
        btn.contentDescription = valueOfCell.first
        btn.setImageDrawable(valueOfCell.second)
        startAnimation(btn.drawable)

        val isFirst = ticTac.isFirstPlayer()

        if (listWinner.isNotEmpty()) {
            val winnerText = "${if (isFirst) firstPlayerTitle else secondPlayerTitle} WIN"
            binding.numberOfPlayer.text = winnerText
            startLineAnim(listWinner)
            incrementScore(isFirst)
            stopGame()
        } else {
            binding.numberOfPlayer.text = if (isFirst) firstPlayerTitle else secondPlayerTitle
        }
        btn.setOnClickListener(null)
    }


    private fun createImageLine() {
        lineImage = ImageView(this)
        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        lineImage?.apply {
            this.setImageDrawable(drawableLine)
            this.layoutParams = layoutParams
            this.tag = "img_test"
            this.id = R.id.blue_line
            this.visibility = View.INVISIBLE
            this.scaleY = 1.3F
        }
        layoutParams.gravity = Gravity.CENTER
        binding.gameLayout.addView(lineImage)
    }

    private fun getScreenSize()
    {
        var display = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            this.baseContext.display
        } else {
            windowManager.defaultDisplay
        }
        var size = Point()
        display?.getRealSize(size)

        var dWidth = size.x
        var dHeight = size.y
        Log.d(TAG, "SIZe of Display width: $dWidth and height: $dHeight")

    }

    private fun defineDirectionLine(list: List<Int>) : Pair<Int, Int> {
        if (list.size > 2) {
            val horiz = gridSize - 1
            val vertic = gridSize * (gridSize - 1)
            val diagForward = gridSize * gridSize - 1
            val diagBackward = (gridSize - 1) * (gridSize - 1)
           // Log.d(TAG, "in DEFine Line type LIne: $horiz, $vertic, $diagForward, $diagBackward")
            //Log.d(TAG, "define Line list0: ${list[0]} ,list1: ${list[1]} ,list2: ${list[2]}")
            var typeLine: Int = 0
            var levelLine: Int = 0
            val diffItems = list.last() - list.first()
            when (diffItems) {
                horiz -> {
                    typeLine = 0
                    levelLine = list.first() / gridSize
                }
                vertic -> {
                    typeLine = 1
                    levelLine = list.first()
                }
                diagForward -> {
                    typeLine = 2
                }
                diagBackward -> {
                    typeLine = 3
                }
            }
            return Pair(typeLine, levelLine)
        }
        return Pair(-1, -1)
    }

    private fun startLineAnim(list: List<Int>) {
        val imageView: ImageView = lineImage!!

        val lParams = FrameLayout.LayoutParams(imageView.width, imageView.height)

        val gridWidth = cellSize * gridSize // (gridSize = 3 (countColumn)) (cellSize = 210 (dp))
        val gridWidthReal = binding.gridLayout.width
        val gridHeight = binding.gridLayout.height
        Log.d(TAG, "grid width $gridWidth lineimage width ${imageView.width} gridHeight $gridHeight")
        var scaleLineX = 0F

        val linePair = defineDirectionLine(list)
        val typeLine = linePair.first
        val levelLine = linePair.second.toFloat()

        if (typeLine != -1 && levelLine != -1F) {
            when (typeLine) {
                0 -> {      // horizontal
                    imageView.translationY = levelLine * (gridHeight / gridSize) //cellSize
                    lParams.gravity = (Gravity.TOP or Gravity.CENTER)
                    lParams.topMargin = cellSize / 2
                }
                1 -> {       //vertical
                    imageView.rotation = 90F
                    imageView.translationX = (-1 + levelLine) * (gridWidthReal / gridSize) //cellSize
                    lParams.gravity = (Gravity.CENTER)
                }
                2 -> {       //diag Front
                    imageView.rotation = 45F
                }
                3 -> {      //diag Back
                    imageView.rotation = -45F
                }
            }
            if (typeLine == 0 || typeLine == 1) {
                scaleLineX = (gridWidth.toFloat() / imageView.width)
            } else {
                val c = sqrt((gridWidth * gridWidth) * 2F).toInt()
                scaleLineX = c.toFloat() / imageView.width
                lParams.gravity = (Gravity.CENTER)
            }
            Log.d(TAG, "scale X $scaleLineX")
            imageView.scaleX = scaleLineX
            imageView.layoutParams = lParams
            imageView.visibility = View.VISIBLE
            startAnimation(imageView.drawable)

        }
    }

    private fun startAnimation(drawable: Drawable) {
        if (drawable is AnimatedVectorDrawableCompat) {
            val avd: AnimatedVectorDrawableCompat = drawable
            avd.start()
        } else if (drawable is AnimatedVectorDrawable) {
            val avd: AnimatedVectorDrawable = drawable
            avd.start()
        }
    }

    private fun stopGame() {
        binding.gridLayout.forEach { it.setOnClickListener(null) }
    }

    private fun incrementScore(isFirst: Boolean) {
        lateinit var winnerPlayerView: TextView
        if (isFirst) {
            winnerPlayerView = binding.scoreFirstPlayer
        } else {
            winnerPlayerView = binding.scoreSecondPlayer
        }
        val scoreWinner = winnerPlayerView.text.toString()
        val scoreInt = scoreWinner.toInt() + 1
        winnerPlayerView.text = scoreInt.toString()
    }

    private fun setSymbol(i: Int): Pair<String, Drawable?> {
        return when (i) {
            1 -> {
                val dr = ContextCompat.getDrawable(this, R.drawable.avd_cross)
                Pair("X", dr)
            }
            2 -> {
                val dr = ContextCompat.getDrawable(this, R.drawable.avd_zero)
                Pair("O", dr)
            }
            else -> Pair("_", null)
        }
    }

}