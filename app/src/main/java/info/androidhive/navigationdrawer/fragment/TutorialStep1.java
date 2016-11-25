package info.androidhive.navigationdrawer.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.codepond.wizardroid.WizardStep;
import org.codepond.wizardroid.persistence.ContextVariable;
import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.models.Success;
import info.androidhive.navigationdrawer.other.UpdateStepperEvent;
import info.androidhive.navigationdrawer.retrofit_helpers.SaveApiRetroFitHelper;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * Created by raul on 30/10/2016.
 */

public class TutorialStep1 extends WizardStep {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private static final String TAG = "TutorialStep1TAG_";
    @BindView(R.id.scanner)
    public Button scannerBTN;
    /**
     * Tell WizarDroid that these are context variables.
     * These values will be automatically bound to any field annotated with {@link ContextVariable}.
     * NOTE: Context Variable names are unique and therefore must
     * have the same name wherever you wish to use them.
     */
    @ContextVariable
    private boolean isCheckout;

    //Wire the layout to the step
    public TutorialStep1() {
    }

    //Set your layout here
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.step_check_in, container, false);
        ButterKnife.bind(this, view);

        if (!isCheckout) {
            scannerBTN.setText("Check in");
        } else {
            scannerBTN.setText("Check out");
        }

        scannerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //start the scanning activity from the com.google.zxing.client.android.SCAN intent
                    Intent intent = new Intent(ACTION_SCAN);
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, 0);
                } catch (ActivityNotFoundException anfe) {
                    //on catch, show the download dialog
                    showDialog(getActivity(), "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
                }
            }
        });

        return view;
    }

    private ProgressDialog progressDialog;
    //on ActivityResult method
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //get the extras that are returned from the intent
                final String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                Log.d("TAG", "onActivityResult: " + isCheckout);

                progressDialog = new ProgressDialog(getActivity());

                SharedPreferences sharedPref = null;

                String email = "";
                try {
                    sharedPref  = getActivity().getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
                    email       = sharedPref.getString("email", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int type = 0;
                if (!isCheckout) {
                    type = 1;
                } else {
                    type = 2;
                }

                Observable<Success> resultSaveApiObservable = SaveApiRetroFitHelper.
                        Factory.createCheckInOut(email, type); // user
                resultSaveApiObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Success>() {

                            @Override
                            public void onStart() {
                                Log.d(TAG, "onStart: ");
                                progressDialog.setMessage("Loading...");
                                progressDialog.show();
                            }

                            @Override
                            public void onCompleted() {

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
                            }

                            @Override
                            public void onNext(Success success) {
                                Toast toast = null;
                                SharedPreferences sharedPref = getActivity().
                                        getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
                                final SharedPreferences.Editor editor = sharedPref.edit();
                                if (success.isSuccess()) {
                                    if (!isCheckout) {
                                        // If not checked set temp value to true
                                        editor.putBoolean("checkin_temp", true);
                                        editor.putString("coordinates", contents);
                                        editor.commit();
                                        toast = Toast.makeText(getActivity(), "Check in was succesful", Toast.LENGTH_LONG);
                                        toast.show();
                                        EventBus.getDefault().post(new UpdateStepperEvent("continue"));
                                    } else {
                                        // Do checkout
                                        editor.remove("checkin_temp");
                                        editor.commit();
                                        toast = Toast.makeText(getActivity(), "Check out was succesful", Toast.LENGTH_LONG);
                                        toast.show();
                                        getActivity().finish();
                                        startActivity(getActivity().getIntent());
                                    }
                                } else {
                                    toast = Toast.makeText(getActivity(), "An error occured", Toast.LENGTH_LONG);
                                    toast.show();
                                    getActivity().finish();
                                    startActivity(getActivity().getIntent());
                                }
                                try {
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                        progressDialog = null;
                                    }
                                } catch(Exception exception) {
                                    exception.printStackTrace();
                                }
                            }
                        });
            }
        }
    }

    //alert dialog for downloadDialog
    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

}