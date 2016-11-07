package info.androidhive.navigationdrawer.models;

import com.orm.SugarRecord;

/**
 * Created by raul on 06/11/2016.
 */

public class CheckinMock extends SugarRecord {
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
