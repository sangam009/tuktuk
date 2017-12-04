package com.tuktuk.serviceinteface;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonObject;
import com.tuktuk.model.Location;
import com.tuktuk.model.SuggestionRequest;

public interface SupportService {

	public JsonObject serializeReqToJson(HttpServletRequest req);

	public Location getLocation(SuggestionRequest suggestion);

	public Object getRequestedObject(JsonObject request);
}
