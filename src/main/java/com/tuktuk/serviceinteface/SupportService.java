package com.tuktuk.serviceinteface;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonObject;
import com.tuktuk.model.Location;
import com.tuktuk.model.SuggestionRequest;

public interface SupportService {

	public JsonObject serializeReqToJson(HttpServletRequest req);

	public Location getLocation(SuggestionRequest suggestion);

	public Object getRequestedObject(JsonObject request);

	public List<JsonObject> getGeoCodeApiResult(SuggestionRequest suggest) throws Exception;

	public List<JsonObject> getGeoCodeFromElasticsearch();

}
