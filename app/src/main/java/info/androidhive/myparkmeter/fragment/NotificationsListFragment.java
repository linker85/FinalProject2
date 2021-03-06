package info.androidhive.myparkmeter.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.myparkmeter.R;
import info.androidhive.myparkmeter.models.Notification;
import info.androidhive.myparkmeter.other.NotificationsAdapter;
import info.androidhive.myparkmeter.other.SimpleDecorator;
import info.androidhive.myparkmeter.other.UpdateMapEvent;
import info.androidhive.myparkmeter.other.UpdateMapEvent2;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsListFragment extends Fragment {

    private static final String TAG = "NotifFragment";
    @BindView(R.id.a_notifications_recycler)
    public RecyclerView notificationRecyclerView;
    private List<Notification> notificationArrayList;
    private NotificationsAdapter notificationAdapter;
    @BindView(R.id.initDateTxt)
    public TextView doOpenDate1;
    @BindView(R.id.endDateTxt)
    public TextView doOpenDate2;
    @BindView(R.id.fab)
    public FloatingActionButton fab1;

    private EventBus eventBus = EventBus.getDefault();

    public NotificationsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        eventBus.register(this);
    }

    public void onAttachFragment(Fragment fragment) {
    }

    // invoked by EventBus
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final UpdateMapEvent event) {
        // Do something!
        eventBus.post(new UpdateMapEvent2(event.coordinates, event.title, event.body));
    }

    @Override
    public void onDetach() {
        eventBus.unregister(this);
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_items_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    // These variables will hold the date values later
    private int    startYear, startMonth, startDay, endYear, endMonth, endDay;
    private String startMonthS, startDayS, endMonthS, endDayS;
    public void showDatePicker() {
        // Inflate your custom layout containing 2 DatePickers
        LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
        View customView = inflater.inflate(R.layout.custom, null);

        // Define your date pickers
        final DatePicker dpStartDate = (DatePicker) customView.findViewById(R.id.initDate);
        final DatePicker dpEndDate   = (DatePicker) customView.findViewById(R.id.endDate);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(customView); // Set the view of the dialog to your custom layout
        builder.setTitle(R.string.notifications_instructions);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startYear  = dpStartDate.getYear();
                startMonth = dpStartDate.getMonth();
                startDay   = dpStartDate.getDayOfMonth();
                endYear    = dpEndDate.getYear();
                endMonth   = dpEndDate.getMonth();
                endDay     = dpEndDate.getDayOfMonth();

                startMonth++;
                endMonth++;
                if (endMonth <= 9) {
                    endMonthS = "0" + endMonth;
                } else {
                    endMonthS = "" + endMonth;
                }

                if (endDay <= 9) {
                    endDayS = "0" + endDay;
                } else {
                    endDayS = "" + endDay;
                }

                if (startMonth <= 9) {
                    startMonthS = "0" + startMonth;
                } else {
                    startMonthS = "" + startMonth;
                }

                if (startDay <= 9) {
                    startDayS = "0" + startDay;
                } else {
                    startDayS = "" + startDay;
                }

                doOpenDate1.setText(startYear + "-" + startMonthS + "-" + startDayS + " 00:00:00");
                doOpenDate2.setText(endYear + "-" + endMonthS + "-" + endDayS + " 23:59:59");
                dialog.dismiss();


                // 1. get a reference to recyclerView
                //notificationRecyclerView = (RecyclerView) getActivity().findViewById(R.id.a_notifications_recycler);
                // 2. set layoutManger
                notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                // 3. Get data from database
                notificationArrayList = new ArrayList<>();
                String email = "";
                SharedPreferences sharedPref = null;
                try {
                    sharedPref = getActivity().
                            getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
                    email = sharedPref.getString("email", "");
                } catch (Exception e) {

                }
                new NotificationsTask(getActivity()).execute(new String[]{
                        email, doOpenDate1.getText().toString(), doOpenDate2.getText().toString()
                });
            }});

        // Create and show the dialog
        builder.create().show();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

    }

    public void doSearch(View view) {

    }


    public class NotificationsTask extends AsyncTask<String, List<Notification>, Void> {

        private Context mContext;

        public NotificationsTask (Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(String... params) {

            // datetime(CAST(strftime('%s', DATE_S) AS INT), 'unixepoch')

            notificationArrayList = Notification.findWithQuery(Notification.class,
                    "SELECT * FROM NOTIFICATION " +
                            " WHERE EMAIL = ? AND datetime(CAST(strftime('%s', DATE_S) AS INT), 'unixepoch') BETWEEN ? AND ? ",
                    params[0], params[1], params[2]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // 4. set adapter
            if (notificationArrayList == null || notificationArrayList.isEmpty()) {
                Toast.makeText(getActivity(), R.string.no_notifications, Toast.LENGTH_LONG).show();
            }
            notificationAdapter = new NotificationsAdapter(notificationArrayList, eventBus);
            notificationRecyclerView.setAdapter(notificationAdapter);
            notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            notificationRecyclerView.setItemAnimator(new DefaultItemAnimator());
            notificationRecyclerView.addItemDecoration(new SimpleDecorator(mContext, LinearLayoutManager.VERTICAL));
            // 5. notify changes
            notificationAdapter.notifyDataSetChanged();
        }
    }

}