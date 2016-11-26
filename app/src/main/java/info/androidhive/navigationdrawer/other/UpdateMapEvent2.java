package info.androidhive.navigationdrawer.other;

/**
 * Created by raul on 10/11/2016.
 */

public class UpdateMapEvent2 {
    public final String coordinates;
    public final String title;
    public final String body;

    public UpdateMapEvent2(String coordinates, String title, String body) {
        this.coordinates = coordinates;
        this.title       = title;
        this.body        = body;
    }
}
