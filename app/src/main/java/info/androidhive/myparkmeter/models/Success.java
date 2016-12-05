package info.androidhive.myparkmeter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Success {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("mensaje")
    @Expose
    private String mensaje;
    @SerializedName("result")
    @Expose
    private Integer result;
    @SerializedName("email")
    @Expose
    private String email;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}