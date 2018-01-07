package com.tuktuk.serviceimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.tuktuk.model.Location;
import com.tuktuk.model.SuggestionRequest;
import com.tuktuk.serviceinteface.SupportService;

@Service
public class SupportServiceImpl implements SupportService {

	final static Logger log = Logger.getLogger(SuggestionRequest.class);

	@Value("${elasticsearch.url}")
	String elasticsearchUrl;

	@Value("${elasticsearch.port}")
	String elasticsearchPort;

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
		System.out.println("tag value is " + tag);
		switch (tag) {

		case "\"getSuggestion\"":
			SuggestionRequest suggest = new SuggestionRequest();
			suggest.setLatitude(request.get("latitude").getAsDouble());
			suggest.setLongitude(request.get("longitude").getAsDouble());
			return suggest;

		default:
			System.out.println("api tag is not present");
		}

		return null;
	}

	@Override
	public List<JsonObject> getGeoCodeApiResult(SuggestionRequest suggest) throws Exception {

		JsonParser jsonparser = new JsonParser();
		GeoApiContext context = new GeoApiContext();
		context.setApiKey("AIzaSyCUg-jlo_6QekPQgmUT_vx6z0nHw-eJOis");
		LatLng location = new LatLng(suggest.getLatitude(), suggest.getLongitude());
		GeocodingResult[] results = GeocodingApi.reverseGeocode(context, location).await();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		List<JsonObject> finalResponse = new ArrayList<JsonObject>();
		for (GeocodingResult geocodingResult : results) {
			finalResponse.add((JsonObject) jsonparser.parse(gson.toJson(geocodingResult)));
		}
		return finalResponse;
	}

	@Override
	public Double getHashCodeOfLocation(JsonObject geoCodeApiResponseRefined) {

		JsonObject location = geoCodeApiResponseRefined.get("geometry").getAsJsonObject().get("location")
				.getAsJsonObject();
		Double addedValueOfLatLong = Double.parseDouble(location.get("lat").toString())
				+ Double.parseDouble(location.get("lng").toString());
		log.info("location value is" + location.toString());
		log.info("addedlong value is " + addedValueOfLatLong.toString());
		// Long.valueOf(Double.doubleToLongBits(addedValueOfLatLong)).hashCode();

		return addedValueOfLatLong;
	}

	@Override
	public List<JsonObject> getElasticsearchResponse(Double hashCodeList) {

		return null;
	}

	@Override
	public void enrichGeoCodeApiResponse(List<JsonObject> geoCodeApiResponse) {
		// TODO Auto-generated method stub

	}

	@Override
	public void indexEnrichedgeoCodeResponse(List<JsonObject> geoCodeApiResponse) {
		// TODO Auto-generated method stub

	}

	@Override
	public JsonObject refineGeoCodeApiResponse(List<JsonObject> geoCodeApiResponse) {

		List<JsonObject> refinedResult = new ArrayList<JsonObject>();

		if (geoCodeApiResponse.size() != 0) {
			JsonObject mostSignificantLocation = geoCodeApiResponse.get(0);
			return mostSignificantLocation;
		}
		log.info("geocode api response is empty");

		return null;
	}

	public List<JsonObject> queryElasticsearch(HttpEntity queryEntity, String endpoint, String handler)
			throws IOException {
		RestClient restClient = RestClient
				.builder(new HttpHost(elasticsearchUrl, Integer.parseInt(elasticsearchPort), "http")).build();
		Response response = restClient.performRequest(handler, endpoint, Collections.singletonMap("pretty", "true"));
		System.out.println(EntityUtils.toString(response.getEntity()));
		return null;
	}
}
