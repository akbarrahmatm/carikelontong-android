package com.akbarrahmatm.projectuas_2112500851.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ListDetailTokoModel {

	@SerializedName("data")
	private List<DetailTokoModel> data;

	public List<DetailTokoModel> getData(){
		return data;
	}
}