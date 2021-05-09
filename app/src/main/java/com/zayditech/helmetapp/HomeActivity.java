package com.zayditech.helmetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    // Variable Initialization
    FirebaseDatabase database;
    DatabaseReference ref;
    TextView bpSistolic;
    TextView bpDiasystolicTxt;
    TextView temperature;
    TextView heartbeat;
    Intent mServiceIntent;
    private NotificationService mNotificationService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.getSupportActionBar().hide();
        // Service Starter
        mNotificationService = new NotificationService();
        mServiceIntent = new Intent(this, mNotificationService.getClass());
        if (!isMyServiceRunning(mNotificationService.getClass())) {
            startService(mServiceIntent);
        }
        // Variable Declaration
        database = FirebaseDatabase.getInstance();
        bpSistolic = findViewById(R.id.bpSistolicTxt);
        bpDiasystolicTxt = findViewById(R.id.bpDiasystolicTxt);
        temperature = findViewById(R.id.tempTxt);
        heartbeat = findViewById(R.id.hbTxt);

        // For Sistolic Blood Pressure
        ref = database.getReference("Blood Pressure").child("Sistolic");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String response = snapshot.getValue(String.class);
                bpSistolic.setText(response);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // For Diasystolic Blood Pressure
        ref = database.getReference("Blood Pressure").child("Diasystolic");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String response = snapshot.getValue(String.class);
                bpDiasystolicTxt.setText(response);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // For Heart Beat
        ref = database.getReference("Pulse").child("HeartBeat");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String response = snapshot.getValue(String.class);
                heartbeat.setText(response);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // For Temperature
        ref = database.getReference("Temp").child("Temperature");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String response = snapshot.getValue(String.class);
                temperature.setText(response);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        sendBroadcast(broadcastIntent);
        super.onDestroy();
    }
}