package com.tuktuk.serviceinteface;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

public interface FirstScreenSuggestion {

	public List<JsonObject> getSuggestion(HttpServletRequest req, HttpServletResponse res) throws Exception;

}
