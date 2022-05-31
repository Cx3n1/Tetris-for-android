package course.android.tetris

class Coordinate {
    var y: Int = 0
    var x: Int = 0

    constructor(y: Int, x: Int) {
        this.y = y
        this.x = x
    }

    companion object {
        fun add(A: Coordinate, B: Coordinate): Coordinate {
            return Coordinate(A.y + B.y, A.x + B.x)
        }


        fun sub(A: Coordinate, B: Coordinate): Coordinate {
            return Coordinate(A.y - B.y, A.x - B.x)
        }

        fun rotateAntiClock(X: Coordinate): Coordinate {
            return Coordinate(-X.x, X.y)
        }

        fun isEqual(A: Coordinate, B: Coordinate): Boolean {
            return A.y == B.y && A.x == B.x
        }
    }
}