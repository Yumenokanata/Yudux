package indi.yume.yudux

/**
 * Created by yume on 17-5-23.
 */

object Utils {
    @JvmStatic
    fun <T> plus(list: Iterable<T>, item: T): List<T> = list + item

    @JvmStatic
    fun <T> replace(list: List<T>, oldItem: T, newItem: T): List<T> =
            list.map { if(oldItem == it) newItem else it }
}