package info.androidhive.navigationdrawer.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.models.Notification;
import info.androidhive.navigationdrawer.other.NotificationsAdapter;
import info.androidhive.navigationdrawer.other.SimpleDecorator;
import info.androidhive.navigationdrawer.other.UpdateMapEvent;
import info.androidhive.navigationdrawer.other.UpdateMapEvent2;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsListFragment extends Fragment {

    private static final String TAG = "NotifFragment";
    private RecyclerView notificationRecyclerView;
    private ArrayList<Notification> notificationArrayList;
    private NotificationsAdapter notificationAdapter;
    //private OnHeadlineSelectedListener mOnPlayerSelectionSetListener;

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
        /*try {
            mOnPlayerSelectionSetListener = (OnHeadlineSelectedListener)fragment;
        } catch (ClassCastException e)  {
            throw new ClassCastException(
                    fragment.toString() + " must implement OnPlayerSelectionSetListener");
        }*/
    }

    // invoked by EventBus
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final UpdateMapEvent event) {
        // Do something!
        Log.d(TAG, "onEventMainThread: " + event.coordinates);
        eventBus.post(new UpdateMapEvent2(event.coordinates));
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
        return inflater.inflate(R.layout.fragment_items_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. get a reference to recyclerView
        notificationRecyclerView = (RecyclerView) view.findViewById(R.id.a_notifications_recycler);
        // 2. set layoutManger
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 3. Get data from database
        notificationArrayList = new ArrayList<>();
        notificationArrayList.add(new Notification("title15", "ATL1", "30/10/2016", "33.862100,-84.687900"));
        notificationArrayList.add(new Notification("title16", "ATL2", "30/10/2016", "33.588263,-84.302177"));
        notificationArrayList.add(new Notification("title17", "ATL3", "30/10/2016", "33.577394,-84.353418"));
        notificationArrayList.add(new Notification("title18", "ATL4", "30/10/2016", "33.559157,-84.410774"));
        notificationArrayList.add(new Notification("title19", "ATL5", "30/10/2016", "33.561339,-84.420741"));

        // 4. set adapter
        notificationAdapter = new NotificationsAdapter(notificationArrayList, eventBus);
        notificationRecyclerView.setAdapter(notificationAdapter);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        notificationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        notificationRecyclerView.addItemDecoration(new SimpleDecorator(getActivity(), LinearLayoutManager.VERTICAL));
        // 5. notify changes
        notificationAdapter.notifyDataSetChanged();

    }


}