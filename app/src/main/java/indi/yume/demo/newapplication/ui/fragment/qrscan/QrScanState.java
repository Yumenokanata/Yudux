package indi.yume.demo.newapplication.ui.fragment.qrscan;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import indi.yume.demo.newapplication.R;
import indi.yume.demo.newapplication.model.api.GoodsModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;

/**
 * Created by yume on 2017-05-23.
 */
@Data
@AllArgsConstructor
public class QrScanState {
    public static final int Status_None = 0;
    public static final int Status_Ok = R.string.scan_searching_succuss;
    public static final int Status_Loading = R.string.scan_searching;
    public static final int Status_Error = R.string.api_error_network_error;
    public static final int Status_Not_Found = R.string.api_error_find_no_goods;

    @IntDef({Status_Ok, Status_Loading, Status_Error, Status_Not_Found})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status{}

    @Wither
    private final boolean isLoading;
    @Wither
    private final boolean cameraIsRunning;
    @Wither
    @Nullable
    private final String barCode;
    @Wither
    @Nullable
    private final GoodsModel currentItem;
    @Wither
    @StringRes
    @Status
    private final int status;

    public QrScanState() {
        this(false, false, "", null, Status_None);
    }
}