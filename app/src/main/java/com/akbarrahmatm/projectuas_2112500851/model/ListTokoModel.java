package com.akbarrahmatm.projectuas_2112500851.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ListTokoModel {

	@SerializedName("data")
	private List<TokoModel> data;

	public List<TokoModel> getData(){
		return data;
	}
}