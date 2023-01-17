package com.akbarrahmatm.projectuas_2112500851;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akbarrahmatm.projectuas_2112500851.model.ListProdukModel;
import com.akbarrahmatm.projectuas_2112500851.model.ProdukModel;
import com.akbarrahmatm.projectuas_2112500851.network.ApiClient;
import com.akbarrahmatm.projectuas_2112500851.network.ApiService;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProdukActivity extends AppCompatActivity {

    private List<ProdukModel> mListProduk = new ArrayList<>();
    GridView gridView;
    TextView kosong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        gridView = findViewById(R.id.gridView);
        kosong = findViewById(R.id.kosong);


        kosong.setVisibility(View.GONE);

        Button bntKembali = findViewById(R.id.btnKembali);

        bntKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getAllData();
    }

    private void getAllData(){
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.PrimaryProgressDialog);
        progressDialog.setMessage("Mohon Tunggu ...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String id_toko = extras.getString("id_toko");
            String nama_toko = extras.getString("nama_toko");

            TextView namaTokoProduk = findViewById(R.id.namaTokoProduk);


            namaTokoProduk.setText(nama_toko);

            ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
            Call<ListProdukModel> call = apiService.getProduk(id_toko);
            call.enqueue(new Callback<ListProdukModel>() {
                @Override
                public void onResponse(Call<ListProdukModel> call, Response<ListProdukModel> response) {

                    progressDialog.dismiss();

                    mListProduk = response.body().getData();

                    if(mListProduk.size() == 0){
                        gridView.setVisibility(View.GONE);
                        kosong.setVisibility(View.VISIBLE);
                        kosong.setText("Produk belum tersedia.");
                    }else{
                        CustomAdapter customAdapter = new CustomAdapter(mListProduk, ProdukActivity.this);

                        gridView.setAdapter(customAdapter);
                    }
                }

                @Override
                public void onFailure(Call<ListProdukModel> call, Throwable t) {
                    progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProdukActivity.this);

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
        }else{
            finish();
        }

    }

    public class CustomAdapter extends BaseAdapter{

        private List<ProdukModel> produkModel;
        private Context context;
        private LayoutInflater layoutInflater;

        public CustomAdapter(List<ProdukModel> produkModel, Context context) {
            this.produkModel = produkModel;
            this.context = context;
            this.layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return produkModel.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null){
                view = layoutInflater.inflate(R.layout.row_grid_items, viewGroup, false);
            }

            ImageView imageView = view.findViewById(R.id.imageView);
            TextView namaProduk = view.findViewById(R.id.namaProduk);
            TextView hargaProduk = view.findViewById(R.id.hargaProduk);

            namaProduk.setText(produkModel.get(i).getNamaProduk());
            hargaProduk.setText(formatRupiah(Double.valueOf(produkModel.get(i).getHargaProduk())));

            GlideApp.with(context).load(produkModel.get(i).getFotoProduk()).into(imageView);


            return view;
        }
    }

    private String formatRupiah(Double number){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }

}

