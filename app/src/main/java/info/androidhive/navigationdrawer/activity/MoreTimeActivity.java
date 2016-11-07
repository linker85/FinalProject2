package info.androidhive.navigationdrawer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_time);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Extend time ");

        mHandler = new Handler();

        Observable<CheckinMock> resultisRegisteredObservable = LoginRetrofitHelper.
                Factory.createIsRegistered("581deb6b0f0000702a02daee"); // user

        resultisRegisteredObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CheckinMock>() {

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                        loadStep2Fragment(false);
                    }

                    @Override
                    public void onNext(CheckinMock result) {
                        boolean isExtend = false;
                        /// Simulation of getting users from backend + from the database to simulate the registry of users
                        ////////////////////////////// Simulation of cheking if the user has checkin
                        SharedPreferences sharedPref = getApplicationContext().
                                getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
                        String email = sharedPref.getString("email", "");
                        List<CheckinMock> checkinMockList = CheckinMock.findWithQuery(
                                CheckinMock.class, "SELECT * FROM CHECKIN_MOCK WHERE EMAIL=?", email);
                        if (checkinMockList != null && !checkinMockList.isEmpty()) {
                            result.setResult(1);
                        } else {
                            result.setResult(0);
                        }
                        /////////////////////////////////////////////////////////////////////////////
                        isExtend = (result.getResult() == 1);
                        loadStep2Fragment(isExtend);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            //onBackPressed();
            this.finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}