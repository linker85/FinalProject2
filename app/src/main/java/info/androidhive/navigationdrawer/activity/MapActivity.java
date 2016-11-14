package info.androidhive.navigationdrawer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.fragment.MyMapFragment;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "MapActivityTAG_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Landscape
        MyMapFragment fragmentMap = new MyMapFragment();

        Intent intent = getIntent();

        String coordinates = intent.getStringExtra("coordinates");

        Bundle bundle = new Bundle();
        bundle.putString("coordinates", coordinates);
        fragmentMap.setArguments(bundle);

        FragmentTransaction mft = getSupportFragmentManager().beginTransaction();
        mft.replace(R.id.flDetailMap, fragmentMap);
        mft.commit();
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        finish();
        //moveTaskToBack(true);
    }
}
