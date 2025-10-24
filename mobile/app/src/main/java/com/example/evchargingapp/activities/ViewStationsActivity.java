package com.example.evchargingapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingapp.R;
import com.example.evchargingapp.api.StationApi;
import com.example.evchargingapp.models.GeoLocation;
import com.example.evchargingapp.models.Station;
import com.example.evchargingapp.utils.SharedPrefsHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewStationsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private final List<Station> stationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stations);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        loadStationsFromApi();
    }

    private void loadStationsFromApi() {
        new Thread(() -> {
            try {
                String token = SharedPrefsHelper.getToken(this);
                JSONArray stationsArray = StationApi.getAllStations(token);

                for (int i = 0; i < stationsArray.length(); i++) {
                    JSONObject obj = stationsArray.getJSONObject(i);
                    Log.d("StationData", obj.toString());
                    JSONObject loc = obj.getJSONObject("location");

                    double lat = loc.optDouble("latitude", 0.0);   // <-- use "latitude"
                    double lng = loc.optDouble("longitude", 0.0);  // <-- use "longitude"

                    Station s = new Station();
                    s.setStationId(obj.optString("stationId"));
                    s.setStationName(obj.optString("name"));
                    s.setLocation(obj.optString("type", "Unknown Type"));
                    s.setGeoLocation(new GeoLocation(lat, lng));
                    s.setAvailable(obj.optBoolean("isActive", true));

                    stationList.add(s);

                    Log.d("StationMarker", s.getStationName() + " -> " + lat + ", " + lng);

                }

                runOnUiThread(this::displayStationsOnMap);

            } catch (Exception e) {
                Log.e("ViewStationsActivity", "Error loading stations", e);
                runOnUiThread(() ->
                        Toast.makeText(this, "Failed to load stations", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void displayStationsOnMap() {
        if (googleMap == null || stationList.isEmpty()) return;

        for (Station s : stationList) {
            GeoLocation g = s.getGeoLocation();
            LatLng latLng = new LatLng(g.getLatitude(), g.getLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(s.getStationName())
                    .snippet(s.getLocation()));
        }

        // Focus camera on first station
        Station first = stationList.get(0);
        GeoLocation g = first.getGeoLocation();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(g.getLatitude(), g.getLongitude()), 11f));
    }
}
