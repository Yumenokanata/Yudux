package indi.yume.yudux.functions

/**
 * Created by yume on 17-4-27.
 */

sealed class Result<R, L> {
    abstract val isRight: Boolean

    abstract val isFail: Boolean

    abstract val right: R?

    abstract val fail: L?

    companion object {
        @JvmStatic
        fun <R, L> right(right: R): Result<R, L> = Right(right)

        @JvmStatic
        fun <R, L> fail(fail: L): Result<R, L> = Fail(fail)
    }
}

class Right<R, L>(override val right: R) : Result<R, L>() {
    override val isRight: Boolean
        get() = true

    override val isFail: Boolean
        get() = false

    override val fail: L?
        get() = null
}

class Fail<R, L>(override val fail: L) : Result<R, L>() {
    override val isRight: Boolean
        get() = false

    override val isFail: Boolean
        get() = true

    override val right: R?
        get() = null
}
