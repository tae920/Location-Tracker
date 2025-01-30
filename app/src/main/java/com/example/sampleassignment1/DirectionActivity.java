package com.example.sampleassignment1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.sampleassignment1.databinding.ActivityDirectionBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DirectionActivity extends FragmentActivity implements OnMapReadyCallback, OnStreetViewPanoramaReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    SupportStreetViewPanoramaFragment streetViewPanoramaFragment;
    StreetViewPanorama mStreetViewPanorama;

    ArrayList<UserData> mainUserList = new ArrayList<>();

    ArrayList<UserData> subUserList = new ArrayList<>();

    String mainUser;

    String subUser;

    LatLng latLngMain,latLngSub;

    TextView distance;
    TextView time;


    double latitude;
    double longitude;
    LatLng latLngRed, latLngBlue;
    String address;

    PlacesClient placesClient;
    Marker redMarker, blueMarker;
    boolean showMap = true;
    MyLocationPlaceMap myLocationPlaceMap;
    ArrayList<MyLocationPlace> myLocations = new ArrayList<>();
    MyLocationPlace myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mainUser = extras.getString("mainUser");
            subUser = extras.getString("subUser");
        }
        downloadMainClassInstanceFromRealtimeDB(mainUser);
        downloadSubClassInstanceFromRealtimeDB(subUser);

        distance = findViewById(R.id.textView3);
        time = findViewById(R.id.textView4);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(R.id.streetView);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        mapFragment.getView().bringToFront();

        myLocationPlaceMap = new MyLocationPlaceMap(getApplicationContext(), DirectionActivity.this);
    }

    public void downloadMainClassInstanceFromRealtimeDB(String mainUser) {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(mainUser);

        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    String address = snapshot.child("address").getValue().toString();
                    String date = snapshot.child("date").getValue().toString();
                    double latitude = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                    double longitude = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                    UserData userData = new UserData(address,date,latitude,longitude);
                    mainUserList.add(userData);

//                int lastIndex = userList.size() - 1;
//                h.setText(userList.get(lastIndex).getNewDate());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void downloadSubClassInstanceFromRealtimeDB(String subUser) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(subUser);

        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    String address = snapshot.child("address").getValue().toString();
                    String date = snapshot.child("date").getValue().toString();
                    double latitude = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                    double longitude = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                    UserData userData = new UserData(address,date,latitude,longitude);
                    subUserList.add(userData);

//                int lastIndex = userList.size() - 1;
//                h.setText(userList.get(lastIndex).getNewDate());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



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
         //       int mainUserNum = mainUserList.length;
                for(int i=0;i<mainUserList.size();i++){
//                    redMarker = mMap.addMarker(new MarkerOptions()
//                            .title("Show Surroundings")
//                            .snippet("Latitude: " + latitude + ", Longitude: " + longitude +
//                                    "\nAddress: " + address)
//                            .position(latLngRed)
//                    );
                    String newAddress = mainUserList.get(i).getNewAddress();
                    String newDate = mainUserList.get(i).getNewDate();
                    double newLatitude=mainUserList.get(i).getNewLatitude();
                    double newLongitude=mainUserList.get(i).getNewLongitude();
                    LatLng latLngTest = new LatLng(newLatitude, newLongitude);
//                    MarkerOptions makerOptions = new MarkerOptions();
//                    makerOptions
//                            .snippet("When: " + newDate +
//                                    "\nWhere: " + newAddress)
//                            .position(latLngTest)
//                            .title(mainUser)
//                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//                    mMap.addMarker(makerOptions);
                    blueMarker = mMap.addMarker(new MarkerOptions()
                            .snippet("When: " + newDate +
                                    "\nWhere: " + newAddress)
                            .position(latLngTest)
                            .title(mainUser)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                );
            }
                for(int i=0;i<subUserList.size();i++){
                    String newAddress = subUserList.get(i).getNewAddress();
                    String newDate = subUserList.get(i).getNewDate();
                    double newLatitude=subUserList.get(i).getNewLatitude();
                    double newLongitude=subUserList.get(i).getNewLongitude();
                    LatLng latLngTest = new LatLng(newLatitude, newLongitude);
                    redMarker = mMap.addMarker(new MarkerOptions()
                            .snippet("When: " + newDate +
                                    "\nWhere: " + newAddress)
                            .position(latLngTest)
                            .title(subUser)
                    );
//                    MarkerOptions makerOptions = new MarkerOptions();
//                    makerOptions
//                            .snippet("When: " + newDate +
//                                    "\nWhere: " + newAddress)
//                            .position(latLngTest)
//                            .title(subUser);
//                    mMap.addMarker(makerOptions);
                }

                double l = mainUserList.get(mainUserList.size()-1).getNewLatitude();
                double r = mainUserList.get(mainUserList.size()-1).getNewLongitude();
                latLngMain = new LatLng(l, r);
                double q = subUserList.get(subUserList.size()-1).getNewLatitude();
                double w = subUserList.get(subUserList.size()-1).getNewLongitude();
                latLngSub = new LatLng(q, w);
                LatLngBounds.Builder builder = LatLngBounds.builder();
                builder.include(latLngMain);
                builder.include(latLngSub);
                LatLngBounds bounds = builder.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,200) );
//                distance.setText(mainUserList.get(2).getNewAddress());
//                time.setText(mainUserList.get(1).getNewAddress());
//                Log.d("fie",mainUserList.get(mainUserList.size()-1).getNewDate());
                drawRoute(latLngMain, latLngSub);
            }
        });

//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(@NonNull Marker marker) {
//                if (marker.equals(redMarker)) {
//                    streetViewPanoramaFragment.getView().bringToFront();
//                    showMap = false;
//                    return true;
//                } else {
//                    return false;
//                }
//
//            }
//        });
//
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                    streetViewPanoramaFragment.getView().bringToFront();
                    latLngBlue = marker.getPosition();
                    mStreetViewPanorama.setPosition(latLngBlue);
                }

        });
//
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

        distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mapFragment.getView().bringToFront();
            }
        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.getView().bringToFront();
            }
        });
    }

    public void drawRoute(LatLng origin, LatLng destination) {

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + origin.latitude + "," + origin.longitude + "&destination="
                + destination.latitude + "," + destination.longitude
                + "&mode=driving&key=AIzaSyCqDhpofV0nb7Ui_OT1qjslpPq80YXjSEQ";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Parse the JSON response and draw the route on the map
                        PolylineOptions polylineOptions = new PolylineOptions();
                        polylineOptions.color(Color.RED);
                        polylineOptions.width(10);
                        String km = "", min = "";
                        JSONArray routes = null;
                        try {
                            routes = response.getJSONArray("routes");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        for (int i = 0; i < routes.length(); i++) {
                            try {
                                JSONObject route = routes.getJSONObject(i);
                                JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                                String points = overviewPolyline.getString("points");
                                List<LatLng> path = PolyUtil.decode(points);
                                polylineOptions.addAll(path);

                                km = route.getJSONArray("legs")
                                        .getJSONObject(0)
                                        .getJSONObject("distance")
                                        .getString("text");
                                min = route.getJSONArray("legs")
                                        .getJSONObject(0)
                                        .getJSONObject("duration")
                                        .getString("text");

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        String distanceResult = "Distance: "+km;
                        String timeResult ="Driving time: "+min;
                        distance.setText(distanceResult);
                        time.setText(timeResult);
                        mMap.addPolyline(polylineOptions);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onStreetViewPanoramaReady(@NonNull StreetViewPanorama streetViewPanorama) {
        mStreetViewPanorama = streetViewPanorama;
        mStreetViewPanorama.setPosition(latLngRed);
    }

//    public void showNearby(View view) {
//        mapFragment.getView().bringToFront();
//        myLocationPlaceMap.getNearbyPlaces(mMap, "AIzaSyCqDhpofV0nb7Ui_OT1qjslpPq80YXjSEQ");
//    }

//    public void showMapStreetView(View view) {
//        showMap = !showMap;
//        if (showMap) {
//            mapFragment.getMapAsync(this);
//            mapFragment.getView().bringToFront();
//            btnMapStreetView.setText("Show Street View");
//        } else {
//            streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
//            streetViewPanoramaFragment.getView().bringToFront();
//            btnMapStreetView.setText("Show Map");
//        }
//    }

}