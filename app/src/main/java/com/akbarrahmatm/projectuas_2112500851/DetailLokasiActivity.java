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
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akbarrahmatm.projectuas_2112500851.model.ListDetailTokoModel;
import com.akbarrahmatm.projectuas_2112500851.network.ApiClient;
import com.akbarrahmatm.projectuas_2112500851.network.ApiService;
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

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailLokasiActivity extends AppCompatActivity {

    private LocationRequest locationRequest;
    TextView tvDetailNamaToko, tvDetailAlamatToko, tvDetailMinuman, tvDetailEsKrim, tvDetailGas, tvDetailBensin, tvDetailPulsa;
    Button btnCekHarga, btnKembali;
    ImageButton btnStreetView;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_detail_lokasi);


        progressDialog = new ProgressDialog(this, R.style.PrimaryProgressDialog);
        progressDialog.setMessage("Mohon Tunggu, Sedang memuat Detail Toko");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        btnKembali = findViewById(R.id.btnKembali);
        btnStreetView = findViewById(R.id.btnStreetView);

        Bundle extras = getIntent().getExtras();
        String id_toko = extras.getString("id_toko");

        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnStreetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailLokasiActivity.this, StreetViewActivity.class);
                intent.putExtra("id_toko", id_toko);
                startActivity(intent);
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        MapView map = (MapView) findViewById(R.id.mapbaru);




        map.setVisibility(View.GONE);

        getDetailDataLocation();

        ImageButton btnDirection = (ImageButton) findViewById(R.id.btnDirection);
        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directionDialog();
            }
        });




    }

    private void directionDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.direction_dialog, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        Button btnMotor = (Button) dialogView.findViewById(R.id.btnMotor);
        Button btnMobil = (Button) dialogView.findViewById(R.id.btnMobil);
        Button btnJalan = (Button) dialogView.findViewById(R.id.btnJalan);



        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String id_toko = extras.getString("id_toko");
            ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
            Call<ListDetailTokoModel> call = apiService.getDetailLocation(id_toko);
            call.enqueue(new Callback<ListDetailTokoModel>() {
                @Override
                public void onResponse(Call<ListDetailTokoModel> call, Response<ListDetailTokoModel> response) {
                    btnMotor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(DetailLokasiActivity.this, DirectionMotorActivity.class);
                            intent.putExtra("bujur", response.body().getData().get(0).getBujur());
                            intent.putExtra("lintang", response.body().getData().get(0).getLintang());
                            intent.putExtra("nama_toko", response.body().getData().get(0).getNamaToko());
                            startActivity(intent);
                        }
                    });
                    btnMobil.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(DetailLokasiActivity.this, DirectionMobilActivity.class);
                            intent.putExtra("bujur", response.body().getData().get(0).getBujur());
                            intent.putExtra("lintang", response.body().getData().get(0).getLintang());
                            intent.putExtra("nama_toko", response.body().getData().get(0).getNamaToko());
                            startActivity(intent);
                        }
                    });
                    btnJalan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(DetailLokasiActivity.this, DirectionJalanActivity.class);
                            intent.putExtra("bujur", response.body().getData().get(0).getBujur());
                            intent.putExtra("lintang", response.body().getData().get(0).getLintang());
                            intent.putExtra("nama_toko", response.body().getData().get(0).getNamaToko());
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public void onFailure(Call<ListDetailTokoModel> call, Throwable t) {

                }
            });
        }


    }

    private void getDetailDataLocation() {


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String id_toko = extras.getString("id_toko");

            ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
            Call<ListDetailTokoModel> call = apiService.getDetailLocation(id_toko);
            call.enqueue(new Callback<ListDetailTokoModel>() {
                @Override
                public void onResponse(Call<ListDetailTokoModel> call, Response<ListDetailTokoModel> response) {


                    setRouteMaps(Double.valueOf(response.body().getData().get(0).getBujur()), Double.valueOf(response.body().getData().get(0).getLintang()), response.body().getData().get(0).getNamaToko());


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
                            Intent i = new Intent(DetailLokasiActivity.this, ProdukActivity.class);
                            i.putExtra("id_toko", response.body().getData().get(0).getIdToko());
                            i.putExtra("nama_toko", response.body().getData().get(0).getNamaToko());
                            startActivity(i);
                        }
                    });
                }

                @Override
                public void onFailure(Call<ListDetailTokoModel> call, Throwable t) {
                    progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailLokasiActivity.this);

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
    public void setRouteMaps(Double destLat, Double destLong, String namatoko) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(DetailLokasiActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(DetailLokasiActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {


                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(DetailLokasiActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() > 0) {

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();

                                        LatLng originLn = new LatLng(latitude, longitude);
                                        LatLng destLn = new LatLng(destLat, destLong);

                                        RoadManager roadManager = new OSRMRoadManager(DetailLokasiActivity.this, "MyOwnUserAgent/1.0");
                                        ((OSRMRoadManager)roadManager).setMean(OSRMRoadManager.MEAN_BY_CAR);

                                        MapView map = (MapView) findViewById(R.id.mapbaru);
                                        ProgressBar loadingDirection = (ProgressBar) findViewById(R.id.loadingDirection);

                                        loadingDirection.setVisibility(View.GONE);
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
                    Toast.makeText(DetailLokasiActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(DetailLokasiActivity.this, 2);
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