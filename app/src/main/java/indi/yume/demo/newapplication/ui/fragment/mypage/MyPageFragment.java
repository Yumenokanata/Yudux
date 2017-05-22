package indi.yume.demo.newapplication.ui.fragment.mypage;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Stream;
import com.lsjwzh.widget.recyclerviewpager.TabLayoutSupport;

import java.util.List;

import indi.yume.demo.newapplication.BR;
import indi.yume.demo.newapplication.R;
import indi.yume.demo.newapplication.databinding.ListviewHistoryBinding;
import indi.yume.demo.newapplication.databinding.MyPageFragmentBinding;
import indi.yume.demo.newapplication.functions.Receiver;
import indi.yume.demo.newapplication.model.api.ChargeModel;
import indi.yume.demo.newapplication.model.api.GoodsModel;
import indi.yume.demo.newapplication.model.api.PayHistoryModel;
import indi.yume.demo.newapplication.ui.AppComponent;
import indi.yume.demo.newapplication.ui.MainApplication;
import indi.yume.demo.newapplication.ui.component.DaggerMyPageComponent;
import indi.yume.demo.newapplication.ui.component.MyPageComponent;
import indi.yume.demo.newapplication.ui.AppState;
import indi.yume.demo.newapplication.ui.fragment.base.BaseToolbarFragment;
import indi.yume.demo.newapplication.ui.module.MyPageModule;
import indi.yume.demo.newapplication.ui.presenter.MyPagePresenter;
import indi.yume.tools.dsladapter2.RendererAdapter;
import indi.yume.tools.dsladapter2.functions.CheckType;
import indi.yume.yudux.collection.ContextCollection;
import indi.yume.yudux.functions.Unit;

import static indi.yume.demo.databinding.DataBindingDsl.*;
import static indi.yume.demo.newapplication.ui.AppStore.mainStore;
import static indi.yume.demo.newapplication.ui.fragment.mypage.MyPageFragment.MyPageKey.*;
import static indi.yume.tools.dsladapter2.renderer.RenderDsl.*;
import static indi.yume.yudux.collection.DSL.*;
import static indi.yume.yudux.DSL.*;

/**
 * Created by DaggerGenerator on 2017-05-22.
 */
public class MyPageFragment extends BaseToolbarFragment {
    enum MyPageKey {
        CONTEXT,
        VIEW,
        BINDER,
        PRESENTER,
        PAGE_ADAPTER,
        TAB_ADAPTER,
        HANDLER,
        INIT
    }

    private final ContextCollection<MyPageKey> repo =
            ContextCollection.<MyPageKey>builder()
                    .withItem(HANDLER,
                            depends(),
                            (real, repo) -> new MyPageHandler(repo))
                    .withItem(BINDER,
                            depends(CONTEXT, VIEW, HANDLER, PAGE_ADAPTER, TAB_ADAPTER),
                            (real, repo) -> {
                                MyPageFragmentBinding binding = MyPageFragmentBinding.bind(real.getItem(VIEW));
                                binding.setHandler(real.getItem(HANDLER));

                                Context context = real.get(CONTEXT);
                                RendererAdapter adapter = real.get(PAGE_ADAPTER);
                                RecyclerView.LayoutManager mainManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                                adapter.setLayoutManager(mainManager);
                                binding.viewPager.setLayoutManager(mainManager);
                                binding.viewPager.setAdapter(adapter);

                                TabLayoutSupport.ViewPagerTabLayoutAdapter tabAdapter = real.get(TAB_ADAPTER);
                                TabLayoutSupport.setupWithViewPager(binding.myPageTabLayout, binding.viewPager, tabAdapter);

                                return binding;
                            })
                    .withItem(PRESENTER,
                            depends(CONTEXT),
                            (real, repo) -> {
                                AppComponent appComponent = MainApplication.getInstance(real.getItem(CONTEXT)).getAppComponent();
                                MyPageComponent myPageComponent = DaggerMyPageComponent.builder()
                                        .appComponent(appComponent)
                                        .myPageModule(new MyPageModule(this))
                                        .build();
                                MyPagePresenter presenter = myPageComponent.getPresenter();
                                myPageComponent.injectPresenter(presenter);

                                return presenter;
                            })
                    .withItem(TAB_ADAPTER,
                            depends(CONTEXT),
                            (real, repo) -> {
                                Context context = real.get(CONTEXT);
                                String[] titles = context.getResources().getStringArray(R.array.my_page_tab_title);
                                return new TabLayoutSupport.ViewPagerTabLayoutAdapter() {
                                    @Override
                                    public String getPageTitle(int i) {
                                        return titles[i];
                                    }

                                    @Override
                                    public int getItemCount() {
                                        return titles.length;
                                    }
                                };
                            })
                    .withItem(PAGE_ADAPTER,
                            depends(CONTEXT),
                            (real, repo) -> {
                                Context context = real.get(CONTEXT);
                                return new RendererAdapter<>(mainStore.getState().getMyPageState(),
                                        suppiler ->
                                                groupOf(MyPageState.class)
                                                        .add(s -> s.getHistory(),
                                                                rendererOf(CheckType.<List<PayHistoryModel.History>>type())
                                                                        .layout(R.layout.recycler_view_layout)
                                                                        .bindWith((l, v) -> {
                                                                            RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
                                                                            RendererAdapter hisAdapter = (RendererAdapter) recyclerView.getAdapter();
                                                                            if(hisAdapter != null) {
                                                                                hisAdapter.update(l);
                                                                            } else {
                                                                                RecyclerView.LayoutManager manager = new LinearLayoutManager(context);
                                                                                hisAdapter = new RendererAdapter<>(l,
                                                                                        sup -> list(PayHistoryModel.History.class)
                                                                                                .item(rendererOf(PayHistoryModel.History.class)
                                                                                                        .layout(R.layout.listview_history)
                                                                                                        .bindWith((his, view) -> {
                                                                                                            ListviewHistoryBinding binding = DataBindingUtil.bind(view);
                                                                                                            binding.setModel(his);
                                                                                                            binding.setHandler((Receiver<PayHistoryModel.History>) m -> {});

                                                                                                            RendererAdapter subAdapter = (RendererAdapter) binding.goodsListView.getAdapter();
                                                                                                            if(subAdapter != null) {
                                                                                                                subAdapter.update(his.getPayGoods());
                                                                                                            } else {
                                                                                                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
                                                                                                                subAdapter = new RendererAdapter<>(his.getPayGoods(),
                                                                                                                        s -> list(PayHistoryModel.PayGoods.class)
                                                                                                                                .item(dataBindingRepositoryPresenterOf(PayHistoryModel.PayGoods.class)
                                                                                                                                        .layout(R.layout.goods_view)
                                                                                                                                        .staticItemId(BR.model, p -> p.getGoodsData())
                                                                                                                                        .forItem())
                                                                                                                                .forList());
                                                                                                                subAdapter.setLayoutManager(layoutManager);
                                                                                                                binding.goodsListView.setLayoutManager(layoutManager);
                                                                                                                binding.goodsListView.setAdapter(subAdapter);
                                                                                                            }
                                                                                                        })
                                                                                                        .forItem())
                                                                                                .forList());
                                                                                hisAdapter.setLayoutManager(manager);
                                                                                recyclerView.setLayoutManager(manager);
                                                                                recyclerView.setAdapter(hisAdapter);
                                                                            }
                                                                        })
                                                                        .forItem())
                                                        .add(s -> s.getChargeHistory(),
                                                                rendererOf(CheckType.<List<ChargeModel.ChargeEntry>>type())
                                                                        .layout(R.layout.recycler_view_layout)
                                                                        .bindWith((l, v) -> {
                                                                            RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
                                                                            RendererAdapter hisAdapter = (RendererAdapter) recyclerView.getAdapter();
                                                                            if(hisAdapter != null) {
                                                                                hisAdapter.update(l);
                                                                            } else {
                                                                                RecyclerView.LayoutManager manager = new LinearLayoutManager(context);
                                                                                hisAdapter = new RendererAdapter<>(l,
                                                                                        sup -> list(ChargeModel.ChargeEntry.class)
                                                                                                .item(dataBindingRepositoryPresenterOf(ChargeModel.ChargeEntry.class)
                                                                                                        .layout(R.layout.recharge_list_item_layout)
                                                                                                        .itemId(BR.model)
                                                                                                        .forItem())
                                                                                                .forList());
                                                                                hisAdapter.setLayoutManager(manager);
                                                                                recyclerView.setLayoutManager(manager);
                                                                                recyclerView.setAdapter(hisAdapter);
                                                                            }
                                                                        })
                                                                        .forItem())
                                                        .forItem());
                            })
                    .withItem(INIT,
                            depends(CONTEXT, VIEW, BINDER, PRESENTER, PAGE_ADAPTER, HANDLER),
                            (real, repo) -> {
                                MyPageHandler handler = real.get(HANDLER);
                                handler.init();
                                return Unit.unit();
                            })
                    .build();

    {
        subscribeUntilChanged(
                mainStore,
                extra(repo, depends(BINDER)),
                s -> s.getUserModel(),
                (state, real) -> real.<MyPageFragmentBinding>getItem(BINDER).setUser(state));
        subscribeUntilChanged(
                mainStore,
                extra(repo, depends(BINDER)),
                s -> s.getMyPageState(),
                (state, real) -> real.<MyPageFragmentBinding>getItem(BINDER).setModel(state));
        subscribeUntilChanged(
                mainStore,
                extra(repo, depends(PAGE_ADAPTER)),
                s -> s.getMyPageState(),
                (state, real) -> real.<RendererAdapter>getItem(PAGE_ADAPTER).update(state));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.my_page_fragment, container, false);
        repo.ready(VIEW, view);
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