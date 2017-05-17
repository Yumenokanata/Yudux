package indi.yume.demo.newapplication.ui.fragment.keep;

import indi.yume.demo.newapplication.databinding.KeepFragmentBinding;
import indi.yume.demo.newapplication.ui.AppState;

import indi.yume.yudux.collection.BaseDependAction;
import indi.yume.yudux.collection.DependsStore;
import lombok.RequiredArgsConstructor;

import static indi.yume.demo.newapplication.ui.fragment.keep.KeepFragment.KeepKey.*;
import static indi.yume.yudux.DSL.*;

/**
 * Created by DaggerGenerator on 2017-05-16.
 */
@RequiredArgsConstructor
public class KeepHandler {
    private final DependsStore<KeepFragment.KeepKey, AppState> store;

}