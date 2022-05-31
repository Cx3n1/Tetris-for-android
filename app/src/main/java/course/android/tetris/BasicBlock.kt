package course.android.tetris

enum class BasicBlockState {
    ON_EMPTY, ON_TETRAMINO
}

internal class BasicBlock {
    var colour: Int
    var tetraId: Int
    var coordinate: Coordinate
    var state: BasicBlockState

    constructor(row: Int, column: Int) {
        colour = -1
        tetraId = -1
        coordinate = Coordinate(row, column)
        state = BasicBlockState.ON_EMPTY
    }

    constructor(colour: Int, tetraId: Int, coordinate: Coordinate, state: BasicBlockState) {
        this.colour = colour
        this.tetraId = tetraId
        this.coordinate = coordinate
        this.state = state
    }

    fun copy(): BasicBlock {
        return BasicBlock(colour, tetraId, coordinate, state)
    }

    fun set(B: BasicBlock) {
        colour = B.colour
        tetraId = B.tetraId
        coordinate.y = B.coordinate.y
        coordinate.x = B.coordinate.x
        state = B.state
    }

    fun setEmptyBlock(coordinate: Coordinate) {
        colour = -1
        tetraId = -1
        this.coordinate.x = coordinate.x
        this.coordinate.y = coordinate.y
        state = BasicBlockState.ON_EMPTY
    }
}