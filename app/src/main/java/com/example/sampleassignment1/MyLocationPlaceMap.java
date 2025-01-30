package com.example.sampleassignment1;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MyLocationPlaceMap {
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallback;
    boolean requestingLocationUpdates = false;
    private double latitude;
    public double longitude;
    public String address;

    private static final int MAX_PLACES = 5;
    Context context;
    Activity activity;

    public double getLatitude() {
        return latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public MyLocationPlace myLocation = new MyLocationPlace(0, 0, "");

    public MyLocationPlaceMap(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public boolean requestPermissions() {
        int REQUEST_PERMISSION = 3000;
        String permissions[] = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};
        boolean grantFinePermission =
                ContextCompat.checkSelfPermission(context, permissions[0]) == PackageManager.PERMISSION_GRANTED;
        boolean grantCoarsePermission =
                ContextCompat.checkSelfPermission(context, permissions[1]) == PackageManager.PERMISSION_GRANTED;

        if (!grantFinePermission && !grantCoarsePermission) {
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSION);
        } else if (!grantFinePermission) {
            ActivityCompat.requestPermissions(activity, new String[]{permissions[0]}, REQUEST_PERMISSION);
        } else if (!grantCoarsePermission) {
            ActivityCompat.requestPermissions(activity, new String[]{permissions[1]}, REQUEST_PERMISSION);
        }

        return grantFinePermission && grantCoarsePermission;
    }

    public void createLocationRequestLocationCallback() {
        locationRequest = LocationRequest.create();
        long ms = 10000; // milliseconds
        // Sets the desired interval for active location updates.
        locationRequest.setInterval(ms);
        // Sets the fastest rate for active location updates.
        locationRequest.setFastestInterval(ms / 2);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
            }
        };
    }

    public synchronized void getLatLngAddress(ArrayList<MyLocationPlace> myLocations) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        createLocationRequestLocationCallback();
                        startLocationUpdates();
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            address = getStreetAddress(latitude, longitude);
                            myLocation = new MyLocationPlace(latitude, longitude, address);
                            myLocations.add(myLocation);
                        }
                    }
                });
    }

    public String getStreetAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String streetAddress = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address address = addresses.get(0);
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    streetAddress += address.getAddressLine(i);
                }
            } else {
                streetAddress = "Unknown";
            }
        } catch (Exception e) {
            streetAddress = "Service not available";
            e.printStackTrace();
        }
        return streetAddress;
    }


    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }
    public void getNearbyPlaces(GoogleMap mMap, String apiKey) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Places.initialize(context, apiKey);
        PlacesClient placesClient = Places.createClient(context);

        // Define 3 place fields then make a request
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.LAT_LNG);
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        // Get the nearby places including businesses and other points of interest
        // that are the best match for the device's current location.
        final Task<FindCurrentPlaceResponse> placeResult = placesClient.findCurrentPlace(request);
        placeResult.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
            @Override
            public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    FindCurrentPlaceResponse nearbyPlaces = task.getResult();

                    // Set the count, handling cases where less than MAX_PLACES entries
                    int count;
                    if (nearbyPlaces.getPlaceLikelihoods().size() < MAX_PLACES) {
                        count = nearbyPlaces.getPlaceLikelihoods().size();
                    } else {
                        count = MAX_PLACES;
                    }

                    int i = 0;
                    String name, address;
                    LatLng latlng;
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    for (PlaceLikelihood placeLikelihood : nearbyPlaces.getPlaceLikelihoods()) {
                        // Get a nearby place
                        name = placeLikelihood.getPlace().getName();
                        address = placeLikelihood.getPlace().getAddress();
                        latlng = placeLikelihood.getPlace().getLatLng();

                        // Add a marker with an info window showing place address
                        mMap.addMarker(new MarkerOptions()
                                .title(name)
                                .position(latlng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                .snippet(address));

                        builder.include(latlng);

                        i++;
                        if (i >= count) {
                            break;
                        }
                    }
                    // Set the greatest zoom level
                    LatLngBounds bounds = builder.build();
                    int padding = 250;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                } else {
                    Log.e(TAG, "Exception: %s", task.getException());
                }
            }
        });
    }

}
