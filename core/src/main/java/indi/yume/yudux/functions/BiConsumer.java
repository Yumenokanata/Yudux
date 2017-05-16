package indi.yume.yudux.functions;

/**
 * Created by yume on 17-5-16.
 */

public interface BiConsumer<T1, T2> {

    /**
     * Performs an operation on the given values.
     * @param t1 the first value
     * @param t2 the second value
     */
    void accept(T1 t1, T2 t2);
}