package com.antiklu.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static com.antiklu.driver.SpawnActivity.my_shared_preferences;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    Boolean session = false;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean("login", false);
        pd = new ProgressDialog(SplashActivity.this);
        askPermission();

        //3000 L = 3 detik
    }

    void askPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, 69);
        } else {
            getLocation();
        }
    }

    public void checkPermission(String permission, int requestCode) {

        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(),
                permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(
                            SplashActivity.this,
                            new String[]{permission},
                            requestCode);
        } else {
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == 69) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                getLocation();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Izin Lokasi Ditolak",
                        Toast.LENGTH_LONG)
                        .show();
                finish();
            }
        }
    }

    void getLocation() {
        FusedLocationProviderClient mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        pd.setMessage("Mendapatkan Lokasi anda . Harap Menunggu");
        pd.show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getApplicationContext(),
                    "Belum memiliki izin lokasi",
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }
        mFusedLocation.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                pd.dismiss();
                if (location != null) {

                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString("latitude", String.valueOf(location.getLatitude()));
                    editor.putString("longitude", String.valueOf(location.getLongitude()));
                    editor.commit();
                    Log.d("My Current location", "Lat : " + location.getLatitude() + " Long : " + location.getLongitude());

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (session) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {

                                startActivity(new Intent(getApplicationContext(), SpawnActivity.class));
                                finish();
                            }

                        }
                    }, 3000L);


                    // Do it all with location

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Gagal Mendapatkan Lokasi . Pastikan Lokasi di Setting diaktifkan untuk menggunakan aplikasi ini",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}