package com.rehablab.rehablab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonOne = findViewById(R.id.buttonLayoutOne);
        Button buttonTwo = findViewById(R.id.buttonLayoutTwo);
        Button buttonThree = findViewById(R.id.buttonLayoutThree);
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.DECEMBER, 22);


        UserReading.initialize(FirebaseDatabase.getInstance().getReference(),new Date());

        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LayoutOneActivity.class));
            }
        });

        buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LayoutTwoActivity.class));
            }
        });

        buttonThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LayoutThreeActivity.class));
            }
        });
    }
}