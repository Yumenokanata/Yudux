package indi.yume.demo.newapplication.manager.api.base;

import indi.yume.demo.newapplication.model.api.CommonModel;

import java.io.IOException;

import lombok.Getter;

/**
 * Created by yume on 16/11/15.
 */
public class NetErrorException extends IOException {
    @Getter
    private CommonModel errorModel;

    public NetErrorException(CommonModel errorModel){
        super(errorModel.getMessage());
        this.errorModel = errorModel;
    }
}
