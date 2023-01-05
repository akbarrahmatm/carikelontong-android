package com.akbarrahmatm.projectuas_2112500851.model;

import com.google.gson.annotations.SerializedName;

public class DetailTokoModel {

	@SerializedName("fitur_es_krim")
	private String fiturEsKrim;

	@SerializedName("fitur_gas_galon")
	private String fiturGasGalon;

	@SerializedName("alamat_toko")
	private String alamatToko;

	@SerializedName("lintang")
	private String lintang;

	@SerializedName("id_toko")
	private String idToko;

	@SerializedName("fitur_bensin")
	private String fiturBensin;

	@SerializedName("fitur_pulsa")
	private String fiturPulsa;

	@SerializedName("fitur_minuman_dingin")
	private String fiturMinumanDingin;

	@SerializedName("nama_toko")
	private String namaToko;

	@SerializedName("bujur")
	private String bujur;

	public String getFiturEsKrim(){
		return fiturEsKrim;
	}

	public String getFiturGasGalon(){
		return fiturGasGalon;
	}

	public String getAlamatToko(){
		return alamatToko;
	}

	public String getLintang(){
		return lintang;
	}

	public String getIdToko(){
		return idToko;
	}

	public String getFiturBensin(){
		return fiturBensin;
	}

	public String getFiturPulsa(){
		return fiturPulsa;
	}

	public String getFiturMinumanDingin(){
		return fiturMinumanDingin;
	}

	public String getNamaToko(){
		return namaToko;
	}

	public String getBujur(){
		return bujur;
	}
}