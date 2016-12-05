package info.androidhive.myparkmeter.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.onesignal.OneSignal;

import info.androidhive.myparkmeter.R;
import info.androidhive.myparkmeter.fragment.MyMapFragment;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "MapActivityTAG_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set current activity
        SharedPreferences sharedPref = getApplicationContext().
                getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("currentActivity", "map");
        editor.commit();

        // Landscape
        MyMapFragment fragmentMap = new MyMapFragment();

        Intent intent = getIntent();

        String coordinates = intent.getStringExtra("coordinates");
        String title       = intent.getStringExtra("title");
        String body        = intent.getStringExtra("body");

        Bundle bundle = new Bundle();
        bundle.putString("coordinates", coordinates);
        bundle.putString("title", title);
        bundle.putString("body", body);
        fragmentMap.setArguments(bundle);

        FragmentTransaction mft = getSupportFragmentManager().beginTransaction();
        mft.replace(R.id.flDetailMap, fragmentMap);
        mft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            // Remove preferences from shared
            SharedPreferences sharedPref = getSharedPreferences(
                    "my_park_meter_pref", Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.commit();

            // No more notifications
            OneSignal.setSubscription(false);

            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
        //moveTaskToBack(true);
    }
}