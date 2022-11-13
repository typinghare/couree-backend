package couree.com.luckycat.constant;

import com.google.gson.annotations.SerializedName;

public enum JSendStatus {
    @SerializedName("success")
    SUCCESS,

    @SerializedName("fail")
    FAIL,   // the request fails because there is one or more problem with the request

    @SerializedName("error")
    ERROR   // the request fails because there is some problem with the server
}
