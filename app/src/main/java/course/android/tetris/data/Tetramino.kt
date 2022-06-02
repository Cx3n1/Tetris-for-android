package course.android.tetris.data

import android.util.Log
import course.android.tetris.Game
import java.util.*

enum class TetraminoType {
    SQUARE,
    LINE,
    T_SHAPE,
    Z_SHAPE,
    INV_Z_SHAPE,
    L_SHAPE,
    INV_L_SHAPE;

    companion object {

        private val VALUES: Array<TetraminoType> = values()
        private val SIZE = VALUES.size
        private val RANDOM = Random()


        open fun getRandomTetramino(): TetraminoType? {
            return VALUES[RANDOM.nextInt(SIZE)]
        }
    }
}

class Tetramino {

    var blocks: Array<BasicBlock?>
    var type: TetraminoType? = null //TODO: Do we need this?????
    private var boardMiddleReference = Game.getMiddleReference();

    constructor(type: TetraminoType, tetraId: Int) {
        blocks = when (type) {
            TetraminoType.LINE -> generateLine(tetraId)
            TetraminoType.SQUARE -> generateSquare(tetraId)
            TetraminoType.L_SHAPE -> generateLShape(tetraId)
            TetraminoType.T_SHAPE -> generateTShape(tetraId)
            TetraminoType.Z_SHAPE -> generateZShape(tetraId)
            TetraminoType.INV_L_SHAPE -> generateInverseLShape(tetraId)
            TetraminoType.INV_Z_SHAPE -> generateInverseZShape(tetraId)
        }
    }


    private constructor(blocks: Array<BasicBlock?>) {
        this.blocks = blocks
    }

    private fun blocksGenerator(tetraId: Int, colour: Int, coordinates: Array<Coordinate>
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
            newBlocks[itr] = blocks[itr]?.copy()
            newBlocks[itr]!!.tetraId = tetraId
        }

        return Tetramino(newBlocks)
    }

    fun moveDown() {
        for (block in blocks) {
            block!!.coordinate.y++
        }
    }

    fun moveLeft() {
        for (block in blocks) {
            block!!.coordinate.x--
        }
    }

    fun moveRight() {
        for (block in blocks) {
            block!!.coordinate.x++
        }
    }

    fun performClockWiseRotation() {
        val referenceBlock = blocks[0]

        for (block in blocks) {
            //basically we convert board coordinates to local coordinates of block (4x4 space)
            val localCoordinates: Coordinate =
                Coordinate.subtract(block!!.coordinate, referenceBlock!!.coordinate)

            block.coordinate = Coordinate.add(
                Coordinate.rotateAntiClock(localCoordinates),
                referenceBlock.coordinate
            )
        }
    }


    //**Utility**\\
    private fun generateLine(tetraId: Int) = blocksGenerator(
        tetraId, 7,
        arrayOf(
            Coordinate(0, boardMiddleReference),
            Coordinate(1, boardMiddleReference),
            Coordinate(2, boardMiddleReference),
            Coordinate(3, boardMiddleReference)
        )
    )

    private fun generateInverseZShape(tetraId: Int) = blocksGenerator(
        tetraId, 6,
        arrayOf(
            Coordinate(1, boardMiddleReference + 1),
            Coordinate(0, boardMiddleReference + 1),
            Coordinate(1, boardMiddleReference),
            Coordinate(2, boardMiddleReference)
        )
    )

    private fun generateZShape(tetraId: Int) = blocksGenerator(
        tetraId, 5,
        arrayOf(
            Coordinate(1, boardMiddleReference + 1),
            Coordinate(1, boardMiddleReference),
            Coordinate(0, boardMiddleReference),
            Coordinate(2, boardMiddleReference + 1)
        )
    )

    private fun generateTShape(tetraId: Int) = blocksGenerator(
        tetraId, 4,
        arrayOf(
            Coordinate(1, boardMiddleReference),
            Coordinate(0, boardMiddleReference),
            Coordinate(1, boardMiddleReference + 1),
            Coordinate(2, boardMiddleReference)
        )
    )

    private fun generateLShape(tetraId: Int) = blocksGenerator(
        tetraId, 3,
        arrayOf(
            Coordinate(0, boardMiddleReference + 1),
            Coordinate(0, boardMiddleReference),
            Coordinate(1, boardMiddleReference + 1),
            Coordinate(2, boardMiddleReference + 1)
        )
    )

    private fun generateInverseLShape(tetraId: Int) = blocksGenerator(
        tetraId, 2,
        arrayOf(
            Coordinate(0, boardMiddleReference),
            Coordinate(0, boardMiddleReference + 1),
            Coordinate(1, boardMiddleReference),
            Coordinate(2, boardMiddleReference)
        )
    )

    private fun generateSquare(tetraId: Int): Array<BasicBlock?> {
        return this.blocksGenerator(
            tetraId, 1,
            arrayOf(
                Coordinate(0, boardMiddleReference),
                Coordinate(1, boardMiddleReference),
                Coordinate(1, boardMiddleReference + 1),
                Coordinate(0, boardMiddleReference + 1)
            )
        )
    }



}