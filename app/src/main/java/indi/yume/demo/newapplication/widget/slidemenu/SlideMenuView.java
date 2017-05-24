package indi.yume.demo.newapplication.widget.slidemenu;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import indi.yume.demo.newapplication.R;
import indi.yume.demo.newapplication.component.slidemenu.SlideMenuActions;
import indi.yume.demo.newapplication.databinding.SlideMenuLayoutBinding;
import indi.yume.demo.newapplication.functions.Receiver;
import indi.yume.demo.newapplication.ui.activity.base.SlideMenuActivity;
import indi.yume.demo.newapplication.ui.fragment.qrscan.QrScanFragment;
import indi.yume.demo.newapplication.util.SlideMenuScreensTag;
import indi.yume.tools.fragmentmanager.StartBuilder;
import lombok.Setter;

/**
 * Created by yume on 16/1/15.
 */
public class SlideMenuView extends FrameLayout {

    SlideMenuLayoutBinding binding;

    private SlideMenuActivity activity;
    @Setter
    private OnClickListener listener;

    public SlideMenuView(Context context) {
        this(context, null);
    }

    public SlideMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.slide_menu_layout, this, false);
        addView(view);

        binding = SlideMenuLayoutBinding.bind(view);

        if (!isInEditMode())
            activity = (SlideMenuActivity) context;

        binding.homeItemView.setSelected(true);

        Receiver<View> hanlder = this::onMenuItemClick;
        binding.setHandler(hanlder);
    }

    void onMenuItemClick(View view) {
        switch (view.getId()) {
            case R.id.home_item_view:
                switchToStack(SlideMenuScreensTag.HOME);
                break;
            case R.id.search_item_view:
                switchToStack(SlideMenuScreensTag.SEARCH);
                break;
            case R.id.keep_item_view:
                switchToStack(SlideMenuScreensTag.KEEP);
                break;
            case R.id.my_page_item_view:
                switchToStack(SlideMenuScreensTag.MY_PAGE);
                break;
            case R.id.cart_item_view:
                switchToStack(SlideMenuScreensTag.CART);
                break;
            case R.id.qr_item_view:
                switchToStack(SlideMenuScreensTag.QR);
                break;
        }

        if (listener != null)
            listener.onClick(view);
    }

    public void switchToStack(SlideMenuScreensTag tag) {
        //取消侧边栏item选中状态
        binding.homeItemView.setSelected(false);
        binding.searchItemView.setSelected(false);
        binding.keepItemView.setSelected(false);
        binding.myPageItemView.setSelected(false);
        binding.cartItemView.setSelected(false);
        binding.qrItemView.setSelected(false);
        SlideMenuActions.enableDrawer(true);

        //设置侧边栏item选中状态以及切换fragment
        switch (tag) {
            case HOME:
                binding.homeItemView.setSelected(true);
                activity.switchToStackByTag(SlideMenuScreensTag.HOME.getTag());
                break;
            case SEARCH:
                binding.searchItemView.setSelected(true);
                activity.switchToStackByTag(SlideMenuScreensTag.SEARCH.getTag());
                break;
            case KEEP:
                binding.keepItemView.setSelected(true);
                activity.switchToStackByTag(SlideMenuScreensTag.KEEP.getTag());
                break;
            case MY_PAGE:
                binding.myPageItemView.setSelected(true);
                activity.switchToStackByTag(SlideMenuScreensTag.MY_PAGE.getTag());
                break;
            case CART:
                binding.cartItemView.setSelected(true);
                activity.switchToStackByTag(SlideMenuScreensTag.CART.getTag());
                break;
            case QR:
                StartBuilder.builder(new Intent(activity, QrScanFragment.class))
                        .withEnterAnim(R.anim.fragment_left_enter)
                        .withExitAnim(R.anim.fragment_left_exit)
                        .start(activity);
                break;
        }
    }


    public interface OnClickListener {
        void onClick(View view);
    }

    public interface ProviderSlideMenu {
        void closeSlideMenu();

        void openSlideMenu();

        void toggleSlidingMenu();
    }
}
