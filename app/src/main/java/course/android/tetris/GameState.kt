package course.android.tetris

import android.util.SparseArray
import course.android.tetris.data.*


class GameState(
    private val rows: Int,
    private val columns: Int,
    fallingTetraminoType: TetraminoType?
) {
    var gameIsRunning = true
    var score = 0
    var pause = false
    var board: Array<Array<BasicBlock?>>
    var falling: Tetramino
    var difficultMode = false //remove this
    private var createdTetraminoIndex = 0
    private val tetraminos: SparseArray<Tetramino>

    private fun getBlockOnBoardOn(coordinate: Coordinate): BasicBlock? {
        return board[coordinate.y][coordinate.x]
    }

    private fun isConflicting(coordinate: Coordinate): Boolean {
        return if (coordinate.x < 0 || coordinate.x >= columns || coordinate.y < 0 || coordinate.y >= rows)
            true //if its outside of bounds of board
        else //else if there there is conflict between to tetramino blocks
            getBlockOnBoardOn(coordinate)!!.state === BasicBlockState.ON_TETRAMINO
    }

    private fun canTetraminoDisplace(tetramino: Tetramino, displacement: Coordinate): Boolean {
        for (block in tetramino.blocks) {
            if (block!!.state === BasicBlockState.ON_TETRAMINO) {
                val shifted: Coordinate = Coordinate.add(block!!.coordinate, displacement)
                if (isConflicting(shifted)) {
                    return false
                }
            }
        }
        return true
    }

    fun moveFallingTetraminoDown(): Boolean {
        return if (canTetraminoDisplace(falling, Coordinate(1, 0))) {
            falling.moveDown()
            true
        } else {
            false
        }
    }

    fun moveFallingTetraminoLeft(): Boolean {
        return if (canTetraminoDisplace(falling, Coordinate(0, -1))) {
            falling.moveLeft()
            true
        } else {
            false
        }
    }

    fun moveFallingTetraminoRight(): Boolean {
        return if (canTetraminoDisplace(falling, Coordinate(0, 1))) {
            falling.moveRight()
            true
        } else {
            false
        }
    }

    fun rotateFallingTetraminoAntiClock(): Boolean {
        return if (falling.type === TetraminoType.SQUARE) {
            true
        } else {
            for (block in falling.blocks) {
                if (block!!.state === BasicBlockState.ON_EMPTY) continue
                val referenceBlock: BasicBlock? = falling.blocks.get(0)

                val baseCoordinate: Coordinate =
                    Coordinate.subtract(block!!.coordinate, referenceBlock!!.coordinate)

                if (isConflicting(
                        Coordinate.add(
                            Coordinate.rotateAntiClock(baseCoordinate),
                            referenceBlock!!.coordinate
                        )
                    )
                ) {
                    return false
                }
            }
            falling.performClockWiseRotation()
            true
        }
    }

    fun paintTetramino(tetramino: Tetramino) {
        for (block in tetramino.blocks) {
            if (block!!.state === BasicBlockState.ON_EMPTY) continue
            getBlockOnBoardOn(block!!.coordinate)!!.set(block)
        }
    }

    fun pushNewTetramino(tetraminoType: TetraminoType?) {
        createdTetraminoIndex++

        falling = Tetramino(tetraminoType!!, createdTetraminoIndex)

        tetraminos.put(createdTetraminoIndex, falling)

        //if new tetramino hits other tetramino straight up then game has ended
        for (block in falling.blocks) {
            if (getBlockOnBoardOn(block!!.coordinate)!!.state === BasicBlockState.ON_TETRAMINO) gameIsRunning =
                false
        }
    }

    fun incrementScore() {
        score++
    }

    fun lineRemove() {
        var removeLines: Boolean
        do {
            removeLines = false
            for (row in rows - 1 downTo 0) {
                var rowIsALine = true
                for (column in 0 until columns) {
                    if (board[row][column]!!.state !== BasicBlockState.ON_TETRAMINO) {
                        rowIsALine = false
                        break
                    }
                }
                if (!rowIsALine) {
                    continue
                }
                for (column in 0 until columns) {
                    val tetramino = tetraminos[board[row][column]!!.tetraId]
                    val blockToClear = board[row][column]
                    blockToClear!!.setEmptyBlock(blockToClear.coordinate)
                    if (tetramino == null) {
                        continue
                    }
                    for (block in tetramino.blocks) {
                        if (block!!.state === BasicBlockState.ON_EMPTY) {
                            continue
                        }
                        if (block!!.coordinate.y === row && block!!.coordinate.x === column) {
                            block!!.state = BasicBlockState.ON_EMPTY
                            createdTetraminoIndex++
                            val upperTetramino = tetramino.copy(createdTetraminoIndex)
                            tetraminos.put(createdTetraminoIndex, upperTetramino)
                            for (upperBlock in upperTetramino.blocks) {
                                if (upperBlock!!.coordinate.y >= block.coordinate.y) {
                                    upperBlock.state = BasicBlockState.ON_EMPTY
                                } else {
                                    getBlockOnBoardOn(upperBlock.coordinate)!!.tetraId =
                                        upperBlock.tetraId
                                }
                            }
                            createdTetraminoIndex++
                            val lowerTetramino = tetramino.copy(createdTetraminoIndex)
                            tetraminos.put(createdTetraminoIndex, lowerTetramino)
                            for (lowerBlock in lowerTetramino.blocks) {
                                if (lowerBlock!!.coordinate.y <= block.coordinate.y) {
                                    lowerBlock.state = BasicBlockState.ON_EMPTY
                                } else {
                                    getBlockOnBoardOn(lowerBlock.coordinate)!!.tetraId =
                                        lowerBlock.tetraId
                                }
                            }
                            tetraminos.remove(block.tetraId)
                            break
                        }
                    }
                }
                adjustTheMatrix()
                incrementScore()
                removeLines = true
                break
            }
        } while (removeLines)
    }

    private fun adjustTheMatrix() {
        for (row in rows - 1 downTo 0) {
            for (column in 0 until columns) {
                val T = tetraminos[board[row][column]!!.tetraId]
                if (T != null) shiftTillBottom(T)
            }
        }
    }

    private fun shiftTillBottom(tetramino: Tetramino) {
        var shiftTillBottom: Boolean
        do {
            var shouldShiftDown = true
            shiftTillBottom = false
            for (block in tetramino.blocks) {
                if (block!!.state === BasicBlockState.ON_EMPTY) continue
                val newCoordinate: Coordinate = Coordinate.add(block!!.coordinate, Coordinate(1, 0))
                if (isTetraPresent(newCoordinate, tetramino)) continue
                if (isConflicting(newCoordinate)) shouldShiftDown = false
            }
            if (shouldShiftDown) {
                for (block in tetramino.blocks) {
                    if (block!!.state === BasicBlockState.ON_EMPTY) continue
                    getBlockOnBoardOn(block!!.coordinate)!!.setEmptyBlock(block.coordinate)
                    block.coordinate.y++
                }
                for (block in tetramino.blocks) {
                    if (block!!.state === BasicBlockState.ON_EMPTY) continue
                    getBlockOnBoardOn(block!!.coordinate)!!.set(block)
                }
                shiftTillBottom = true
            }
        } while (shiftTillBottom)
    }

    private fun isTetraPresent(coordinate: Coordinate, tetramino: Tetramino): Boolean {
        for (block in tetramino.blocks) {
            if (block!!.state === BasicBlockState.ON_EMPTY) continue
            if (Coordinate.isEqual(block!!.coordinate, coordinate)) return true
        }
        return false
    }

    init {
        board = Array(rows) { arrayOfNulls(columns) }
        for (row in 0 until rows) {
            for (column in 0 until columns) {
                board[row][column] = BasicBlock(row, column)
            }
        }
        tetraminos = SparseArray()
        falling = Tetramino(fallingTetraminoType!!, createdTetraminoIndex)
        tetraminos.put(createdTetraminoIndex, falling)
    }
}