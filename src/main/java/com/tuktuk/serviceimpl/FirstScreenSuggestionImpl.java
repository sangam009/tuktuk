package com.tuktuk.serviceimpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.tuktuk.model.SuggestionRequest;
import com.tuktuk.serviceinteface.FirstScreenSuggestion;
import com.tuktuk.serviceinteface.SupportService;

@Service
public class FirstScreenSuggestionImpl implements FirstScreenSuggestion {

	@Autowired
	SupportService support;

	@Override
	public JsonObject getSuggestion(HttpServletRequest req, HttpServletResponse res) {
		// parse the req object and get the values of the required field in the model

		JsonObject serializedRequest = support.serializeReqToJson(req);

		SuggestionRequest suggestionRequest = (SuggestionRequest) support.getRequestedObject(serializedRequest);

		
		
		return serializedRequest;
	}

}
