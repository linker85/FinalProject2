package info.androidhive.navigationdrawer.models;

import com.orm.SugarRecord;

/**
 * Created by linke_000 on 30/10/2016.
 */

public class Notification extends SugarRecord {
    private String title;
    private String body;
    private String date;
    private String email;
    private int    remaining;

    public Notification() {
    }

    public Notification(String title, String body, String date) {
        this.title = title;
        this.body = body;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
}
