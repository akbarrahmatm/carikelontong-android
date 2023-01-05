package com.akbarrahmatm.projectuas_2112500851.model;

import com.google.gson.annotations.SerializedName;

public class ProdukModel {

	@SerializedName("id_produk")
	private String idProduk;

	@SerializedName("nama_produk")
	private String namaProduk;

	@SerializedName("harga_produk")
	private String hargaProduk;

	@SerializedName("id_toko")
	private String idToko;

	@SerializedName("foto_produk")
	private String fotoProduk;

	public String getIdProduk(){
		return idProduk;
	}

	public String getNamaProduk(){
		return namaProduk;
	}

	public String getHargaProduk(){
		return hargaProduk;
	}

	public String getIdToko(){
		return idToko;
	}

	public String getFotoProduk(){
		return fotoProduk;
	}
}