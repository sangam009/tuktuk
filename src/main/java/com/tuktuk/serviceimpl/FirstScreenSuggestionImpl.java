package com.tuktuk.serviceimpl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tuktuk.model.SuggestionRequest;
import com.tuktuk.serviceinteface.FirstScreenSuggestion;
import com.tuktuk.serviceinteface.SupportService;

@Service
public class FirstScreenSuggestionImpl implements FirstScreenSuggestion {

	@Autowired
	SupportService support;

	@Override
	public Gson getSuggestion(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// parse the req object and get the values of the required field in the model

		// serialize the request object to get the jsonobject
		JsonObject serializedRequest = support.serializeReqToJson(req);

		// convert the above serialized object to suggestion request object
		SuggestionRequest suggestionRequest = (SuggestionRequest) support.getRequestedObject(serializedRequest);

		// search for response for gecodeApi from elasticsearch
		List<JsonObject> elasticsearchResponse = support.getGeoCodeFromElasticsearch();

		/*
		 * if (elasticsearchResponse == null) { // get reverse geocoding data for the
		 * suggest request object List<JsonObject> geoCodeApiResponse =
		 * support.getGeoCodeApiResult(suggestionRequest); }
		 */

		return null;
	}

}
