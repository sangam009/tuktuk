package com.tuktuk.serviceimpl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
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
	public List<JsonObject> getGeoCodeFromElasticsearch() {

		/*
		 * Client client = getElasticsearchClient("elasticsearch", "127.0.0.1", 9300);
		 * System.out.println("client is " + client); QueryBuilder builder =
		 * QueryBuilders.matchQuery("user", "kimchy"); SearchResponse res =
		 * client.prepareSearch("promo_apply_logs").setTypes("promo_apply_logs")
		 * .setSearchType(SearchType.QUERY_AND_FETCH).setQuery(builder).setFrom(0).
		 * setSize(60).setExplain(true) .execute().actionGet(); SearchHit[] results =
		 * res.getHits().getHits(); for (SearchHit searchHit : results) { Map<String,
		 * Object> result = searchHit.getSource(); System.out.println(result); }
		 */

		try {
			TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
			SearchResponse searchResponse = client.prepareSearch("promo_apply_logs").setTypes("promo_apply_logs")
					.execute().actionGet();
			SearchHit[] hits = searchResponse.getHits().getHits();
			Map<String, Object> attributes = new HashMap<>();
			attributes.put("songs", hits);
			System.out.println("attributes are " + attributes);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/*
	 * @SuppressWarnings("resource") public static Client
	 * getElasticsearchClient(String clustername, String ipAddress, int port) {
	 * 
	 * Settings settings = Settings.builder().put("cluster.name",
	 * clustername).build(); Client client = null; try { client = new
	 * TransportClient } catch (Exception e) { System.out.println("error is " +
	 * e.toString()); e.printStackTrace(); }
	 * 
	 * return client; }
	 */

	@Override
	public void populateDataInES(List<JsonObject> objects) {

	}
}
