package indi.yume.demo.newapplication.ui.fragment.login;

import indi.yume.demo.newapplication.ui.fragment.AppState;
import io.reactivex.Observable;

/**
 * Created by yume on 17-4-24.
 */

public interface BaseAction<Real, State> {
    State reduce(State oldState);
    Observable<State> effect(Real realWorld, State oldState);
}
