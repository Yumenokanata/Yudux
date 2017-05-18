package indi.yume.yudux.functions;

/**
 * Created by yume on 17-5-17.
 */
public interface Subscriber3<S, T1, T2> {
    void onStateChange(S state, T1 t1, T2 t2);
}
