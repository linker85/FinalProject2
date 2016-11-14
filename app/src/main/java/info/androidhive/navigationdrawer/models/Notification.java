package info.androidhive.navigationdrawer.models;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by linke_000 on 30/10/2016.
 */

public class Notification extends SugarRecord {
    private String title;
    private String body;
    private Date dateS;
    private String email;
    private int    remaining;
    private String coordinates;

    public Notification() {
    }

    public Notification(String title, String body, Date date) {
        this.title = title;
        this.body = body;
        this.dateS = date;
    }

    public Notification(String title, String body, Date dateS, String coordinates) {
        this.title       = title;
        this.body        = body;
        this.dateS       = dateS;
        this.coordinates = coordinates;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDateS() {
        return dateS;
    }

    public void setDateS(Date dateS) {
        this.dateS = dateS;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", dateS='" + dateS + '\'' +
                ", email='" + email + '\'' +
                ", remaining=" + remaining +
                ", coordinates='" + coordinates + '\'' +
                '}';
    }
}