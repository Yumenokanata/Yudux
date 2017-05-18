package indi.yume.yudux.functions;

/**
 * Created by yume on 17-5-17.
 */
public interface Subscriber4<S, T1, T2, T3> {
    void onStateChange(S state, T1 t1, T2 t2, T3 t3);
}
