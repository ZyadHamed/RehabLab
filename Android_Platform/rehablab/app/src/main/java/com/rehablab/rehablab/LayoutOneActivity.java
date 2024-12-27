package com.rehablab.rehablab;  // تأكد من أن الباكيج هو نفسه في مشروعك

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class LayoutOneActivity extends AppCompatActivity {

    private Button buttonBackToMain;
    private Button buttonToAngle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_one);  // تعيين الـ layout الأول

        // Initialize buttons
        buttonBackToMain = findViewById(R.id.buttonBackToMain);
        buttonToAngle = findViewById(R.id.buttonToAngle);

        // Set up Next button click listener
        buttonToAngle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LayoutOneActivity.this, AngleActivity1.class));
            }
        });

        // Set up Back button click listener
        buttonBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Changed to finish() instead of starting new MainActivity
            }
        });
    }
}