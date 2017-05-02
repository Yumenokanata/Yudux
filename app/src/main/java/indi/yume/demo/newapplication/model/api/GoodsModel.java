package indi.yume.demo.newapplication.model.api;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by yume on 16-4-12.
 */
@Data
@NoArgsConstructor
public class GoodsModel implements Parcelable {
    private String name;
    private String barCode;
    private float salePrice;
    private float costPrice;
    private int count = 0;
    private String unit;
    private int packageNum;
    private String note;
    private String className;

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

    protected GoodsModel(Parcel in) {
        this.name = in.readString();
        this.barCode = in.readString();
        this.salePrice = in.readFloat();
        this.costPrice = in.readFloat();
        this.count = in.readInt();
        this.unit = in.readString();
        this.packageNum = in.readInt();
        this.note = in.readString();
        this.className = in.readString();
    }

    public static final Parcelable.Creator<GoodsModel> CREATOR = new Parcelable.Creator<GoodsModel>() {
        @Override
        public GoodsModel createFromParcel(Parcel source) {
            return new GoodsModel(source);
        }

        @Override
        public GoodsModel[] newArray(int size) {
            return new GoodsModel[size];
        }
    };
}
