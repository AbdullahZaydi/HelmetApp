package com.zayditech.helmetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import java.io.File;
import java.io.IOException;
public class HomeActivity extends AppCompatActivity {
    // Variable Initialization
    FirebaseDatabase database;
    DatabaseReference ref;
    TextView bpSistolic;
    TextView bpDiasystolicTxt;
    TextView name;
    TextView temperature;
    TextView datetime;
    TextView heartbeat;
    Intent mServiceIntent;
    CircularImageView imageView;
    Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseUser currentFirebaseUser;
    private NotificationService mNotificationService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.getSupportActionBar().hide();
        // Service Starter
        imageView = findViewById(R.id.img_profile);
        name = findViewById(R.id.name);
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        name.setText(currentFirebaseUser.getEmail());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
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
        datetime = findViewById(R.id.datetime);
        heartbeat = findViewById(R.id.hbTxt);

        StorageReference fileRef = storageReference.child("images/" + currentFirebaseUser.getUid());
        try {
            File localFile = File.createTempFile("images", "jpg");
            fileRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            }).addOnProgressListener(new OnProgressListener() {
                @Override
                public void onProgress(@NonNull Object snapshot) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        // For Sistolic Blood Pressure
        ref = database.getReference("Blood Pressure").child("Sistolic");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String response = snapshot.getValue(String.class);
                bpSistolic.setText(response + " 'S");
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
                bpDiasystolicTxt.setText(response+ " 'D");
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
                heartbeat.setText(response+ " 'P");
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
                temperature.setText(response + " 'T");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // For DateTime
        ref = database.getReference("Date and Time").child("Date");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String date = snapshot.getValue(String.class);
                ref = database.getReference("Date and Time").child("Time");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String time = snapshot.getValue(String.class);
                        datetime.setText("Last Accident Details: " + date + " " + time);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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

    public void pickFromGallery(View view) {
        ImagePicker.with(this).galleryOnly().start(1002);
    }
    public void pickFromCamera(View view) {
        ImagePicker.with(this).cameraOnly().start(1001);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Uri selectedImage = imageReturnedIntent.getData();
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            imageView.setImageBitmap(bitmap);
            filePath = selectedImage;
            uploadImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadImage()
    {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                        + currentFirebaseUser.getUid());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(HomeActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(HomeActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }
    public void ShowMap(View view) {
        ref = database.getReference("GPS").child("Latitude");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String lat = snapshot.getValue(String.class);
                ref = database.getReference("GPS").child("Longitude");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String lng = snapshot.getValue(String.class);
                        String label = "Current Location";
                        String uriBegin = "geo:"+lat+","+lng;
                        String query = lat+","+lng+"(" + label + ")";
                        String encodedQuery = Uri.encode(query);
                        String uriString = uriBegin + "?q=" + encodedQuery;
                        Uri uri = Uri.parse(uriString);
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}