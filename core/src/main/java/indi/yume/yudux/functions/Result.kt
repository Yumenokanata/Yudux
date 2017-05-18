package indi.yume.yudux.functions

/**
 * Created by yume on 17-4-27.
 */

sealed class Result<out R, out L> {
    abstract val isRight: Boolean

    abstract val isLeft: Boolean

    abstract val right: R?

    abstract val left: L?

    companion object {
        @JvmStatic
        fun <R, L> right(right: R): Result<R, L> = Right(right)

        @JvmStatic
        fun <R, L> left(fail: L): Result<R, L> = Left(fail)

        fun <R, L, T> Result<R, L>.map(f: (R) -> T): Result<T, L> =
                when(this) {
                    is Right -> right(f(right))
                    is Left -> left(left)
                }

        fun <R, L, T> Result<R, L>.mapLeft(f: (L) -> T): Result<R, T> =
                when(this) {
                    is Right -> right(right)
                    is Left -> left(f(left))
                }

        fun <R, L, T> Result<R, L>.flatMap(f: (R) -> Result<T, L>): Result<T, L> =
                when(this) {
                    is Right -> f(right)
                    is Left -> left(left)
                }

        fun <R, L, T> Result<R, L>.flatMapLeft(f: (L) -> Result<R, T>): Result<R, T> =
                when(this) {
                    is Right -> right(right)
                    is Left -> f(left)
                }

        fun <R, L, R1, T> Result<R, L>.map2(result: Result<R1, L>, f: (R, R1) -> T): Result<T, L> =
                flatMap { r -> result.map { r1 -> f(r, r1) } }

        fun <R, L, R1, L1, R2, L2> Result<R, L>.map2(result: Result<R1, L1>,
                                                     rightF: (R, R1) -> R2,
                                                     leftF: (L?, L1?) -> L2): Result<R2, L2> =
                when {
                    this is Right && result is Right -> right(rightF(right, result.right))
                    this is Left && result is Left -> left(leftF(left, result.left))
                    else -> left(leftF(left, result.left))
                }
    }
}

class Right<out R, out L>(override val right: R) : Result<R, L>() {
    override val isRight: Boolean
        get() = true

    override val isLeft: Boolean
        get() = false

    override val left: L?
        get() = null
}

class Left<out R, out L>(override val left: L) : Result<R, L>() {
    override val isRight: Boolean
        get() = false

    override val isLeft: Boolean
        get() = true

    override val right: R?
        get() = null
}
