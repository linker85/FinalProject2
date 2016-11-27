package info.androidhive.navigationdrawer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.onesignal.OneSignal;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.fragment.HomeFragment;
import info.androidhive.navigationdrawer.fragment.NotificationsFragment;
import info.androidhive.navigationdrawer.fragment.RegPayFragment;
import info.androidhive.navigationdrawer.fragment.SettingsFragment;
import info.androidhive.navigationdrawer.models.CheckinMock;
import info.androidhive.navigationdrawer.retrofit_helpers.LoginRetrofitHelper;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityTAG_";
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    //private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    // urls to load navigation header background image
    // and profile image
    //private static final String urlNavHeaderBg = "http://api.androidhive.info/images/nav-menu-header-bg.jpg";
    //private static final String urlProfileImg  = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String ID_HOME          = "id_home";
    private static final String ID_MORE_TIME     = "id_more_time";
    private static final String ID_SIGN_OFF      = "id_sign_off";
    private static final String ID_NOTIFICATIONS = "id_notifications";
    private static final String ID_SETTINGS      = "id_settings";
    private static final String ID_REG_PAY       = "id_reg_pay";
    public static String CURRENT_TAG             = ID_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: MainActivity");

        // Stetho
        Stetho.initializeWithDefaults(this);

        // Set current activity
        SharedPreferences sharedPref = getApplicationContext().
                getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("currentActivity", "main");
        editor.commit();

        // One Signal subscription
        OneSignal.setSubscription(true);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String newUserId, String registrationId) {
                checkIfWeHaveUserId(newUserId);
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //fab = (FloatingActionButton) findViewById(R.id.fab);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName        = (TextView)  navHeader.findViewById(R.id.name);
        txtWebsite     = (TextView)  navHeader.findViewById(R.id.website);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = ID_HOME;
            loadHomeFragment();
        }
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        txtWebsite.setText("Hello");
        try {
            SharedPreferences sharedPref = getApplicationContext().
                    getSharedPreferences("my_park_meter_pref",
                            Context.MODE_PRIVATE);
            txtName.setText(sharedPref.getString("email", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //txtWebsite.setText("www.androidhive.info");

        // loading header background image
        /*Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);*/

        // Loading profile image
        /*Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);*/

        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu.
     * Loads the fragment returned from getHomeFragment() function into FrameLayout.
     * It also takes care of other things like changing the toolbar title, hiding / showing fab,
     * invalidating the options menu so that new menu can be loaded for different fragment.
     */
    private void loadHomeFragment() {
        clearTempCheckin();
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            //toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                if (navItemIndex == 0) {
                    ////////////////////////////// Simulation of cheking if the user has checkin
                    SharedPreferences sharedPref = getApplicationContext().
                            getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
                    String email = sharedPref.getString("email", "");
                    Observable<CheckinMock> resultisRegisteredObservable = LoginRetrofitHelper.
                            Factory.createHasCheckIn(email); // user

                    resultisRegisteredObservable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<CheckinMock>() {
                                @Override
                                public void onStart() {
                                    Log.d(TAG, "onStart: ");
                                }

                                @Override
                                public void onCompleted() {
                                    Log.d(TAG, "onCompleted: ");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.d(TAG, "onError: " + e.getMessage());
                                    Toast.makeText(getApplicationContext(), "An error occurred.", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onNext(CheckinMock result) {
                                    boolean isCheckout = false;
                                    if (!result.isSuccess()) {
                                        Toast.makeText(getApplicationContext(), result.getMensaje(), Toast.LENGTH_LONG).show();
                                    } else {
                                        Bundle bundle = new Bundle();
                                        isCheckout = (result.getResult() == 1);
                                        bundle.putBoolean("isCheckout", isCheckout);
                                        // update the main content by replacing fragments
                                        Fragment fragment = getHomeFragment();
                                        if (navItemIndex == 0) {
                                            fragment.setArguments(bundle);
                                        }
                                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                                android.R.anim.fade_out);
                                        fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                                        fragmentTransaction.commitAllowingStateLoss();
                                    }
                                }
                            });
                    return;
                } else if (navItemIndex == 4) {
                    // 1. Signup, 2. Remember, 3. settings
                    bundle.putInt("settingsFragment", 3);
                }
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                if (navItemIndex == 0) {
                    fragment.setArguments(bundle);
                }
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        //toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    /*
    * Returns the appropriate Fragment depending on the nav menu item user selected.
    * For example if user selects Photos from nav menu, it returns PhotosFragment.
    * This can be done by using the variable navItemIndex.
    * */
    private Fragment getHomeFragment() {
        clearTempCheckin();
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            /*case 1:
                // more time
                PhotosFragment moreTimeFragment = new PhotosFragment();
                return moreTimeFragment;*/
            case 2:
                // register payment fragment
                RegPayFragment registerPaymentFragment = new RegPayFragment();
                return registerPaymentFragment;
            case 3:
                // notifications fragment
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;
            case 4:
                // settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            /*case 5:
                // sign off
                return new HomeFragment();*/
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    /*
    * Initializes the Navigation Drawer by creating necessary click listeners and other functions.
    * */
    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                clearTempCheckin();
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.id_home:
                        navItemIndex = 0;
                        CURRENT_TAG = ID_HOME;
                        break;
                    case R.id.id_reg_pay:
                        navItemIndex = 2;
                        CURRENT_TAG = ID_REG_PAY;
                        break;
                    case R.id.id_notifications:
                        navItemIndex = 3;
                        CURRENT_TAG = ID_NOTIFICATIONS;
                        break;
                    case R.id.id_settings:
                        navItemIndex = 4;
                        CURRENT_TAG = ID_SETTINGS;
                        break;
                    case R.id.id_sign_off:
                        // Remove preferences from shared
                        SharedPreferences sharedPref = getSharedPreferences(
                                "my_park_meter_pref", Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.commit();
                        // No more notifications
                        OneSignal.setSubscription(false);
                        finish();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        // clear activity stack
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        drawer.closeDrawers();
                        break;
                    case R.id.id_more_time:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, MoreTimeActivity.class));
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        clearTempCheckin();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = ID_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
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
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            // clear activity stack
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            drawer.closeDrawers();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearTempCheckin() {
        // Remove preferences from shared
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                "my_park_meter_pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("checkin_temp");
        editor.commit();
    }

    // show or hide the fab
    /*private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }*/

    // Check if we have userId
    public void checkIfWeHaveUserId(String newUserId) {
        SharedPreferences sharedPref = getApplicationContext().
                getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
        String email = sharedPref.getString("email", "");
        String userId = sharedPref.getString("userId", "");
        Observable<CheckinMock> resultisRegisteredObservable = LoginRetrofitHelper.
                Factory.createUpdateUserId(email, newUserId); // user

        if (!newUserId.equals(userId)) {
            resultisRegisteredObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<CheckinMock>() {
                @Override
                public void onStart() {
                    Log.d(TAG, "onStart: ");
                }

                @Override
                public void onCompleted() {
                    Log.d(TAG, "onCompleted: ");
                }

                @Override
                public void onError(Throwable e) {
                    Log.d(TAG, "onError: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "An error occurred.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNext(CheckinMock result) {
                    Log.d(TAG, "new userId: " + result.isSuccess());
                }
            });
        }
    }

}