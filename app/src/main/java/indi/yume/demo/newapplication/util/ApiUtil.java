package indi.yume.demo.newapplication.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.annimon.stream.Optional;

import java.io.IOException;
import java.net.UnknownHostException;

import indi.yume.demo.newapplication.R;
import indi.yume.demo.newapplication.manager.api.base.NetErrorException;
import indi.yume.demo.newapplication.model.api.CommonModel;
import kotlin.Pair;
import lombok.experimental.UtilityClass;
import lombok.val;

/**
 * Created by yume on 16-7-20.
 */
@UtilityClass
public class ApiUtil {

    @Nullable
    public static CommonModel getCommonModel(Throwable t) {
        return Optional.ofNullable(t)
                .select(NetErrorException.class)
                .map(NetErrorException::getErrorModel)
                .orElse(null);
    }

    /**
     * Check is offline error.
     */
    public static boolean isOfflineError(Throwable t) {
        return t instanceof UnknownHostException || t instanceof IOException;
    }

    public static Pair<String, String> errorMessage(Context context,
                                                    Throwable t) {
        if(t instanceof NetErrorException) {
            return getApiError(context, ((NetErrorException) t).getErrorModel());
        } else if(t instanceof UnknownHostException) {
            return new Pair(context.getString(R.string.error_network_title),
                    context.getString(R.string.network_error));
        } else if(t instanceof IOException) {
            return new Pair(context.getString(R.string.error_network_title),
                    context.getString(R.string.network_error));
        } else {
            return new Pair(context.getString(R.string.error_normal_title),
                    t.getMessage());
        }
    }

    private static Pair<String, String> getApiError(Context context,
                                                      CommonModel commonModel) {
        return new Pair(getApiErrorTitle(context, commonModel),
                getApiErrorMessage(context, commonModel));
    }

    private static String getApiErrorTitle(Context context, CommonModel commonModel) {
        if (commonModel == null || TextUtils.isEmpty(commonModel.getTitle()))
            return context.getString(R.string.error_network_title);

        return commonModel.getTitle();
    }

    private static String getApiErrorMessage(Context context,
                                             CommonModel commonModel) {
        if (commonModel == null
                || TextUtils.isEmpty(commonModel.getMessage()))
            return context.getString(R.string.network_error);

        return commonModel.getMessage();
    }
}
