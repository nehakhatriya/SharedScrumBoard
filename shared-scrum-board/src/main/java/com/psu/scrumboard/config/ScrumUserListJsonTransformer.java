package com.psu.scrumboard.config;

import java.util.List;

import javax.persistence.AttributeConverter;

import com.psu.scrumboard.model.ScrumBoardCurrentUser;
import com.google.gson.Gson;
import com.googlecode.gentyref.TypeToken;

public class ScrumUserListJsonTransformer implements AttributeConverter<List<ScrumBoardCurrentUser>, String> {

	@Override
	public List<ScrumBoardCurrentUser> convertToEntityAttribute(String json) {
		return new Gson().fromJson(json, new TypeToken<List<ScrumBoardCurrentUser>>() {}.getType());
	}

	@Override
	public String convertToDatabaseColumn(List<ScrumBoardCurrentUser> attribute) {
		return new Gson().toJson(attribute); 
	}

}
