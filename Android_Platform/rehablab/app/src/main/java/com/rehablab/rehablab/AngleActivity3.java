package com.rehablab.rehablab;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;

public class AngleActivity3 extends AppCompatActivity {
    private TextView angleReading;
    private TextView statusIndicator;
    private TextView rangeInfo;
    
    // Define acceptable range for head rotation
    private final double MIN_ACCEPTABLE_ANGLE_HEAD_X = -7;
    private final double MAX_ACCEPTABLE_ANGLE_HEAD_X = 7;
    private final double MIN_ACCEPTABLE_ANGLE_HEAD_Y = -97;
    private final double MAX_ACCEPTABLE_ANGLE_HEAD_Y = 97;
    private final double MIN_ACCEPTABLE_ANGLE_HEAD_Z = -7;
    private final double MAX_ACCEPTABLE_ANGLE_HEAD_Z = 7;
    private static final int UPDATE_INTERVAL_MS = 100;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.angle_layout3);

        // Initialize views
        angleReading = findViewById(R.id.angleReading);
        statusIndicator = findViewById(R.id.statusIndicator);
        rangeInfo = findViewById(R.id.rangeInfo);
        Button buttonBack = findViewById(R.id.buttonBack);
        mainHandler = new Handler(Looper.getMainLooper());
        startPeriodicUpdates();


        rangeInfo.setText("Acceptable range: " + MIN_ACCEPTABLE_ANGLE_HEAD_X + "° : " + MAX_ACCEPTABLE_ANGLE_HEAD_X + "°");

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void updateAngleReading(float x , float y , float z) {
        // Update angle display
        angleReading.setText(x + "°");

        if (y < MIN_ACCEPTABLE_ANGLE_HEAD_Y ){
            statusIndicator.setText("Your hand is rotated to the left!!");
            statusIndicator.setBackgroundColor(Color.parseColor("#FFE0E0")); // Light red
            statusIndicator.setTextColor(Color.parseColor("#D32F2F")); // Dark red
        }else if (y > MAX_ACCEPTABLE_ANGLE_HEAD_Y) {
            statusIndicator.setText("Your hand is rotated to the right!!");
            statusIndicator.setBackgroundColor(Color.parseColor("#FFE0E0")); // Light red
            statusIndicator.setTextColor(Color.parseColor("#D32F2F")); // Dark red
        } else { if (z < MIN_ACCEPTABLE_ANGLE_HEAD_Z ){
            statusIndicator.setText("Your hand is tilted to the left!!");
            statusIndicator.setBackgroundColor(Color.parseColor("#FFE0E0")); // Light red
            statusIndicator.setTextColor(Color.parseColor("#D32F2F")); // Dark red
        }else if (z > MAX_ACCEPTABLE_ANGLE_HEAD_Z) {
            statusIndicator.setText("Your hand is tilted to the right!!");
            statusIndicator.setBackgroundColor(Color.parseColor("#FFE0E0")); // Light red
            statusIndicator.setTextColor(Color.parseColor("#D32F2F")); // Dark red
        }
        else {
            if (x >= MIN_ACCEPTABLE_ANGLE_HEAD_X && x <= MAX_ACCEPTABLE_ANGLE_HEAD_X) {
                statusIndicator.setText("GOOD");
                statusIndicator.setBackgroundColor(Color.parseColor("#C8E6C9")); // Light green
                statusIndicator.setTextColor(Color.parseColor("#2E7D32")); // Dark green
            } else {
                statusIndicator.setText("BAD");
                statusIndicator.setBackgroundColor(Color.parseColor("#FFE0E0")); // Light red
                statusIndicator.setTextColor(Color.parseColor("#D32F2F")); // Dark red
            }

        }


        }}
    private void startPeriodicUpdates() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                UserReading angles = UserReading.getCurrentReading();
                updateAngleReading(angles.gyroX,angles.gyroY,angles.gyroZ); // Example angle
                mainHandler.postDelayed(this, UPDATE_INTERVAL_MS);
            }
        });
    }
}
