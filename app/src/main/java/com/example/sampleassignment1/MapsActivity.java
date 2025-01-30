package com.example.sampleassignment1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.sampleassignment1.databinding.ActivityMapsBinding;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, OnStreetViewPanoramaReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    SupportStreetViewPanoramaFragment streetViewPanoramaFragment;
    StreetViewPanorama mStreetViewPanorama;

    boolean isClicked, redMarkerClicked, blueMarkerClicked, streetViewClicked= false;
    boolean secondIsClicked = false;

    String theUser;
    double latitude, longitude, blueMarkerLat, blueMarkerLng;
    LatLng latLngRed, latLngBlue;
    String address;
    TextView textViewAddress;
    TextView textViewLatitude;
    TextView textViewLongitude;
    Button btnMapStreetView;

    PlacesClient placesClient;
    Marker redMarker, blueMarker;
    boolean showMap = true;
    MyLocationPlaceMap myLocationPlaceMap;
    ArrayList<Double>checkDuplicateLat=new ArrayList<>();
    ArrayList<Double>checkDuplicateLng=new ArrayList<>();
    boolean foundOne;

    boolean foundTwo;

    MyLocationPlace newLocation;

//    boolean found;

    ArrayList<MyLocationPlace> myLocations = new ArrayList<>();
    MyLocationPlace myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        binding = ActivityMapsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
        setTitle("My Location Details");
        btnMapStreetView = findViewById(R.id.buttonMapStreetView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            theUser = extras.getString("theUser");
            latitude = extras.getDouble("lat");
            longitude = extras.getDouble("lng");
            latLngRed = new LatLng(latitude, longitude);
            address = extras.getString("addr");

            textViewAddress = findViewById(R.id.textViewStreetAddress);
            textViewAddress.setText("Address: " + address);
            textViewLatitude = findViewById(R.id.textViewLatitude);
            textViewLatitude.setText("Latitude: " + latitude);
            textViewLongitude = findViewById(R.id.textViewLongitude);
            textViewLongitude.setText("Longitude: " + longitude);
        }
        newLocation = new MyLocationPlace(latitude, longitude, address);


            uploadClassInstanceToRealtimeDB(theUser,newLocation);




        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(R.id.streetView);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        mapFragment.getView().bringToFront();

        myLocationPlaceMap = new MyLocationPlaceMap(getApplicationContext(), MapsActivity.this);
    }



    public void uploadClassInstanceToRealtimeDB(String hello,MyLocationPlace myLocationPlace) {

        if (myLocationPlace == null)
            return;


        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(hello);
        String key = dbRef.push().getKey(); // to generate a random key
        dbRef.child(key).child("latitude").setValue(myLocationPlace.getLatitude());
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");//dd/mm/yyyy at hh:mm:ss
        SimpleDateFormat date2 = new SimpleDateFormat("HH:mm:ss a");
        String timeStamp = date.format(new Date()) + " at " + date2.format(new Date());
        dbRef.child(key).child("date").setValue(timeStamp);
        dbRef.child(key).child("longitude").setValue(myLocationPlace.getLongitude());
        dbRef.child(key).child("address").setValue(myLocationPlace.getAddress());}
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                redMarker = mMap.addMarker(new MarkerOptions()
                        .title("Show Surroundings")
                        .snippet("Latitude: " + latitude + ", Longitude: " + longitude +
                                "\nAddress: " + address)
                        .position(latLngRed)
                );
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngRed, 14));
                if (isClicked) {
                    showNearby(null);
}
                if(isClicked && redMarkerClicked){
                    streetViewPanoramaFragment.getView().bringToFront();
                }
                if(isClicked && blueMarkerClicked){
                    LatLng position = new LatLng(blueMarkerLat, blueMarkerLng);

                    streetViewPanoramaFragment.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
                        @Override
                        public void onStreetViewPanoramaReady(@NonNull StreetViewPanorama streetViewPanorama) {
                            streetViewPanorama.setPosition(position);
                            streetViewPanoramaFragment.getView().bringToFront();
                        }
                    });
                }
                if(streetViewClicked){
                    showMapStreetView(null);

                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if (marker.equals(redMarker)) {
                    redMarkerClicked = true;
                    streetViewPanoramaFragment.getView().bringToFront();
                    btnMapStreetView.setText("Show map");
                    showMap = false;
                    return true;
                } else {
                    return false;
                }

            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                blueMarkerClicked = true;
                Log.d("clicked", "true");
                if (!marker.getId().equals(redMarker.getId())) {
                    streetViewPanoramaFragment.getView().bringToFront();
                    latLngBlue = marker.getPosition();
                    mStreetViewPanorama.setPosition(latLngBlue);

                    blueMarkerLat = marker.getPosition().latitude;
                    blueMarkerLng = marker.getPosition().longitude;
                }
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                TextView title = (TextView) infoWindow.findViewById(R.id.textViewTitle);
                TextView snippet = (TextView) infoWindow.findViewById(R.id.textViewSnippet);
                ImageView image = (ImageView) infoWindow.findViewById(R.id.imageView);

                if (marker.getTitle() != null && marker.getSnippet() != null) {
                    title.setText(marker.getTitle());
                    snippet.setText(marker.getSnippet());
                } else {
                    title.setText("No info available");
                    snippet.setText("No location available");
                }
                image.setImageDrawable(getResources()
                        .getDrawable(R.drawable.blue_marker, getTheme()));
                return infoWindow;
            }
        });
    }

    @Override
    public void onStreetViewPanoramaReady(@NonNull StreetViewPanorama streetViewPanorama) {
        mStreetViewPanorama = streetViewPanorama;
        mStreetViewPanorama.setPosition(latLngRed);

    }


    public void showNearby(View view) {
        isClicked = true;
        mapFragment.getView().bringToFront();
        myLocationPlaceMap.getNearbyPlaces(mMap, "AIzaSyCqDhpofV0nb7Ui_OT1qjslpPq80YXjSEQ");
    }

    public void showMapStreetView(View view) {
        showMap = !showMap;
        if (showMap) {
            streetViewClicked = false;
            mapFragment.getMapAsync(this);
            mapFragment.getView().bringToFront();
            btnMapStreetView.setText("Show Street View");
        } else {
            streetViewClicked = true;
            streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
            streetViewPanoramaFragment.getView().bringToFront();
            btnMapStreetView.setText("Show Map");

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
// Save the current state
        outState.putBoolean("button_clicked", isClicked);
        outState.putBoolean("red_marker", redMarkerClicked);
        outState.putBoolean("blue_marker", blueMarkerClicked);
        outState.putDouble("blue_marker_lat", blueMarkerLat);
        outState.putDouble("blue_marker_lng", blueMarkerLng);
        outState.putBoolean("street_view", streetViewClicked);
        super.onSaveInstanceState(outState);

    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
// Restore the saved state
        isClicked = savedInstanceState.getBoolean("button_clicked");
        redMarkerClicked = savedInstanceState.getBoolean("red_marker");
        blueMarkerClicked = savedInstanceState.getBoolean("blue_marker");
        blueMarkerLat = savedInstanceState.getDouble("blue_marker_lat");
        blueMarkerLng = savedInstanceState.getDouble("blue_marker_lng");
        streetViewClicked = savedInstanceState.getBoolean("street_view");
    }

//    @Override
//    public void onInfoWindowClick(@NonNull Marker marker) {
//        blueMarkerClicked = true;
//
//        if (!marker.getId().equals(redMarker.getId())) {
//            streetViewPanoramaFragment.getView().bringToFront();
//            latLngBlue = marker.getPosition();
//            mStreetViewPanorama.setPosition(latLngBlue);
//        }
//    }

//    @Override
//    protected void onSaveInstanceStateTwo(Bundle outState) {
//// Save the current state
//        outState.putBoolean("button_clicked", isClicked);
//        super.onSaveInstanceState(outState);
//
//    }
//    @Override
//    protected void onRestoreInstanceStateTwo(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//// Restore the saved state
//        isClicked = savedInstanceState.getBoolean("button_clicked");
//    }

}