package com.akbarrahmatm.projectuas_2112500851;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akbarrahmatm.projectuas_2112500851.directionhelper.FetchURL;
import com.akbarrahmatm.projectuas_2112500851.directionhelper.TaskLoadedCallback;
import com.akbarrahmatm.projectuas_2112500851.model.ListDetailTokoModel;
import com.akbarrahmatm.projectuas_2112500851.network.ApiClient;
import com.akbarrahmatm.projectuas_2112500851.network.ApiService;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private GoogleMap mMap;
    Polyline currentPolyline;

    TextView tvDetailNamaToko, tvDetailAlamatToko, tvDetailMinuman, tvDetailEsKrim, tvDetailGas, tvDetailBensin, tvDetailPulsa;
    Button btnCekHarga, btnKembali;
    private LocationRequest locationRequest;
    private MarkerOptions originLoc, destLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        btnKembali = findViewById(R.id.btnKembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        getDetailDataLocation();

    }

    private void getDetailDataLocation() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mohon Tunggu, Sedang memuat Detail Toko");
        progressDialog.show();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String id_toko = extras.getString("id_toko");

            ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
            Call<ListDetailTokoModel> call = apiService.getDetailLocation(id_toko);
            call.enqueue(new Callback<ListDetailTokoModel>() {
                @Override
                public void onResponse(Call<ListDetailTokoModel> call, Response<ListDetailTokoModel> response) {
                    progressDialog.dismiss();

                    setRouteMaps(Double.valueOf(response.body().getData().get(0).getBujur()), Double.valueOf(response.body().getData().get(0).getLintang()));

                    initMarker(Double.valueOf(response.body().getData().get(0).getBujur()), Double.valueOf(response.body().getData().get(0).getLintang()), response.body().getData().get(0).getNamaToko());
                    tvDetailNamaToko = findViewById(R.id.tvDetailNamaToko);
                    tvDetailAlamatToko = findViewById(R.id.tvDetailAlamatToko);
                    tvDetailMinuman = findViewById(R.id.tvDetailMinuman);
                    tvDetailEsKrim = findViewById(R.id.tvDetailEsKrim);
                    tvDetailGas = findViewById(R.id.tvDetailGas);
                    tvDetailBensin = findViewById(R.id.tbDetailBensin);
                    tvDetailPulsa = findViewById(R.id.tvDetailPulsa);

                    tvDetailNamaToko.setText(response.body().getData().get(0).getNamaToko());
                    tvDetailAlamatToko.setText(response.body().getData().get(0).getAlamatToko());

                    String resMinuman = response.body().getData().get(0).getFiturMinumanDingin();
                    String resEsKrim = response.body().getData().get(0).getFiturEsKrim();
                    String resGas = response.body().getData().get(0).getFiturGasGalon();
                    String resBensin = response.body().getData().get(0).getFiturBensin();
                    String resPulsa = response.body().getData().get(0).getFiturPulsa();

//                  // Cek Minuman
                    if (resMinuman.contains("true")) {
                        tvDetailMinuman.setText("Tersedia Minuman Dingin");
                    } else {
                        tvDetailMinuman.setText("Tidak Tersedia Minuman Dingin");
                    }

                    // Cek EsKrim
                    if (resEsKrim.contains("true")) {
                        tvDetailEsKrim.setText("Tersedia Pendingin Es Krim");
                    } else {
                        tvDetailEsKrim.setText("Tidak Tersedia Pendingin Es Krim");
                    }

                    // Cek Gas
                    if (resGas.contains("true")) {
                        tvDetailGas.setText("Tersedia Gas LPG & Galon");
                    } else {
                        tvDetailGas.setText("Tidak Tersedia Gas LPG & Galon");
                    }

                    // Cek Bensin
                    if (resBensin.contains("true")) {
                        tvDetailBensin.setText("Tersedia Bensin Eceran");
                    } else {
                        tvDetailBensin.setText("Tidak Tersedia Bensin Eceran");
                    }

                    // Cek Pulsa
                    if (resPulsa.contains("true")) {
                        tvDetailPulsa.setText("Tersedia Pulsa & Token Listrik");
                    } else {
                        tvDetailPulsa.setText("Tidak Tersedia Pulsa & Token Listrik");
                    }

                    btnCekHarga = findViewById(R.id.btnCekHarga);

                    btnCekHarga.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(DetailActivity.this, ProdukActivity.class);
                            i.putExtra("id_toko", response.body().getData().get(0).getIdToko());
                            i.putExtra("nama_toko", response.body().getData().get(0).getNamaToko());
                            startActivity(i);
                        }
                    });
                }

                @Override
                public void onFailure(Call<ListDetailTokoModel> call, Throwable t) {
                    progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);

                    builder.setTitle("Terjadi Kesalahan")
                            .setMessage("Mohon maaf, nampaknya terjadi kesalahan")
                            .setIcon(R.drawable.alert_new)
                            .setCancelable(false)
                            .setPositiveButton("YA", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        } else {
            finish();
        }

    }

    public void setRouteMaps(Double destLat, Double destLong) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(DetailActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {


                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(DetailActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() > 0) {

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();

                                        LatLng originLn = new LatLng(latitude, longitude);
                                        LatLng destLn = new LatLng(destLat, destLong);


                                        originLoc = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Lokasi Sekarang");
                                        destLoc = new MarkerOptions().position(new LatLng(destLat, destLong));

                                        if (ActivityCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                            return;
                                        }

                                        new FetchURL(DetailActivity.this).execute(getUrl(originLoc.getPosition(), destLoc.getPosition(), "driving"), "driving");

                                        mMap.addMarker(originLoc);

                                        mMap.setMyLocationEnabled(true);
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
                    Toast.makeText(DetailActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(DetailActivity.this, 2);
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
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("storage","Permission is granted");
                return true;
            } else {

                Log.v("storage","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("storage","Permission is granted");
            return true;
        }
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }


    private void initMarker(Double bujur, Double lintang, String namatoko) {
        LatLng location = new LatLng(bujur, lintang);
        mMap.addMarker(new MarkerOptions().position(location).title(namatoko));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}