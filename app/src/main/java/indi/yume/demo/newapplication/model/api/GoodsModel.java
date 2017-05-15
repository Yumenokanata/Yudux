package indi.yume.demo.newapplication.model.api;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

/**
 * Created by yume on 16-4-12.
 */
@Data
public class GoodsModel implements Parcelable {
    private String name;
    private String barCode;
    private float salePrice;
    private float costPrice;
    private int count;
    private String unit;
    private int packageNum;
    private String note;
    private String className;

    protected GoodsModel(Parcel in) {
        name = in.readString();
        barCode = in.readString();
        salePrice = in.readFloat();
        costPrice = in.readFloat();
        count = in.readInt();
        unit = in.readString();
        packageNum = in.readInt();
        note = in.readString();
        className = in.readString();
    }

    public GoodsModel(){

    }

    public static final Creator<GoodsModel> CREATOR = new Creator<GoodsModel>() {
        @Override
        public GoodsModel createFromParcel(Parcel in) {
            return new GoodsModel(in);
        }

        @Override
        public GoodsModel[] newArray(int size) {
            return new GoodsModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.barCode);
        dest.writeFloat(this.salePrice);
        dest.writeFloat(this.costPrice);
        dest.writeInt(this.count);
        dest.writeString(this.unit);
        dest.writeInt(this.packageNum);
        dest.writeString(this.note);
        dest.writeString(this.className);
    }


}
