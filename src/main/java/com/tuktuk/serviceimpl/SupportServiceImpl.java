package com.tuktuk.serviceimpl;

import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.tuktuk.model.Location;
import com.tuktuk.model.SuggestionRequest;
import com.tuktuk.serviceinteface.SupportService;

@Service
public class SupportServiceImpl implements SupportService {

	@Override
	public JsonObject serializeReqToJson(HttpServletRequest req) {

		String uri = req.getRequestURI();
		JsonObject response = new JsonObject();

		switch (uri) {

		case "/test":

			response.addProperty("name", "sangam");
			break;

		case "/getSuggestion":

			response.addProperty("latitude", req.getParameter("latitude"));
			response.addProperty("longitude", req.getParameter("longitude"));
			response.addProperty("tag", "getSuggestion");

			break;

		default:
			System.out.println("hit wrong uri");

		}
		return response;
	}

	@Override
	public Location getLocation(SuggestionRequest suggestion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getRequestedObject(JsonObject request) {

		String tag = request.get("tag").toString();
		switch (tag) {

		case "getSuggestion":
			SuggestionRequest suggest = new SuggestionRequest();
			suggest.setLatitude(request.get("latitude").getAsDouble());
			suggest.setLongitude(request.get("longitude").getAsDouble());
			return suggest;

		default:
			System.out.println("api tag is not present");
		}

		return null;
	}

}
