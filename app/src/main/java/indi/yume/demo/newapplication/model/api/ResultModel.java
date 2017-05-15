package indi.yume.demo.newapplication.model.api;

import lombok.Data;

/**
 * Created by xiejinpeng on 16/5/25.
 */

@Data
public class ResultModel {

    public final static String STATUS_OK = "OK";
    public final static String STATUS_NG = "NG";

    private String status;
    private String errorMsg;
}
