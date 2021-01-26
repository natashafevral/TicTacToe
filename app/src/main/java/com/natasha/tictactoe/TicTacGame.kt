package com.natasha.tictactoe

import android.util.Log
import android.widget.Button

class TicTacGame {

    private val cellsCount = 9
    private var isFirstPlayer = true
    private val cells: MutableMap<Int, Int> = mutableMapOf()
    private val TAG = "TIC TAC G"
    fun start() {
        cells.clear()
        isFirstPlayer = true
        for (i in 0 until cellsCount) {
            cells[i] = 0
        }
    }

    fun getCells() = cells

    fun TicTacTap(i: Int): List<Int> {
      //  Log.d(TAG, " TAP / is FirstPlayer? $isFirstPlayer")
        if (isFirstPlayer) {
            cells[i] = 1
        } else {
            cells[i] = 2
        }
        var listWinner = checkWinner()
        if (listWinner.isNotEmpty()) return listWinner
        isFirstPlayer = !isFirstPlayer
        return listWinner
    }

    private fun checkWinner(): List<Int> {
        val i = 0
        for (j in 0 .. 2) {
            val x1 = i + j * 3
            val x2 = x1 + 1
            val x3 = x1 + 2
       //     Log.d(TAG,"checkWinner on j ")
            if (checkCellsForWinner(x1, x2, x3)) {
                return listOf(x1,x2,x3)
            }
            val y1 = i + j
            val y2 = y1 + 3
            val y3 = y1 + 6
            if (checkCellsForWinner(y1, y2, y3)) {
                return listOf(y1,y2,y3)
            }
        }
      //  Log.d(TAG,"checkWinner on z diag ")
        val z2 = i + 4
        val z3 = i + 8
        if (checkCellsForWinner(i, z2, z3)) {
            return listOf(i,z2,z3)
        }
      //  Log.d(TAG,"checkWinner on q diag")
        val q1 = i + 2
        val q2 = q1 + 2
        val q3 = q1 + 4
        if (checkCellsForWinner(q1, q2, q3))  {
            return listOf(q1,q2,q3)
        }
        return listOf()
    }

    private fun checkCellsForWinner(i1: Int, i2: Int, i3: Int): Boolean {
        //Log.d(TAG, " I am in checkCellsForWinner")
        val cellx1 = cells[i1]
        val cellx2 = cells[i2]
        val cellx3 = cells[i3]
       // Log.d(TAG, " I am in checkCellsForWinner $cellx1  $cellx2 $cellx3" )
        if (isMoreThenZero(cellx1!!, cellx2!!, cellx3!!)) {
         //   Log.d(TAG, "checkWinner on x : $cellx1 , $cellx2 , $cellx3 on x: $i1 $i2 $i3")
            if (checkEqualsCells(cellx1, cellx2, cellx3)) {
             //   Log.d(TAG, " winner on x: $i1 $i2 $i3")
                return true
            }
        }
        return false
    }

    private fun isMoreThenZero(vararg el: Int) = el.filter {it > 0}.size == el.size

    private fun checkEqualsCells(x1: Int, x2: Int, x3: Int) = ((x1 == x2) && (x1 == x3) && (x2 == x3))

    fun isFirstPlayer() = isFirstPlayer
}