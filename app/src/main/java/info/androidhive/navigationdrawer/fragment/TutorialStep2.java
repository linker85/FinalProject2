package info.androidhive.navigationdrawer.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etiennelawlor.discreteslider.library.ui.DiscreteSlider;
import com.etiennelawlor.discreteslider.library.utilities.DisplayUtility;

import org.codepond.wizardroid.WizardStep;
import org.codepond.wizardroid.persistence.ContextVariable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.models.CheckinMock;
import info.androidhive.navigationdrawer.models.Success;
import info.androidhive.navigationdrawer.retrofit_helpers.SaveApiRetroFitHelper;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by raul on 30/10/2016.
 */

public class TutorialStep2 extends WizardStep {

    private static final String TAG = "Step2TAG_";

    @BindView(R.id.total_to_pay)
    public TextView totalToPay;
    @BindView(R.id.snap_bar)
    public TextView snap_bar;
    @BindView(R.id.parent_step)
    public RelativeLayout relativeLayout;
    @BindView(R.id.btn_save_time)
    public Button   saveTime;

    private String[] tickMarkLabels1 = {"0 min", "5 min", "10 min", "15 min", "30 min", "45 min"};
    private String[] tickMarkLabels2 = {"0 hr", "1 hr", "2 hr", "3 hr", "4 hr", "5 hr", "6 hr", "7 hr", "8 hr"};
    private double[] costLabels1 = {0, 0.5, 1, 1, 1,5, 3, 4,5};
    private double[] costLabels2 = {0, 10, 20, 30, 40, 50, 60, 70, 80};

    // region Views
    @BindView(R.id.discrete_slider1)
    public DiscreteSlider discreteSlider1;
    @BindView(R.id.discrete_slider2)
    public DiscreteSlider discreteSlider2;
    @BindView(R.id.tick_mark_labels_rl)
    public RelativeLayout tickMarkLabelsRelativeLayout1;
    @BindView(R.id.tick_mark_labels_r2)
    public RelativeLayout tickMarkLabelsRelativeLayout2;
    // endregion

    private static final int SNAP_MIN = 0;
    private static final int SNAP_MIDDLE = 50;
    private static final int SNAP_MAX = 100;

    private static final int LOWER_HALF = (SNAP_MIN + SNAP_MIDDLE) / 2;
    private static final int UPPER_HALF = (SNAP_MIDDLE + SNAP_MAX) / 2;

    private View.OnClickListener yesOnClickListener;
    private View.OnClickListener noOnClickListener;

    @ContextVariable
    private boolean isCheckin;
    private boolean isExtend;

    private String finalMessage;

    //Wire the layout to the step
    public TutorialStep2() {
    }

    //Set your layout here
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.step_choose_time, container, false);
        ButterKnife.bind(this, v);

        /*totalToPay                   = (TextView)       v.findViewById(R.id.total_to_pay);
        snap_bar                     = (TextView)       v.findViewById(R.id.snap_bar);
        relativeLayout               = (RelativeLayout) v.findViewById(R.id.parent_step);
        saveTime                     = (Button)         v.findViewById(R.id.btn_save_time);

        discreteSlider1               = (DiscreteSlider) v.findViewById(R.id.discrete_slider1);
        tickMarkLabelsRelativeLayout1 = (RelativeLayout) v.findViewById(R.id.tick_mark_labels_rl);
        discreteSlider2               = (DiscreteSlider) v.findViewById(R.id.discrete_slider2);
        tickMarkLabelsRelativeLayout2 = (RelativeLayout) v.findViewById(R.id.tick_mark_labels_r2);*/

        isExtend = getArguments().getBoolean("isExtend");

        SharedPreferences sharedPref = null;
        try {
            sharedPref = getActivity().
                    getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
            isCheckin = sharedPref.getBoolean("checkin_temp", false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isExtend) {
            isCheckin = true;
            finalMessage = "You´ve succesfully extend your checkin time.";
            snap_bar.setText("For how long do you wish to extend your time?");
        } else {
            finalMessage = "You´ve succesfully checkin.";
        }

        if (!isCheckin) {
            totalToPay.setVisibility(View.INVISIBLE);
            discreteSlider1.setVisibility(View.INVISIBLE);
            discreteSlider2.setVisibility(View.INVISIBLE);
            saveTime.setVisibility(View.INVISIBLE);
            tickMarkLabelsRelativeLayout1.setVisibility(View.INVISIBLE);
            tickMarkLabelsRelativeLayout2.setVisibility(View.INVISIBLE);
            snap_bar.setText("You need to Check in before extending time.");
        }

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        discreteSlider1.setTickMarkCount(6);
        discreteSlider1.setPosition(0);
        discreteSlider2.setTickMarkCount(6);
        discreteSlider2.setPosition(0);

        saveTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectionHr  = 0;
                int selectionMin = 0;

                SharedPreferences sharedPref = getActivity().
                        getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
                selectionMin = sharedPref.getInt("min", 0);
                selectionHr  = sharedPref.getInt("hr", 0);

                Log.d("TAG", "onPositionChangedMin: " + costLabels2[selectionHr]);
                Log.d("TAG", "onPositionChangedHr: " + costLabels1[selectionMin]);

                if (costLabels2[selectionHr] > 0 || costLabels1[selectionMin] > 0) {

                    StringBuilder message = new StringBuilder("You have choosen ");
                    double timeMinD = 0;
                    double timeHrD = 0;

                    if (costLabels2[selectionHr] > 0) {
                        message.append(tickMarkLabels2[selectionHr]);
                        timeHrD = costLabels2[selectionHr];
                    }

                    if (costLabels1[selectionMin] > 0) {
                        if (costLabels2[selectionHr] > 0) {
                            message.append(" and ");
                        }
                        message.append(tickMarkLabels1[selectionMin]);
                        timeMinD = costLabels1[selectionMin];
                    }
                    message.append(", you have to pay: $" + (timeMinD + timeHrD));
                    totalToPay.setText(message);

                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());

                    Snackbar snackbar = Snackbar
                            .make(relativeLayout, "Do you accept?", Snackbar.LENGTH_LONG)
                            .setAction("Yes", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Observable<Success> resultSaveApiObservable = SaveApiRetroFitHelper.
                                            Factory.createCheckInOut("email", 2); // user
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
                                                    Log.d(TAG, "onCompleted: ");
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    Log.d(TAG, "onError: " + e.getMessage());
                                                    totalToPay.setText("An error ocurred, your time couldn´t be set.");
                                                }

                                                @Override
                                                public void onNext(Success success) {
                                                    CheckinMock checkinMock = null;
                                                    ///////////////////////////////////// Simulation of registering time
                                                    SharedPreferences sharedPref = getActivity().
                                                            getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
                                                    String email = sharedPref.getString("email", "");
                                                    List<CheckinMock> checkinMockList = CheckinMock.findWithQuery(
                                                            CheckinMock.class, "SELECT * FROM CHECKIN_MOCK WHERE EMAIL=?", email);
                                                    final SharedPreferences.Editor editor = sharedPref.edit();
                                                    editor.remove("checkin_temp");
                                                    editor.commit();
                                                    if (checkinMockList != null && !checkinMockList.isEmpty()) {
                                                        checkinMock = checkinMockList.get(0);
                                                    } else {
                                                        checkinMock = new CheckinMock();
                                                        checkinMock.setResult(1);
                                                        checkinMock.setEmail(email);
                                                    }
                                                    checkinMock.save();
                                                    ////////////////////////////////////////////////////////////////////
                                                    Toast.makeText(getActivity(), finalMessage, Toast.LENGTH_LONG).show();
                                                    getActivity().finish();
                                                    startActivity(getActivity().getIntent());
                                                }
                                            });
                                }
                            });
                    snackbar.show();

                } else {
                    totalToPay.setText("Select a time before saving.");
                }
            }
        });

        // Detect when slider position changes
        discreteSlider1.setOnDiscreteSliderChangeListener(new DiscreteSlider.OnDiscreteSliderChangeListener() {
            @Override
            public void onPositionChanged(int position) {
                int childCount = tickMarkLabelsRelativeLayout1.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    TextView tv = (TextView) tickMarkLabelsRelativeLayout1.getChildAt(i);
                    if (i == position)
                        tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                    else
                        tv.setTextColor(getResources().getColor(R.color.grey_400));
                }
                SharedPreferences sharedPref = getActivity().
                        getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("min", position);
                editor.commit();
            }
        });

        discreteSlider2.setOnDiscreteSliderChangeListener(new DiscreteSlider.OnDiscreteSliderChangeListener() {
            @Override
            public void onPositionChanged(int position) {
                int childCount = tickMarkLabelsRelativeLayout2.getChildCount();
                for (int i= 0; i<childCount; i++) {
                    TextView tv = (TextView) tickMarkLabelsRelativeLayout2.getChildAt(i);
                    if (i == position)
                        tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                    else
                        tv.setTextColor(getResources().getColor(R.color.grey_400));
                }
                SharedPreferences sharedPref = getActivity().
                        getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("hr", position);
                editor.commit();
            }
        });

        tickMarkLabelsRelativeLayout1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tickMarkLabelsRelativeLayout1.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                addTickMarkTextLabels1();
            }
        });

        tickMarkLabelsRelativeLayout2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tickMarkLabelsRelativeLayout2.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                addTickMarkTextLabels2();
            }
        });
    }

    // region Helper Methods

    private void addTickMarkTextLabels1(){
        int tickMarkCount = discreteSlider1.getTickMarkCount();
        float tickMarkRadius = discreteSlider1.getTickMarkRadius();
        int width = tickMarkLabelsRelativeLayout1.getMeasuredWidth();

        int discreteSliderBackdropLeftMargin = DisplayUtility.dp2px(getContext(), 32);
        int discreteSliderBackdropRightMargin = DisplayUtility.dp2px(getContext(), 32);
        float firstTickMarkRadius = tickMarkRadius;
        float lastTickMarkRadius  = tickMarkRadius;
        int interval = (width - (discreteSliderBackdropLeftMargin+discreteSliderBackdropRightMargin) - ((int)(firstTickMarkRadius+lastTickMarkRadius)) )
                / (tickMarkCount-1);

        int tickMarkLabelWidth = DisplayUtility.dp2px(getContext(), 40);

        for(int i=0; i<tickMarkCount; i++) {
            TextView tv = new TextView(getContext());

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    tickMarkLabelWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);

            tv.setText(tickMarkLabels1[i]);
            tv.setGravity(Gravity.CENTER);
            if(i== discreteSlider1.getPosition())
                tv.setTextColor(getResources().getColor(R.color.colorPrimary));
            else
                tv.setTextColor(getResources().getColor(R.color.grey_400));

//                    tv.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));

            int left = discreteSliderBackdropLeftMargin + (int)firstTickMarkRadius + (i * interval) - (tickMarkLabelWidth/2);

            layoutParams.setMargins(left,
                    0,
                    0,
                    0);
            tv.setLayoutParams(layoutParams);

            tickMarkLabelsRelativeLayout1.addView(tv);
        }
    }

    private void addTickMarkTextLabels2(){
        int tickMarkCount = discreteSlider2.getTickMarkCount();
        float tickMarkRadius = discreteSlider2.getTickMarkRadius();
        int width = tickMarkLabelsRelativeLayout2.getMeasuredWidth();

        int discreteSliderBackdropLeftMargin = DisplayUtility.dp2px(getContext(), 32);
        int discreteSliderBackdropRightMargin = DisplayUtility.dp2px(getContext(), 32);
        float firstTickMarkRadius = tickMarkRadius;
        float lastTickMarkRadius  = tickMarkRadius;
        int interval = (width - (discreteSliderBackdropLeftMargin+discreteSliderBackdropRightMargin) - ((int)(firstTickMarkRadius+lastTickMarkRadius)) )
                / (tickMarkCount-1);

        int tickMarkLabelWidth = DisplayUtility.dp2px(getContext(), 40);

        for(int i=0; i<tickMarkCount; i++) {
            TextView tv = new TextView(getContext());

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    tickMarkLabelWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);

            tv.setText(tickMarkLabels2[i]);
            tv.setGravity(Gravity.CENTER);
            if(i== discreteSlider2.getPosition())
                tv.setTextColor(getResources().getColor(R.color.colorPrimary));
            else
                tv.setTextColor(getResources().getColor(R.color.grey_400));

//                    tv.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));

            int left = discreteSliderBackdropLeftMargin + (int)firstTickMarkRadius + (i * interval) - (tickMarkLabelWidth/2);

            layoutParams.setMargins(left,
                    0,
                    0,
                    0);
            tv.setLayoutParams(layoutParams);

            tickMarkLabelsRelativeLayout2.addView(tv);
        }
    }
    // endregion

}