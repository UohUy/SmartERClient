package monash.smarterclient;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapquest.mapping.maps.MapView;
import com.mapquest.mapping.maps.MapboxMap;
import com.mapquest.mapping.maps.OnMapReadyCallback;


public class MapFragment extends Fragment {
    View vMap;
    private MapboxMap mMapboxMap;
    private MapView mMapView;

    private final LatLng SAN_FRAN = new LatLng(37.7749, -122.4194);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vMap = inflater.inflate(R.layout.fragment_map, container, false);
        MapboxAccountManager.start(getActivity().getApplicationContext());

        mMapView = (MapView) vMap.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mMapboxMap = mapboxMap;
                mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SAN_FRAN, 11));
                addMarker(mMapboxMap);
            }
        });

        return vMap;
    }

    @Override
    public void onResume(){
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        mMapView.onResume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private void addMarker(MapboxMap mapboxMap) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SAN_FRAN);
        markerOptions.title("San Francisco");
        markerOptions.snippet("Welcome to San Fran!");
        mapboxMap.addMarker(markerOptions);
    }
}
