package info.androidhive.myparkmeter.other;

/**
 * Created by raul on 10/11/2016.
 */

public class UpdateMapEvent3 {
    public final String coordinates;
    public final String title;
    public final String body;

    public UpdateMapEvent3(String coordinates, String title, String body) {
        this.coordinates = coordinates;
        this.title       = title;
        this.body        = body;
    }
}
