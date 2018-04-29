package monash.smarterclient;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private LatLng[] latLngs;
    private String[] queries;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vMap = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) vMap.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);

        // Initialize address queries.
        GetAddressQueries getAddressQueries = new GetAddressQueries();
        getAddressQueries.execute((Void) null);

        // Initialize LatLng of all resident address.
        GetAllLatLng getAllLatLng = new GetAllLatLng();
        getAllLatLng.execute((Void) null);


        return vMap;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private void addMarker(MapboxMap mapboxMap, LatLng givenLatLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(givenLatLng);
        mapboxMap.addMarker(markerOptions);
    }


    private String generateQuery(String address) {
        String pieces[] = address.split(",");
        String street[] = pieces[0].split(" ");
        String postcode = pieces[1];
        String query = "";
        for (String piece : street) {
            query += piece.trim();
            query += "+";
        }
        query += postcode;
        return query;
    }

    public class GetAddressQueries extends AsyncTask<Void, Void, Boolean> {
        public GetAddressQueries() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String[] addressArray = HTTPRequest.findAllAddressAndPostcode();
            queries = new String[addressArray.length];
            for (int i = 0; i < addressArray.length; i++) {
                queries[i] = generateQuery(addressArray[i]);
            }
            return true;
        }
    }

    public class GetAllLatLng extends AsyncTask<Void, Void, Boolean> {
        public GetAllLatLng() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            latLngs = new LatLng[queries.length];
            for (int i = 0; i < queries.length; i++) {
                latLngs[i] = HTTPRequest.getLatLng(queries[i]);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // Add markers for all residents on map
                for (final LatLng latLng : latLngs) {
                    mMapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(MapboxMap mapboxMap) {
                            mMapboxMap = mapboxMap;
                            mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            addMarker(mMapboxMap, latLng);
                        }
                    });
                }
            }
        }
    }
}
