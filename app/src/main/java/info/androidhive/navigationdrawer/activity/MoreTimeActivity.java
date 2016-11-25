package info.androidhive.navigationdrawer.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.onesignal.OneSignal;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.fragment.TutorialStep2;
import info.androidhive.navigationdrawer.models.CheckinMock;
import info.androidhive.navigationdrawer.retrofit_helpers.LoginRetrofitHelper;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MoreTimeActivity extends AppCompatActivity {

    private static final String TAG = "MoreTimeActivityTAG_";
    private Handler mHandler;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_time);

        // Set current activity
        SharedPreferences sharedPref = getApplicationContext().
                getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("currentActivity", "moreTime");
        editor.commit();
        String email = "";
        try {
            email = sharedPref.getString("email", "");
        } catch (Exception e) {}

        progressDialog = new ProgressDialog(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Extend time");

        mHandler = new Handler();

        Observable<CheckinMock> resultisRegisteredObservable = LoginRetrofitHelper.
                Factory.createHasCheckIn(email); // user

        resultisRegisteredObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CheckinMock>() {
                    @Override
                    public void onStart() {
                        Log.d(TAG, "onStart: ");
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                        try {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                        } catch(Exception exception) {
                            exception.printStackTrace();
                        }
                        loadStep2Fragment(false);
                    }

                    @Override
                    public void onNext(CheckinMock result) {
                        boolean isExtend = false;
                        if (!result.isSuccess()) {
                            Toast.makeText(getApplicationContext(), result.getMensaje(), Toast.LENGTH_LONG).show();
                        } else {
                            isExtend = (result.getResult() == 1);
                            loadStep2Fragment(isExtend);
                        }
                        try {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                });
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu.
     * Loads the fragment returned from getHomeFragment() function into FrameLayout.
     * It also takes care of other things like changing the toolbar title, hiding / showing fab,
     * invalidating the options menu so that new menu can be loaded for different fragment.
     */
    private void loadStep2Fragment(final boolean isExtended) {
        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {

                Bundle bundle = new Bundle();
                bundle.putBoolean("isExtend", isExtended);

                // update the main content by replacing fragments
                Fragment fragment = new TutorialStep2();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame_more_time, fragment, "id_more_time");
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
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

}