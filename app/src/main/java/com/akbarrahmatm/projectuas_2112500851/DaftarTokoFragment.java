package com.akbarrahmatm.projectuas_2112500851;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.akbarrahmatm.projectuas_2112500851.model.ListProdukModel;
import com.akbarrahmatm.projectuas_2112500851.model.ListTokoModel;
import com.akbarrahmatm.projectuas_2112500851.model.ProdukModel;
import com.akbarrahmatm.projectuas_2112500851.model.TokoModel;
import com.akbarrahmatm.projectuas_2112500851.network.ApiClient;
import com.akbarrahmatm.projectuas_2112500851.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DaftarTokoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DaftarTokoFragment extends Fragment {

    GridView gridView;
    private List<TokoModel> mListToko = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DaftarTokoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DaftarTokoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DaftarTokoFragment newInstance(String param1, String param2) {
        DaftarTokoFragment fragment = new DaftarTokoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_daftar_toko, container, false);
        gridView = (GridView) rootView.findViewById(R.id.daftarGrid);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.PrimaryProgressDialog);
        progressDialog.setMessage("Mohon Tunggu ...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
        Call<ListTokoModel> call = apiService.getAllLocation();
        call.enqueue(new Callback<ListTokoModel>() {
            @Override
            public void onResponse(Call<ListTokoModel> call, Response<ListTokoModel> response) {
                progressDialog.dismiss();

                mListToko = response.body().getData();
                CustomAdapter customAdapter = new CustomAdapter(mListToko, getActivity());

                gridView.setAdapter(customAdapter);

            }

            @Override
            public void onFailure(Call<ListTokoModel> call, Throwable t) {
                progressDialog.dismiss();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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

        return rootView;
    }

    public class CustomAdapter extends BaseAdapter {

        private List<TokoModel> tokoModel;
        private Context context;
        private LayoutInflater layoutInflater;

        public CustomAdapter(List<TokoModel> tokoModel, Context context) {
            this.tokoModel = tokoModel;
            this.context = context;
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return tokoModel.size();
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
                view = layoutInflater.inflate(R.layout.daftar_toko_list, viewGroup, false);
            }

            TextView namaToko = view.findViewById(R.id.namaToko);
            TextView alamatToko = view.findViewById(R.id.alamatToko);
            Button btnDetail = view.findViewById(R.id.btnDetail);

            namaToko.setText(tokoModel.get(i).getNamaToko());
            alamatToko.setText(tokoModel.get(i).getAlamatToko());

            btnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), DetailLokasiActivity.class);
                    intent.putExtra("id_toko", tokoModel.get(i).getIdToko());
                    startActivity(intent);
                }
            });


            return view;
        }
    }
}