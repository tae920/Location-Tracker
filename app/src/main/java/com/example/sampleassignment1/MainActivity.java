package com.example.sampleassignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

//    ArrayList<String> users = new ArrayList<>();
//
//    MyDbHelper db = new MyDbHelper(this, "Users", null, 1);

    MyLocationPlaceMap myLocationPlaceMap;
    ArrayList<MyLocationPlace> myLocations = new ArrayList<>();
    MyLocationPlace myLocation;

    String selectedUser;

    String finalUser;
    String firstName;

    String secondName;

    String thirdName;

    Button firstButton;

    Button secondButton;

    Button thirdButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Location Tracker");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            firstName = extras.getString("firstName");
            secondName = extras.getString("secondName");
            thirdName = extras.getString("thirdName");
    }
//        Intent intent =getIntent();
//        firstName = intent.getStringExtra("firstName");
//        secondName = intent.getStringExtra("secondName");
//        thirdName = intent.getStringExtra("thirdName");
        firstButton = findViewById(R.id.buttonWhereAmI);
        firstButton.setText("Where Am I ("+firstName+")?");
        secondButton = findViewById(R.id.buttonWhereIsUser2);
        secondButton.setText("Where Is "+secondName+"?");
        thirdButton = findViewById(R.id.buttonWhereIsUser3);
        thirdButton.setText("Where Is "+thirdName+"?");
//        users = db.getAllUsers();
//        int whereIsTheUser = users.indexOf(selectedUser);
//        users.remove(whereIsTheUser);
////        users.add(0,selectedUser);
//        firstButton = findViewById(R.id.buttonWhereAmI);
//        firstButton.setText("Where Am I ("+selectedUser+")?");
//        secondButton = findViewById(R.id.buttonWhereIsUser2);
//        secondButton.setText("Where Is "+users.get(0)+"?");
//        thirdButton = findViewById(R.id.buttonWhereIsUser3);
//        thirdButton.setText("Where Is "+users.get(1)+"?");
//        users.add(whereIsTheUser,selectedUser);
//
//        finalUser = users.get(0);

        myLocationPlaceMap = new MyLocationPlaceMap(getApplicationContext(), MainActivity.this);
        myLocationPlaceMap.requestPermissions();
        myLocationPlaceMap.getLatLngAddress(myLocations);
    }

    public void whereAmI (View view) {
        myLocationPlaceMap.getLatLngAddress(myLocations);

        if (myLocations.size() > 0) {
            myLocation = myLocations.get(0);
            myLocations.clear();

            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("theUser",firstName);
            intent.putExtra("lat", myLocation.getLatitude());
            intent.putExtra("lng", myLocation.getLongitude());
            intent.putExtra("addr", myLocation.getAddress());
            startActivity(intent);
        }
    }
    public void getDirectionOne (View view){
        Intent intent =new Intent(this, DirectionActivity.class);
        intent.putExtra("mainUser",firstName);
        intent.putExtra("subUser",secondName);
        startActivity(intent);
    }
    public void getDirectionTwo (View view){
        Intent intent =new Intent(this, DirectionActivity.class);
        intent.putExtra("mainUser",firstName);
        intent.putExtra("subUser",thirdName);
        startActivity(intent);
    }

}