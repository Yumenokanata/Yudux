package indi.yume.yudux.functions;

/**
 * Created by yume on 17-4-27.
 */

public interface Function<T, R> {
    R apply(T t);
}
