package indi.yume.demo.newapplication.model.api;

import lombok.Data;

/**
 * Created by yume on 16-4-12.
 */
@Data
public class StatusModel {
    private String status = "OK";
    private String errorMsg = "";
}
