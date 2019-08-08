package com.example.vnsproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button screenShotButton;
    private View main;
    private ImageView imageView;
    private Screenshot screenshot;
    private final int MY_PERMISSION_REQUEST = 1;
    private LocationManager locationManager;
    private LocationListener listenerReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main = findViewById(R.id.mainLayout);
        screenShotButton = findViewById(R.id.screenShotBtn);
        imageView = findViewById(R.id.imageView);
        screenshot = new Screenshot();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //Asking for external storage write & read permission
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST);
        } else {
            screeshotCode();
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    public void screeshotCode() {
        screenShotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View onClick) {
                Bitmap b = screenshot.returnRootViewOfScreenshot(main, getApplicationContext());
                MyLocationListener myLocationListener = new MyLocationListener();
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    Log.e("location request code", "Code got hit under requestLocationUpdates() GPS");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, myLocationListener);
                    //listenerReference = myLocationListener;
                if (b != null)
                    Toast.makeText(getApplicationContext(), "Screenshot taken", Toast.LENGTH_SHORT).show();
                //main.setBackgroundColor(Color.parseColor("#999999"));
                imageView.setImageBitmap(b);

                //geotagging the screenshot taken

            }
        });
        //making the screenshot taken available in gallery
        if (imageView.getDrawable() != null) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(screenshot.getImageFilePath());
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && (grantResults[0] & grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                    screeshotCode();
                } else {
                    Toast.makeText(getApplicationContext(), "Permissions not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void geoTagImage(String filePath, Location location) {
        ExifInterface exifInterface;
        try {
            exifInterface = new ExifInterface(filePath);
            exifInterface.setLatLong(location.getLatitude(), location.getLongitude());
            exifInterface.saveAttributes();
            Log.e("exif code", "Code got hit for exif!");
            //locationManager.removeUpdates(listener);
            imageView.setImageBitmap(null);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("exif error", e.getMessage());

        }
    }


    private class MyLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            Log.e("GPS Location code", "Code got hit under onLocationChanged()");
            geoTagImage(screenshot.getImageFilePath(), location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, please enable it.")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        System.exit(0);
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void displayFiles(View view){
        Intent intent = new Intent(this, DisplayFiles.class);
        startActivity(intent);
    }

}
