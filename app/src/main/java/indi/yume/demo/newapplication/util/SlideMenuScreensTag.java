package indi.yume.demo.newapplication.util;

import indi.yume.demo.newapplication.ui.fragment.home.HomeFragment;
import indi.yume.demo.newapplication.ui.fragment.keep.KeepFragment;
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
    KEEP("keep", KeepFragment.class);
//    /**
//     * 我的小卖部界面
//     */
//    MY_PAGE("my_page", D01_01_RechargeFragment.class),
//    /**
//     * 购物车界面
//     */
//    CART("cart", E01_01_UserInfoFragment.class),
//    /**
//     * QR界面
//     */
//    QR("qr", A00_01_LoginFragment.class),
//    /**
//     * 其他界面
//     */
//    OTHER("other", A00_01_LoginFragment.class);

    @Getter
    private String tag;
    @Getter
    private Class<? extends BaseManagerFragment> fragmentClazz;
}
