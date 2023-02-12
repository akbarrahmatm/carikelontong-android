package com.akbarrahmatm.projectuas_2112500851;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DirectionMobilActivity extends AppCompatActivity {

    private LocationRequest locationRequest;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction_mobil);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        progressDialog = new ProgressDialog(this, R.style.PrimaryProgressDialog);
        progressDialog.setMessage("Mohon Tunggu, Sedang Memuat Arah Yang Menggunakan Mobil");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        TextView namaTokoDirection = (TextView) findViewById(R.id.namaTokoDirection);
        Button btnKembali = (Button) findViewById(R.id.btnKembali);

        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        MapView map = (MapView) findViewById(R.id.mapdirection);




        map.setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String nama_toko = extras.getString("nama_toko");
            Double bujur = Double.valueOf(extras.getString("bujur"));
            Double lintang = Double.valueOf(extras.getString("lintang"));

            namaTokoDirection.setText(nama_toko);
            setRouteMaps(bujur, lintang, nama_toko);
        }
    }
    public void setRouteMaps(Double destLat, Double destLong, String namatoko) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(DirectionMobilActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(DirectionMobilActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {


                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(DirectionMobilActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() > 0) {

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();

                                        LatLng originLn = new LatLng(latitude, longitude);
                                        LatLng destLn = new LatLng(destLat, destLong);

                                        RoadManager roadManager = new OSRMRoadManager(DirectionMobilActivity.this, "MyOwnUserAgent/1.0");
                                        ((OSRMRoadManager)roadManager).setMean(OSRMRoadManager.MEAN_BY_CAR);

                                        MapView map = (MapView) findViewById(R.id.mapdirection);
                                        TextView duration = (TextView) findViewById(R.id.duration);
                                        TextView distance = (TextView) findViewById(R.id.distance);

                                        map.setVisibility(View.VISIBLE);

                                        map.setMultiTouchControls(true);


                                        map.invalidate();


                                        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();

                                        GeoPoint startPoint = new GeoPoint(latitude, longitude);
                                        waypoints.add(startPoint);
                                        GeoPoint endPoint = new GeoPoint(destLat, destLong);
                                        waypoints.add(endPoint);

                                        Marker startMarker = new Marker(map);
                                        startMarker.setPosition(startPoint);
                                        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_location_person));
                                        startMarker.setTitle("Lokasi Sekarang");
                                        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                        map.getOverlays().add(startMarker);

                                        Marker endMarker = new Marker(map);
                                        endMarker.setPosition(endPoint);
                                        endMarker.setIcon(getResources().getDrawable(R.drawable.ic_location));
                                        endMarker.setTitle(namatoko);
                                        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                        map.getOverlays().add(endMarker);

                                        Road road = roadManager.getRoad(waypoints);
                                        Double durationVal = (road.mDuration / 60) * 2;
                                        Double distanceVal = road.mLength;

                                        DecimalFormat formatVal = new DecimalFormat("#.##");


                                        String resultDuration = String.valueOf(formatVal.format(durationVal)) + " Menit";
                                        String resultDistance = String.valueOf(formatVal.format(distanceVal)) + " Km";

                                        duration.setText(resultDuration);
                                        distance.setText(resultDistance);

                                        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
                                        roadOverlay.getOutlinePaint().setColor(Color.parseColor("#FF3700B3"));
                                        roadOverlay.getOutlinePaint().setStrokeWidth(12);
                                        map.getOverlays().remove(roadOverlay);
                                        map.getOverlays().add(roadOverlay);


                                        IMapController mapController = map.getController();
                                        mapController.setZoom(19);

                                        mapController.setCenter(startPoint);

                                        progressDialog.dismiss();
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }
            }else{
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }
        }

    }
    private void turnOnGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(DirectionMobilActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(DirectionMobilActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }
}