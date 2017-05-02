package indi.yume.yudux.functions;

/**
 * Created by yume on 17-4-27.
 */
public interface BiSubscriber<S, T> {
    void onStateChange(S state, T t);
}
