package indi.yume.demo.newapplication.model.api;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

/**
 * Created by sashiro on 16/5/13.
 */
@Data
public class UserModel implements Parcelable {
    private String name;
    private String username;
    private int staffId;
    private String token;
    private float point;
    private float money;

    protected UserModel(Parcel in) {
        name = in.readString();
        username = in.readString();
        staffId = in.readInt();
        token = in.readString();
        point = in.readFloat();
        money = in.readFloat();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.username);
        dest.writeInt(this.staffId);
        dest.writeString(this.token);
        dest.writeFloat(this.point);
        dest.writeFloat(this.money);
    }
}
