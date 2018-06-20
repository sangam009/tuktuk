package com.tuktuk.serviceinteface;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;

import com.google.gson.JsonObject;
import com.tuktuk.model.Location;
import com.tuktuk.model.SuggestionRequest;

public interface SupportService {

	JsonObject serializeReqToJson(HttpServletRequest req);

	Location getLocation(SuggestionRequest suggestion);

	Object getRequestedObject(JsonObject request);

	List<JsonObject> getGeoCodeApiResult(SuggestionRequest suggest) throws Exception;

	Double getHashCodeOfLocation(JsonObject geoCodeApiResponseRefined);

	List<JsonObject> getElasticsearchResponse(Double hashCodeList) throws IOException;

	List<JsonObject> enrichGeoCodeApiResponse(JsonObject geoCodeApiResponse) throws IOException, JSONException;

	void indexEnrichedgeoCodeResponse(List<JsonObject> geoCodeApiResponse);

	JsonObject refineGeoCodeApiResponse(List<JsonObject> geoCodeApiResponse);
}
