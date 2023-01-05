package com.akbarrahmatm.projectuas_2112500851.model;

import com.google.gson.annotations.SerializedName;

public class TokoModel {

	@SerializedName("alamat_toko")
	private String alamatToko;

	@SerializedName("lintang")
	private String lintang;

	@SerializedName("id_toko")
	private String idToko;

	@SerializedName("nama_toko")
	private String namaToko;

	@SerializedName("bujur")
	private String bujur;

	public String getAlamatToko(){
		return alamatToko;
	}

	public String getLintang(){
		return lintang;
	}

	public String getIdToko(){
		return idToko;
	}

	public String getNamaToko(){
		return namaToko;
	}

	public String getBujur(){
		return bujur;
	}
}