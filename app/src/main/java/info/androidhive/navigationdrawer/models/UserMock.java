package info.androidhive.navigationdrawer.models;

import com.orm.SugarRecord;

/**
 * Created by raul on 05/11/2016.
 */

public class UserMock extends SugarRecord {
    private String email;
    private String password;
    private String name;
    private String plate;

     /**
     *
     * @return
     *     The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     *     The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     *     The password
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     *     The password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @return
     *     The password
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    @Override
    public String toString() {
        return "UserMock{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", plate='" + plate + '\'' +
                '}';
    }
}