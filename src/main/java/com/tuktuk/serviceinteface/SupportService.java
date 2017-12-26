package com.tuktuk.serviceinteface;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonObject;
import com.tuktuk.model.Location;
import com.tuktuk.model.SuggestionRequest;

public interface SupportService {

	JsonObject serializeReqToJson(HttpServletRequest req);

	Location getLocation(SuggestionRequest suggestion);

	Object getRequestedObject(JsonObject request);

	List<JsonObject> getGeoCodeApiResult(SuggestionRequest suggest) throws Exception;

	List<JsonObject> getGeoCodeFromElasticsearch();

	void populateDataInES(List<JsonObject> objects);
}
