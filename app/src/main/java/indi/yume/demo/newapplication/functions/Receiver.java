package indi.yume.demo.newapplication.functions;

import android.support.annotation.NonNull;

/**
 * Created by yume on 17-5-3.
 */
public interface Receiver<T> {

    /**
     * Accepts the given {@code value}.
     */
    void accept(@NonNull T value);
}
