package info.androidhive.myparkmeter.models;

import com.orm.SugarRecord;

/**
 * Created by raul on 05/11/2016.
 */

public class RegisteredMock extends SugarRecord {
    private Integer result;
    private String  email;

    /**
     *
     * @return
     * The result
     */
    public Integer getResult() {
        return result;
    }

    /**
     *
     * @param result
     * The result
     */
    public void setResult(Integer result) {
        this.result = result;
    }

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

}
