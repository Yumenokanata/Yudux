package indi.yume.demo.newapplication.ui.fragment.other;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import indi.yume.demo.newapplication.R;
import indi.yume.demo.newapplication.databinding.OtherFragmentBinding;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.MainApplication;
import indi.yume.demo.newapplication.ui.component.DaggerOtherComponent;
import indi.yume.demo.newapplication.ui.component.OtherComponent;
import indi.yume.demo.newapplication.ui.AppState;
import indi.yume.demo.newapplication.ui.fragment.base.BaseToolbarFragment;
import indi.yume.demo.newapplication.ui.module.OtherModule;
import indi.yume.demo.newapplication.ui.presenter.OtherPresenter;
import indi.yume.yudux.collection.ContextCollection;

import static indi.yume.demo.newapplication.ui.AppStore.mainStore;
import static indi.yume.demo.newapplication.ui.fragment.other.OtherFragment.OtherKey.*;
import static indi.yume.yudux.collection.DSL.*;
import static indi.yume.yudux.DSL.*;

/**
 * Created by DaggerGenerator on 2017-05-24.
 */
public class OtherFragment extends BaseToolbarFragment {
    enum OtherKey {
        CONTEXT,
        FRAGMENT,
        VIEW,
        BINDER,
        PRESENTER,
        HANDLER
    }

    private final ContextCollection<OtherKey> repo =
            ContextCollection.<OtherKey>builder()
                    .withItem(HANDLER,
                            depends(),
                            (real, repo) -> new OtherHandler(repo))
                    .withItem(BINDER,
                            depends(VIEW, HANDLER),
                            (real, repo) -> {
                                OtherFragmentBinding binding = OtherFragmentBinding.bind(real.getItem(VIEW));
                                binding.setHandler(real.getItem(HANDLER));

                                return binding;
                            })
                    .withItem(PRESENTER,
                            depends(CONTEXT),
                            (real, repo) -> {
                                AppComponent appComponent = MainApplication.getInstance(real.getItem(CONTEXT)).getAppComponent();
                                OtherComponent otherComponent = DaggerOtherComponent.builder()
                                        .appComponent(appComponent)
                                        .otherModule(new OtherModule(this))
                                        .build();
                                OtherPresenter presenter = otherComponent.getPresenter();
                                otherComponent.injectPresenter(presenter);

                                return presenter;
                            })
                    .build();

    {
        subscribeUntilChanged(
                mainStore,
                extra(repo, depends(BINDER)),
                s -> s.getOtherState(),
                (real, state) -> real.<OtherFragmentBinding>getItem(BINDER).setModel(state));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.other_fragment, container, false);
        repo.ready(VIEW, view);
        repo.ready(FRAGMENT, this);
        forceRender(mainStore);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        repo.ready(CONTEXT, context);
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        repo.destroy(CONTEXT);
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        repo.destroyAll();
        super.onDestroy();
    }

    @Override
    public void inject(AppComponent appComponent){

    }
}