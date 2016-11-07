package info.androidhive.navigationdrawer.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.models.Notification;
import info.androidhive.navigationdrawer.other.NotificationsAdapter;
import info.androidhive.navigationdrawer.other.SimpleDecorator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * createSave an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        // 1. get a reference to recyclerView
        notificationRecyclerView = (RecyclerView) view.findViewById(R.id.a_notifications_recycler);
        // 2. set layoutManger
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 3. Get data from database
        notificationArrayList = new ArrayList<>();
        notificationArrayList.add(new Notification("title09", "body1", "30/10/2016"));
        notificationArrayList.add(new Notification("title10", "body2", "30/10/2016"));
        notificationArrayList.add(new Notification("title11", "body3", "30/10/2016"));
        notificationArrayList.add(new Notification("title12", "body4", "30/10/2016"));
        notificationArrayList.add(new Notification("title13", "body5", "30/10/2016"));
        notificationArrayList.add(new Notification("title14", "body6", "30/10/2016"));
        notificationArrayList.add(new Notification("title15", "body7", "30/10/2016"));
        notificationArrayList.add(new Notification("title16", "body8", "30/10/2016"));
        notificationArrayList.add(new Notification("title17", "body9", "30/10/2016"));
        notificationArrayList.add(new Notification("title18", "body10", "30/10/2016"));
        notificationArrayList.add(new Notification("title19", "body11", "30/10/2016"));

        // 4. set adapter
        notificationAdapter = new NotificationsAdapter(notificationArrayList);
        notificationRecyclerView.setAdapter(notificationAdapter);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        notificationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        notificationRecyclerView.addItemDecoration(new SimpleDecorator(getActivity(), LinearLayoutManager.VERTICAL));
        // 5. notify changes
        notificationAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
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
