package course.android.tetris.data

enum class BasicBlockState {
    ON_EMPTY, ON_TETRAMINO
}

class BasicBlock {

    var color: Int
    /***
     * which block is it by index in full tetramino figure
     */
    var tetraId: Int
    var coordinate: Coordinate
    var state: BasicBlockState

    constructor(row: Int, column: Int) {
        color = -1
        tetraId = -1
        coordinate = Coordinate(row, column)
        state = BasicBlockState.ON_EMPTY
    }

    constructor(colour: Int, tetraId: Int, coordinate: Coordinate, state: BasicBlockState) {
        this.color = colour
        this.tetraId = tetraId
        this.coordinate = coordinate
        this.state = state
    }

    fun copy(): BasicBlock {
        return BasicBlock(color, tetraId, coordinate, state)
    }

    fun set(B: BasicBlock) {
        color = B.color
        tetraId = B.tetraId
        coordinate.y = B.coordinate.y
        coordinate.x = B.coordinate.x
        state = B.state
    }

    fun setEmptyBlock(coordinate: Coordinate) {
        color = -1
        tetraId = -1
        this.coordinate.x = coordinate.x
        this.coordinate.y = coordinate.y
        state = BasicBlockState.ON_EMPTY
    }
}