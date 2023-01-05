package com.akbarrahmatm.projectuas_2112500851;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.akbarrahmatm.projectuas_2112500851.model.ListTokoModel;
import com.akbarrahmatm.projectuas_2112500851.model.TokoModel;
import com.akbarrahmatm.projectuas_2112500851.network.ApiClient;
import com.akbarrahmatm.projectuas_2112500851.network.ApiService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.akbarrahmatm.projectuas_2112500851.databinding.ActivityMapsBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private List<TokoModel> mListMarker = new ArrayList<>();

    String URLString;

    Button btnDetailToko;
    TextView tvNamaToko, tvAlamatToko;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        btnDetailToko = findViewById(R.id.btnDetailToko);


        btnDetailToko.setVisibility(View.GONE);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getAllDataLocation();
    }

    private void getAllDataLocation() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mohon Tunggu ...");
        progressDialog.show();

        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
        Call<ListTokoModel> call = apiService.getAllLocation();

        call.enqueue(new Callback<ListTokoModel>() {
            @Override
            public void onResponse(Call<ListTokoModel> call, Response<ListTokoModel> response) {
                progressDialog.dismiss();
                mListMarker = response.body().getData();
                initMarker(mListMarker);
            }

            @Override
            public void onFailure(Call<ListTokoModel> call, Throwable t) {
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);

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
    }

    private void initMarker(List<TokoModel> mListMarker) {
        for(int i = 0; i < mListMarker.size(); i++){
            LatLng location = new LatLng(Double.parseDouble(mListMarker.get(i).getBujur()), Double.parseDouble(mListMarker.get(i).getLintang()));

            Marker markerku = mMap.addMarker(new MarkerOptions().position(location).title(mListMarker.get(i).getNamaToko()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setPadding(0,0,0, 750);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            markerku.setTag(i);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    int indeks = (int) marker.getTag();
                    tvNamaToko = findViewById(R.id.tvNamaToko);
                    tvAlamatToko = findViewById(R.id.tvAlamatToko);

                    tvNamaToko.setText(mListMarker.get(indeks).getNamaToko());
                    tvAlamatToko.setText(mListMarker.get(indeks).getAlamatToko());
                    btnDetailToko.setVisibility(View.VISIBLE);

                    btnDetailToko.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(MapsActivity.this, DetailLokasiActivity.class);
                            i.putExtra("id_toko", mListMarker.get(indeks).getIdToko());
                            startActivity(i);
                        }
                    });


                    return false;
                }
            });

        }
    }
}