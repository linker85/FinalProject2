package info.androidhive.navigationdrawer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.fragment.SettingsFragment;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivityTAG_";

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Sign up");

        mHandler = new Handler();

        loadSignUpFragment();
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu.
     * Loads the fragment returned from getHomeFragment() function into FrameLayout.
     * It also takes care of other things like changing the toolbar title, hiding / showing fab,
     * invalidating the options menu so that new menu can be loaded for different fragment.
     */
    private void loadSignUpFragment() {
        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {

                Bundle bundle = new Bundle();
                bundle.putBoolean("isSignUp", true);

                // update the main content by replacing fragments
                Fragment fragment = new SettingsFragment();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame_sign_up, fragment, "id_sign_up");
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        this.finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        return true;
    }

}
