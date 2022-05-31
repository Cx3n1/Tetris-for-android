package course.android.tetris

import java.util.*

enum class TetraminoType{
    SQUARE,
    LINE,
    T_SHAPE,
    Z_SHAPE,
    INV_Z_SHAPE,
    L_SHAPE,
    INV_L_SHAPE;


    private val VALUES: Array<TetraminoType> = values()
    private val SIZE = VALUES.size
    private val RANDOM = Random()

    open fun getRandomTetramino(): TetraminoType? {
        return VALUES[RANDOM.nextInt(SIZE)]
    }
}

class Tetramino {

    var blocks : BasicBlock[]
    var type : TetraminoType = null

    constructor(type: TetraminoType, tetraId: Int) {
        val coordinates: Array<Coordinate>
        when (type) {
            TetraminoType.SQUARE -> {
                coordinates = arrayOf<Coordinate>(
                    Coordinate(0, 10),
                    Coordinate(1, 10),
                    Coordinate(1, 11),
                    Coordinate(0, 11)
                )
                blocks = blocksGenerator(tetraId, 1, coordinates)
            }
            TetraminoType.INV_L_SHAPE -> {
                coordinates = arrayOf<Coordinate>(
                    Coordinate(0, 10),
                    Coordinate(0, 11),
                    Coordinate(1, 10),
                    Coordinate(2, 10)
                )
                blocks = blocksGenerator(tetraId, 2, coordinates)
            }
            TetraminoType.L_SHAPE -> {
                coordinates = arrayOf<Coordinate>(
                    Coordinate(0, 11),
                    Coordinate(0, 10),
                    Coordinate(1, 11),
                    Coordinate(2, 11)
                )
                blocks = blocksGenerator(tetraId, 3, coordinates)
            }
            TetraminoType.T_SHAPE -> {
                coordinates = arrayOf<Coordinate>(
                    Coordinate(1, 10),
                    Coordinate(0, 10),
                    Coordinate(1, 11),
                    Coordinate(2, 10)
                )
                blocks = blocksGenerator(tetraId, 4, coordinates)
            }
            TetraminoType.Z_SHAPE -> {
                coordinates = arrayOf<Coordinate>(
                    Coordinate(1, 11),
                    Coordinate(1, 10),
                    Coordinate(0, 10),
                    Coordinate(2, 11)
                )
                blocks = blocksGenerator(tetraId, 5, coordinates)
            }
            TetraminoType.INV_Z_SHAPE -> {
                coordinates = arrayOf<Coordinate>(
                    Coordinate(1, 11),
                    Coordinate(0, 11),
                    Coordinate(1, 10),
                    Coordinate(2, 10)
                )
                blocks = blocksGenerator(tetraId, 6, coordinates)
            }
            TetraminoType.LINE -> {
                coordinates = arrayOf<Coordinate>(
                    Coordinate(0, 10),
                    Coordinate(1, 10),
                    Coordinate(2, 10),
                    Coordinate(3, 10)
                )
                blocks = blocksGenerator(tetraId, 7, coordinates)
            }
        }
    }

    private constructor(blocks: Array<BasicBlock?>) {
        this.blocks = blocks
    }

    private fun blocksGenerator(
        tetraId: Int,
        colour: Int,
        coordinates: Array<Coordinate>
    ): Array<BasicBlock?> {
        val blocks = arrayOfNulls<BasicBlock>(coordinates.size)
        for (itr in coordinates.indices) {
            blocks[itr] =
                BasicBlock(colour, tetraId, coordinates[itr], BasicBlockState.ON_TETRAMINO)
        }
        return blocks
    }

    fun copy(tetraId: Int): Tetramino {
        val newBlocks = arrayOfNulls<BasicBlock>(blocks.size)
        for (itr in blocks.indices) {
            newBlocks[itr] = blocks[itr].copy()
            newBlocks[itr].tetraId = tetraId
        }
        return Tetramino(newBlocks)
    }

    fun moveDown() {
        for (block in blocks) {
            block.coordinate.y++
        }
    }

    fun moveLeft() {
        for (block in blocks) {
            block.coordinate.x--
        }
    }

    fun moveRight() {
        for (block in blocks) {
            block.coordinate.x++
        }
    }

    fun performClockWiseRotation() {
        val referenceBlock = blocks[0]
        for (block in blocks) {
            val baseCoordinate: Coordinate =
                Coordinate.sub(block.coordinate, referenceBlock.coordinate)
            block.coordinate = Coordinate.add(
                Coordinate.rotateAntiClock(baseCoordinate),
                referenceBlock.coordinate
            )
        }
    }

}