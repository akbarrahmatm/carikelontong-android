package com.akbarrahmatm.projectuas_2112500851.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ListProdukModel {

	@SerializedName("data")
	private List<ProdukModel> data;

	public List<ProdukModel> getData(){
		return data;
	}
}