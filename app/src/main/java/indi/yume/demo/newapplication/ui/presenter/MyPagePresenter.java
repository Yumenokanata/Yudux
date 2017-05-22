package indi.yume.demo.newapplication.ui.presenter;


import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import indi.yume.demo.newapplication.manager.api.ChargeHistoryService;
import indi.yume.demo.newapplication.manager.api.PayHistoryService;
import indi.yume.demo.newapplication.model.api.ChargeModel;
import indi.yume.demo.newapplication.model.api.PayHistoryModel;
import io.reactivex.Single;

/**
 * Created by DaggerGenerator on 2017/05/22.
 */
public class MyPagePresenter extends BasePresenter{
    @Inject
    PayHistoryService payHistoryService;
    @Inject
    ChargeHistoryService chargeHistoryService;

    public Single<PayHistoryModel> getPayHistory(String token) {
        return payHistoryService.getPayHistory(token, null, null, null, null);
    }

    public Single<ChargeModel> getChargeHistory(String token) {
        Calendar calendar = Calendar.getInstance();
        return chargeHistoryService.getPayHistory(token,
                formatDateToServiceType(2017, 1, 1),
                formatDateToServiceType(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH)),
                0,
                0);
    }

    public static String formatDateToServiceType(int year, int monthOfYear, int dayOfMonth) {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d-00:00", year, monthOfYear , dayOfMonth);
    }
}