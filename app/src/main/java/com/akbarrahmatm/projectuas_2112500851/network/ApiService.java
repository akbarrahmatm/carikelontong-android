package com.akbarrahmatm.projectuas_2112500851.network;

import com.akbarrahmatm.projectuas_2112500851.model.ListDetailTokoModel;
import com.akbarrahmatm.projectuas_2112500851.model.ListProdukModel;
import com.akbarrahmatm.projectuas_2112500851.model.ListTokoModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("toko/list")
    Call<ListTokoModel> getAllLocation();

    @GET("toko/detail/{id}")
    Call<ListDetailTokoModel> getDetailLocation(@Path("id") String id);

    @GET("produk/list/{id}")
    Call<ListProdukModel> getProduk(@Path("id") String id);
}
