package vn.com.rfim_mobile.models.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseMessage {

    @SerializedName("code")
    @Expose
    private Object code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("devMessage")
    @Expose
    private Object devMessage;

    public Object getCode() {
        return code;
    }

    public void setCode(Object code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getDevMessage() {
        return devMessage;
    }

    public void setDevMessage(Object devMessage) {
        this.devMessage = devMessage;
    }
}
