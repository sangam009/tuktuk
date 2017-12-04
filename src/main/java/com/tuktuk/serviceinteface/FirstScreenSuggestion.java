package com.tuktuk.serviceinteface;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

public interface FirstScreenSuggestion {

	public JsonObject getSuggestion(HttpServletRequest req, HttpServletResponse res);

}
