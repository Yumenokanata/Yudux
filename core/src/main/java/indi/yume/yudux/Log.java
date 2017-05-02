package indi.yume.yudux;

import static indi.yume.yudux.collection.ExceptionKt.isInnerException;

/**
 * Created by yume on 17-4-24.
 */
public class Log {
    static final long startTime = System.currentTimeMillis();
    private static boolean isOpen = true;

    public static void d(String TAG, String message) {
        if (!isOpen)
            return;

        long time = System.currentTimeMillis() - startTime;
        System.out.println(String.format("thread: %3d | time:%6d | %s : %s", Thread.currentThread().getId(), time, TAG, message));
    }

    public static void e(String TAG, String message, Throwable throwable) {
        if (!isOpen)
            return;

        long time = System.currentTimeMillis() - startTime;
        System.out.println(String.format("thread: %3d | time:%6d |Error= %s : %s %s", Thread.currentThread().getId(), time, TAG, message, throwable.getMessage()));
        if(!isInnerException(throwable))
            throwable.printStackTrace();
    }
}
