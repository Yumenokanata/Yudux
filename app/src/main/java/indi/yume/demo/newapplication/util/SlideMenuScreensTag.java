package indi.yume.demo.newapplication.util;

import indi.yume.demo.newapplication.ui.fragment.cart.CartFragment;
import indi.yume.demo.newapplication.ui.fragment.home.HomeFragment;
import indi.yume.demo.newapplication.ui.fragment.keep.KeepFragment;
import indi.yume.demo.newapplication.ui.fragment.mypage.MyPageFragment;
import indi.yume.demo.newapplication.ui.fragment.other.OtherFragment;
import indi.yume.demo.newapplication.ui.fragment.qrscan.QrScanFragment;
import indi.yume.demo.newapplication.ui.fragment.search.SearchFragment;
import indi.yume.tools.fragmentmanager.BaseManagerFragment;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by yume on 16/1/18.
 * <p>
 * Constants class for provide fragment when switch screen by slide menu.
 */
@AllArgsConstructor
public enum SlideMenuScreensTag {
    /**
     * Home界面
     */
    HOME("home", HomeFragment.class),
    /**
     * 检索界面
     */
    SEARCH("search",SearchFragment.class),
    /**
     * 收藏界面
     */
    KEEP("keep", KeepFragment.class),
    /**
     * 我的小卖部界面
     */
    MY_PAGE("my_page", MyPageFragment.class),
    /**
     * 购物车界面
     */
    CART("cart", CartFragment.class),
    /**
     * QR界面
     */
    QR("qr", QrScanFragment.class),
    /**
     * 其他界面
     */
    OTHER("other", OtherFragment.class);

    @Getter
    private String tag;
    @Getter
    private Class<? extends BaseManagerFragment> fragmentClazz;
}
