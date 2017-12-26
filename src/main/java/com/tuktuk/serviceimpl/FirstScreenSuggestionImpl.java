package com.tuktuk.serviceimpl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.tuktuk.model.SuggestionRequest;
import com.tuktuk.serviceinteface.FirstScreenSuggestion;
import com.tuktuk.serviceinteface.SupportService;

@Service
public class FirstScreenSuggestionImpl implements FirstScreenSuggestion {
	SupportService support;

	@Autowired
	public FirstScreenSuggestionImpl(SupportService support) {
		this.support = support;
	}

	@Override
	/*public Gson getSuggestion(HttpServletRequest req, HttpServletResponse res) throws Exception {*/
	public Gson getSuggestion(SuggestionRequest req) throws Exception {
		/*// parse the req object and get the values of the required field in the model

		// serialize the request object to get the jsonobject
		JsonObject serializedRequest = support.serializeReqToJson(req);

		// convert the above serialized object to suggestion request object
		SuggestionRequest suggestionRequest = new Gson().fromJson(serializedRequest, SuggestionRequest.class);*/

		// search for response for gecodeApi from elasticsearch
		List<JsonObject> elasticsearchResponse = support.getGeoCodeFromElasticsearch();

		 if (null == elasticsearchResponse) {
			 // get reverse geocoding data for the suggest request object
			 List<JsonObject> geoCodeApiResponse = support.getGeoCodeApiResult(req);
		 }

		return null;
	}

}
