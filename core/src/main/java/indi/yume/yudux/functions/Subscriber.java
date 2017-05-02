package indi.yume.yudux.functions;

/**
 * Created by yume on 17-4-24.
 */
public interface Subscriber<S> {
    void onStateChange(S state);
}