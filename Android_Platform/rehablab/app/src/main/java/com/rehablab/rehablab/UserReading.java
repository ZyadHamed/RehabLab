package com.rehablab.rehablab;

import androidx.annotation.NonNull;
import com.google.firebase.database.*;
import com.google.firebase.database.ChildEventListener;

import java.text.SimpleDateFormat;
import java.util.*;


public class UserReading {
    public float gyroX;
    public float gyroY;
    public float gyroZ;
    public Date ReadingTime;

    private static final String TAG = "UserReading";
    private static final List<UserReading> allReadings = new ArrayList<>();
    private static UserReading currentReading = null;
    private static ChildEventListener activeListener;
    private static DatabaseReference databaseRef;
    private static Date selectedDate;


    public UserReading() {
        this.ReadingTime = new Date();
    }

    public UserReading(float gyroX, float gyroY, float gyroZ, Date readingTime) {
        this.gyroX = gyroX;
        this.gyroY = gyroY;
        this.gyroZ = gyroZ;
        this.ReadingTime = readingTime;
    }

    // Public interface methods
    public static void initialize(DatabaseReference dbRef, Date date) {
        databaseRef = dbRef;
        selectedDate = date != null ? date : new Date(); // Use provided date or current date if null
        startDataFetching();
    }

    public static void cleanup() {
        if (activeListener != null && databaseRef != null) {
            databaseRef.removeEventListener(activeListener);
            activeListener = null;
        }
        databaseRef = null;
        selectedDate = null;

    }

    public static UserReading getCurrentReading() {
        return currentReading;
    }

    public static List<UserReading> getAllReadings() {
        return new ArrayList<>(allReadings);
    }

    // Private implementation methods
    private static void startDataFetching() {
        if (databaseRef == null) {
            System.err.println(TAG + ": Database reference is null. Initialization required.");
            return;
        }

        if (activeListener != null) {
            databaseRef.removeEventListener(activeListener);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String todayNodeKey = dateFormat.format(selectedDate);

        activeListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                UserReading reading = parseReading(snapshot.getValue(String.class), snapshot.getKey());
                if (reading != null && isNewReading(reading)) {
                    allReadings.add(reading);
                    currentReading = reading;
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                UserReading reading = parseReading(snapshot.getValue(String.class), snapshot.getKey());
                if (reading != null) {
                    updateExistingReading(reading);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println(TAG + ": Database error: " + error.getMessage());
            }
        };

        databaseRef.child(todayNodeKey).addChildEventListener(activeListener);
        fetchInitialData(todayNodeKey);
    }

    private static void fetchInitialData(String todayNodeKey) {
        databaseRef.child(todayNodeKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allReadings.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserReading reading = parseReading(snapshot.getValue(String.class), snapshot.getKey());
                    if (reading != null) {
                        allReadings.add(reading);
                        currentReading = reading;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println(TAG + ": Initial data fetch failed: " + error.getMessage());
            }
        });
    }

    private static boolean isNewReading(UserReading reading) {
        return currentReading == null ||
                reading.ReadingTime.after(currentReading.ReadingTime);
    }

    private static void updateExistingReading(UserReading reading) {
        int index = findReadingIndex(reading.ReadingTime);
        if (index != -1) {
            allReadings.set(index, reading);
            if (index == allReadings.size() - 1) {
                currentReading = reading;
            }
        }
    }

    private static int findReadingIndex(Date timestamp) {
        for (int i = 0; i < allReadings.size(); i++) {
            if (allReadings.get(i).ReadingTime.equals(timestamp)) {
                return i;
            }
        }
        return -1;
    }

    private static UserReading parseReading(String value, String timeKey) {
        if (value != null) {
            String[] parts = value.split(";");
            if (parts.length == 3) {
                try {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss:S", Locale.getDefault());
                    Date readingTime = timeFormat.parse(timeKey);
                    return new UserReading(
                            Float.parseFloat(parts[0]),
                            Float.parseFloat(parts[1]),
                            Float.parseFloat(parts[2]),
                            readingTime
                    );
                } catch (Exception e) {
                    System.err.println(TAG + ": Failed to parse reading: " + value + " or time key: " + timeKey);
                }
            }
        }
        return null;
    }
}
