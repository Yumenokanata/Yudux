package indi.yume.demo.newapplication.model.api;

import lombok.Data;
import lombok.ToString;

/**
 * Created by yume on 16/1/13.
 *
 * @author xuemao.tang
 */
@ToString
@Data
public class ErrorModel {

    /**
     * 　{"error":{"message":"リクエストされたオブジェクトが見つかりません。","status":"404","type":"1"}}
     *
     * message : リクエストされたオブジェクトが見つかりません。
     * status : 404
     * type : 1
     */

    private ErrorEntity error;

    @Data
    public static class ErrorEntity {
        private String message;
        private String status;
        private String type;
    }

    public String getErrorMessage(){
        if(error != null)
            return error.getMessage();
        else
            return null;
    }
}
