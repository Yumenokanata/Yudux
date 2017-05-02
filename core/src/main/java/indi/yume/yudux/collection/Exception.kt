package indi.yume.yudux.collection

/**
 * Created by yume on 17-4-28.
 */

class DependsNotReadyException(depends: String) : Exception(String.format("Depends not ready: %s", depends))

fun isInnerException(e: Throwable): Boolean = e is DependsNotReadyException || e.cause is DependsNotReadyException