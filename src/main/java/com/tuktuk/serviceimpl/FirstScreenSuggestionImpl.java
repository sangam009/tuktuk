package com.tuktuk.serviceimpl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tuktuk.model.SuggestionRequest;
import com.tuktuk.serviceinteface.FirstScreenSuggestion;
import com.tuktuk.serviceinteface.SupportService;

@Service
public class FirstScreenSuggestionImpl implements FirstScreenSuggestion {

	final static Logger log = Logger.getLogger(SuggestionRequest.class);
	
	@Autowired
	SupportService support;

	@Override
	public List<JsonObject> getSuggestion(HttpServletRequest req, HttpServletResponse res) throws Exception {

		JsonObject serializedRequest = support.serializeReqToJson(req);

		SuggestionRequest suggestionRequest = new Gson().fromJson(serializedRequest, SuggestionRequest.class);

		List<JsonObject> geoCodeApiResponse = support.getGeoCodeApiResult(suggestionRequest);

		System.out.println("geocode api response is " + geoCodeApiResponse.toString());

		JsonObject geoCodeApiResponseRefined = support.refineGeoCodeApiResponse(geoCodeApiResponse);

		System.out.println("geocode api refined response is " + geoCodeApiResponseRefined.toString());

		Double hashCode = support.getHashCodeOfLocation(geoCodeApiResponseRefined);

		System.out.println("hash code value is " + hashCode.toString());

		List<JsonObject> elasticsearchResponse = support.getElasticsearchResponse(hashCode);
		log.info("elasticssarch response size is " + elasticsearchResponse.size());
		if (elasticsearchResponse.size() >= 1 && elasticsearchResponse.get(0).getAsJsonObject("hits").get("total").getAsInt() > 0 ) {
			return elasticsearchResponse;
		}else {
			List<JsonObject> nearByElasticSearch = support.nearBySuggestionSearch(suggestionRequest);
			if(nearByElasticSearch.size() >= 1 && nearByElasticSearch.get(0).getAsJsonObject("hits").get("total").getAsInt() > 0 ) {
				return nearByElasticSearch;
			}else {
				geoCodeApiResponse = support.enrichGeoCodeApiResponse(geoCodeApiResponse, suggestionRequest);
				support.indexEnrichedgeoCodeResponse(geoCodeApiResponse);	
			}	
		}
		return geoCodeApiResponse;
	}

}
