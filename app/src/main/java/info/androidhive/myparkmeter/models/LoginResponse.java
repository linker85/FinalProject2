package info.androidhive.myparkmeter.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by raul on 21/11/2016.
 */

public class LoginResponse {
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("mensaje")
    @Expose
    private String mensaje;

    /**
     *
     * @return
     * The user
     */
    public User getUser() {
        return user;
    }

    /**
     *
     * @param user
     * The user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     *
     * @return
     * The success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     *
     * @param success
     * The success
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     *
     * @return
     * The mensaje
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     *
     * @param mensaje
     * The mensaje
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}