package com.example.sampleassignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SQLiteActivity extends AppCompatActivity {
    ArrayList<String> users = new ArrayList<>();
    MyDbHelper db = new MyDbHelper(this, "Users", null, 1);
    EditText editText1;
    EditText editText2;
    EditText editText3;

    long id1;
    long id2;
    long id3;

    RadioButton button1;
    RadioButton button2;
    RadioButton button3;

    RadioGroup radioGroup;

    String selectedUser;

    TextView currentUser;

    Button startButton;

    boolean isClicked = false;

    int whereIsSelected;



//    RadioButton button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);
        editText1 = findViewById(R.id.editTextFirstName);
        editText2 = findViewById(R.id.editTextSecondName);
        editText3 = findViewById(R.id.editTextThirdName);

        radioGroup = findViewById(R.id.radioGroup);

//        radioGroup.getCheckedRadioButtonId()
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = findViewById(checkedId);
                selectedUser = selectedRadioButton.getText().toString();
            }
        });

        currentUser =findViewById(R.id.textView2);
        startButton =findViewById(R.id.buttonStart);
        currentUser.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.INVISIBLE);
        radioGroup.setVisibility(View.INVISIBLE);
//        db.deleteAllUsers();
        users = db.getAllUsers();
        if(users.isEmpty()){

        }else {
            editText1.setText(users.get(0));
            editText2.setText(users.get(1));
            editText3.setText(users.get(2));
        }


    }

    public void saveUsers(View view){
        if (db==null){
            db.insertUser(editText1.getText().toString());
            db.insertUser(editText2.getText().toString());
            db.insertUser(editText3.getText().toString());
        }else{
            db.deleteAllUsers();
            db.insertUser(editText1.getText().toString());
            db.insertUser(editText2.getText().toString());
            db.insertUser(editText3.getText().toString());
        }
        users = db.getAllUsers();
        editText1.setText(users.get(0));
        editText2.setText(users.get(1));
        editText3.setText(users.get(2));
//        if(isClicked){
//
//        }
        if (editText1.getText().toString().equals("") || editText2.getText().toString().equals("")|| editText3.getText().toString().equals("")) {

            return;
        }else{
//            if(users.size()==3){
                button1 = findViewById(R.id.radioButton3);
                button2 = findViewById(R.id.radioButton4);
                button3 = findViewById(R.id.radioButton5);
                button1.setText(users.get(0));
                button2.setText(users.get(1));
                button3.setText(users.get(2));
                currentUser.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.VISIBLE);
                radioGroup.setVisibility(View.VISIBLE);
//            } else if (users.size()==4) {
//
//                button1 = findViewById(R.id.radioButton3);
//                button2 = findViewById(R.id.radioButton4);
//                button3 = findViewById(R.id.radioButton5);
//                button1.setText(users.get(0));
//                button2.setText(users.get(1));
//                button3.setText(users.get(2));
////                for (int i=0;i<3;i++){
////                    if(users.get(i)==selectedUser){
////                        whereIsSelected = i;
////                    }
////                }
////                if (whereIsSelected==0){
////                    radioGroup.check(R.id.radioButton3);
////                } else if (whereIsSelected==1) {
////                    radioGroup.check(R.id.radioButton4);
////                } else if (whereIsSelected==2) {
////                    radioGroup.check(R.id.radioButton5);
////                }
//                currentUser.setVisibility(View.VISIBLE);
//                startButton.setVisibility(View.VISIBLE);
//                radioGroup.setVisibility(View.VISIBLE);
//            }
            isClicked = true;

                }



    }
//
//    public void showRadioGroup(View view){
//        EditText editText1 = findViewById(R.id.editTextFirstName);
//        EditText editText2 = findViewById(R.id.editTextSecondName);
//        EditText editText3 = findViewById(R.id.editTextThirdName);
//        //TextView textView = findViewById(R.id.textViewColours);
//
//        String name1 = editText1.getText().toString();
//        String name2 = editText2.getText().toString();
//        String name3 = editText3.getText().toString();
//        if (name1.equals("") || name2.equals("")|| name3.equals("")) {
//            return;
//        }
//
//
//        MyDbHelper db = new MyDbHelper(getApplicationContext(), "MobileTech", null, 1);
//        if (db != null) {
// //           db.deleteAllColours();
////            db.insertColour(editText1.getText().toString());
////            db.insertColour(editText2.getText().toString());
////            db.insertColour(editText3.getText().toString());
// //           ArrayList<String> colours = db.getAllColours();
//
////            editText1.setText(colours.get(0));
////            editText2.setText(colours.get(1));
////            editText3.setText(colours.get(2));
////            button1 = findViewById(R.id.radioButton3);
////            RadioButton button2 = findViewById(R.id.radioButton4);
////            RadioButton button3 = findViewById(R.id.radioButton5);
////            button1.setText(colours.get(0));
////            button2.setText(colours.get(1));
////            button3.setText(colours.get(2));
//        }
//    }
    public void openMainActivity(View view) {
        if(selectedUser==null){
            return;
        }
        int whereIsTheUser = users.indexOf(selectedUser);
        users.remove(whereIsTheUser);
        Intent intent =new Intent(this, MainActivity.class);
        intent.putExtra("firstName", selectedUser);
        intent.putExtra("secondName", users.get(0));
        intent.putExtra("thirdName", users.get(1));
        startActivity(intent);
        users.add(whereIsTheUser,selectedUser);}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
// Save the current state
        outState.putBoolean("button_clicked", isClicked);
        outState.putString("selected_user", selectedUser);

        super.onSaveInstanceState(outState);


    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
// Restore the saved state
        isClicked = savedInstanceState.getBoolean("button_clicked");
        selectedUser = savedInstanceState.getString("selected_user");

        if (isClicked){
            button1 = findViewById(R.id.radioButton3);
            button2 = findViewById(R.id.radioButton4);
            button3 = findViewById(R.id.radioButton5);
            button1.setText(users.get(0));
            button2.setText(users.get(1));
            button3.setText(users.get(2));
            currentUser.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.VISIBLE);
            radioGroup.setVisibility(View.VISIBLE);

        }
    }



}