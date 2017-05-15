package indi.yume.demo.newapplication.widget.slidemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import indi.yume.demo.newapplication.R;
import indi.yume.demo.newapplication.databinding.SlideMenuLayoutBinding;
import indi.yume.demo.newapplication.functions.Receiver;
import indi.yume.demo.newapplication.ui.activity.base.SlideMenuActivity;
import indi.yume.demo.newapplication.util.SlideMenuScreensTag;
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
//            case R.id.keep_item_view:
//                switchToStack(SlideMenuScreensTag.BUY_HISTORY);
//                break;
        }

        if (listener != null)
            listener.onClick(view);
    }

    public void switchToStack(SlideMenuScreensTag tag) {
        //取消侧边栏item选中状态
        binding.homeItemView.setSelected(false);
        binding.searchItemView.setSelected(false);
        binding.keepItemView.setSelected(false);
        activity.setEnableDrawer(true);

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
