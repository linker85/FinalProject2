package info.androidhive.navigationdrawer.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.navigationdrawer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyMapFragment extends Fragment {

    private static final String TAG = "MyMapFragmentTAG_";
    private GoogleMap mMap;
    @BindView(R.id.map2)
    public MapView mMapView;
    private EventBus eventBus = EventBus.getDefault();

    public MyMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

            if (mMapView != null) {
                mMapView.onCreate(savedInstanceState);

                mMapView.onResume(); // needed to get the map to display immediately

                try {
                    MapsInitializer.initialize(getActivity().getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap gMap) {
                        mMap = gMap;
                        String coordinates = "";
                        String title       = "";
                        String body        = "";
                        try {
                            coordinates = getArguments().getString("coordinates", "");
                            title       = getArguments().getString("title", "");
                            body        = getArguments().getString("body", "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.d(TAG, "coordinates: " + coordinates);

                        if (coordinates != null && !coordinates.equals("")) {
                            // For dropping a marker at a point on the Map
                            String [] coordinatesD = coordinates.split(",");


                            LatLng sydney = new LatLng(Double.parseDouble(coordinatesD[0]), Double.parseDouble(coordinatesD[1]));
                            mMap.addMarker(new MarkerOptions().position(sydney).title(title).snippet(body));

                            // For zooming automatically to the location of the marker
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    }
                });
            }
        //}
    }
}