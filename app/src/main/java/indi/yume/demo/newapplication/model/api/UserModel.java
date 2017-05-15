package indi.yume.demo.newapplication.model.api;

import lombok.Data;

/**
 * Created by sashiro on 16/5/9.
 */

@Data
public class UserModel {
    private String name;
    private String username;
    private String token;
    private float point;
    private float money;
    private int staffId;
    private String status;
}
