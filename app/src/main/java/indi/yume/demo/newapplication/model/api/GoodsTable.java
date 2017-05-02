package indi.yume.demo.newapplication.model.api;

import java.util.Date;

import lombok.Data;

/**
 * Created by sashiro on 16/5/16.
 */
@Data
public class GoodsTable {
    private long id;
    private String name;
    private String barCode;
    private Float salePrice;
    private Float costPrice;
    private Integer amount;
    private String unit;
    private Integer packageNum;
    private String note;
    private String className;
    private Date createDate;
    private Date updateDate;
}
