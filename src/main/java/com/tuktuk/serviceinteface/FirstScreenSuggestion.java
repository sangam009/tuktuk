package com.tuktuk.serviceinteface;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public interface FirstScreenSuggestion {

	public Gson getSuggestion(HttpServletRequest req, HttpServletResponse res) throws Exception;

}
