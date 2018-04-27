package com.tuktuk.serviceimpl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectSerializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tuktuk.model.ConfigurationProperties;

import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;

@Service("asyncservice")
@PropertySource("classpath:application.properties")
public class AsyncServices {

	@Autowired
	ConfigurationProperties config;

	@Async("executor")
	public void addNearBySearchToTheEnrichment(List<JsonObject> geoCodeApiResponse) throws IOException, JSONException {
		GooglePlaces client = new GooglePlaces(config.getGoogleKey());
		// to get the list of the place ids of the nearby places to given lat long
		for (int i = 0; i < geoCodeApiResponse.size(); i++) {
			JsonObject location = geoCodeApiResponse.get(i).get("geometry").getAsJsonObject().get("location")
					.getAsJsonObject();
			Double lat = Double.parseDouble(location.get("lat").toString());
			Double lon = Double.parseDouble(location.get("lng").toString());
			List<Place> places = client.getNearbyPlaces(lat, lon, config.getIndexRadius(),
					GooglePlaces.MAXIMUM_RESULTS);
			for (Place place : places) {
				Place response = client.getPlaceById(place.getPlaceId());
				JSONObject responseInJson = response.getJson();
				String geometry = responseInJson.get("geometry").toString().replace("lng", "lon");
				JSONObject geometryJson = new JSONObject(geometry);
				responseInJson.put("geometry", geometryJson);
				System.out.println("final json to index is " + responseInJson.toString());
				indexInElasticsearch(responseInJson);
			}
		}

	}

	private void indexInElasticsearch(JSONObject json) {
		try {
			HttpEntity entity = new NStringEntity(json.toString(), ContentType.APPLICATION_JSON);
			RestClient restClient = RestClient
					.builder(new HttpHost(config.getElasticsearchUrl(), config.getElasticsearchPort(), "http")).build();
			JsonParser jsonp = new JsonParser();
			JsonObject location = jsonp.parse(json.get("geometry").toString()).getAsJsonObject().get("location")
					.getAsJsonObject();
			System.out.println("location value is " + location);
			Double addedValueOfLatLong = Double.parseDouble(location.get("lat").toString())
					+ Double.parseDouble(location.get("lon").toString());
			System.out.println("added value for lat long is " + addedValueOfLatLong);
			String endpointValue = config.getEndpoint() + addedValueOfLatLong.toString();
			Response response = restClient.performRequest(config.getIndexHanler(), endpointValue,
					Collections.<String, String>emptyMap(), entity);
			restClient.close();
		} catch (Exception e) {
			System.out.println("Exception in indexing is " + e);
		}
	}

}
