package info.androidhive.navigationdrawer.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.models.Notification;
import info.androidhive.navigationdrawer.other.NotificationsAdapter;
import info.androidhive.navigationdrawer.other.UpdateMapEvent2;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * createSave an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {

    private static final String TAG = "NotifiFragmentTAG_";
    private RecyclerView notificationRecyclerView;
    private ArrayList<Notification> notificationArrayList;
    private NotificationsAdapter notificationAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EventBus eventBus = EventBus.getDefault();

    private OnFragmentInteractionListener mListener;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to createSave a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    private GoogleMap mMap;
    private MapView mMapView;
    private Button button;

    // invoked by EventBus
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final UpdateMapEvent2 event) {
        // Do something!
        Log.d(TAG, "onEventMainThreadUp: " + event.coordinates);

        final int orientation = getResources().getConfiguration().orientation;
        if (orientation == 1) { // Portrait
        } else {
            // Landscape
            MyMapFragment fragmentMap = new MyMapFragment();

            Bundle bundle = new Bundle();
            bundle.putString("coordinates", event.coordinates);
            fragmentMap.setArguments(bundle);

            FragmentTransaction mft = getActivity().getSupportFragmentManager().beginTransaction();
            mft.replace(R.id.flDetailMap, fragmentMap);
            mft.addToBackStack(null);
            mft.commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");

        //If landscape replace with fragment
        NotificationsListFragment fragmentItem = new NotificationsListFragment();
        fragmentItem.setArguments(new Bundle());

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flDetailContainer, fragmentItem);
        ft.commit();

        final int orientation = getResources().getConfiguration().orientation;
        if (orientation == 1) { // Portrait
        } else {
            // Landscape
            MyMapFragment fragmentMap = new MyMapFragment();
            FragmentTransaction mft = getActivity().getSupportFragmentManager().beginTransaction();
            mft.replace(R.id.flDetailMap, fragmentMap);
            mft.commit();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        eventBus.register(this);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        eventBus.unregister(this);
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}