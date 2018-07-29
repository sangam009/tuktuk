package com.tuktuk.serviceimpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.log4j.Logger;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.tuktuk.model.ConfigurationProperties;
import com.tuktuk.model.Location;
import com.tuktuk.model.SuggestionRequest;
import com.tuktuk.serviceinteface.SupportService;

@Service
@PropertySource("classpath:application.properties")
public class SupportServiceImpl implements SupportService {

	final static Logger log = Logger.getLogger(SuggestionRequest.class);

	@Autowired
	ConfigurationProperties config;

	@Autowired
	AsyncServices asyncservice;

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
			response.addProperty("radiusInMeters", req.getParameter("radiusInMeters"));
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
			suggest.radiusInMeters = request.get("radiusInMeters").getAsInt();
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
		System.out.println("key valus is :- " + config.getGoogleKey());
		context.setApiKey(config.getGoogleKey());
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
	public List<JsonObject> getElasticsearchResponse(Double hashCodeList) throws IOException {

		JsonObject options = new JsonObject();
		options.addProperty("type", "match");
		options.addProperty("key", "_id");
		options.addProperty("value", hashCodeList.toString());
		System.out.println("options value is" + options.toString());
		String query = buildQuery(options.get("type").toString(), options);
		HttpEntity entity = new NStringEntity(query, ContentType.APPLICATION_JSON);
		List<JsonObject> elasticsearchresponse = queryElasticsearch(entity, config.getElasticsearchHandler(), "GET");

		return elasticsearchresponse;
	}

	private String buildQuery(String type, JsonObject options) {
		JsonObject query_final = new JsonObject();
		System.out.println("type value is " + type);
		switch (type) {
		case "\"match\"":
			System.out.println("entered match case");
			JsonObject query_inner = new JsonObject();
			JsonObject query_inner1 = new JsonObject();
			query_inner.addProperty(options.get("key").toString(), options.get("value").toString());
			query_inner1.add("match", query_inner);
			query_final.add("query", query_inner1);
			log.info("final query to be send is " + query_final.toString());
			break;

		default:
			System.out.println("entered wrong query builder type");
			break;
		}
		return query_final.toString();
	}

	@Override
	public List<JsonObject> enrichGeoCodeApiResponse(List<JsonObject> geoCodeApiResponse, SuggestionRequest request)
			throws IOException, JSONException {
		List<JsonObject> defaultResult  = new ArrayList<JsonObject>();
		try {
			JsonObject finalRequest = getParamsBasedSuggestionRequst(request);
			defaultResult = getDefaultSearchResult(finalRequest);
			asyncservice.addNearBySearchToTheEnrichment(geoCodeApiResponse);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return defaultResult;
		
	}

	private JsonObject getParamsBasedSuggestionRequst(SuggestionRequest request) {
		JsonObject options = new JsonObject();
		
		JsonObject query = new JsonObject();
		JsonObject bool = new JsonObject();
		JsonArray must = new JsonArray();
		
		JsonObject geoLoc = new JsonObject();
		
		JsonObject geoLocInner = new JsonObject();
		geoLocInner.addProperty("distance", String.valueOf(request.radiusInMeters) + "m");
		
		JsonObject loc = new JsonObject();
		loc.addProperty("lat", request.getLatitude());
		loc.addProperty("lon", request.getLongitude());
		
		geoLocInner.add("geometry.location", loc);
		
		geoLoc.add("geo_distance", geoLocInner);
		
		must.add(geoLoc);
		
		JsonObject match = new JsonObject();
		JsonObject type = new JsonObject();
		type.addProperty("types", "BANK, restaurant");
		match.add("match", type);
		must.add(match);
		
		bool.add("must", must);
		query.add("bool", bool);
		options.add("query",query);
		
		JsonArray sorting = new JsonArray();
		JsonObject sort = new JsonObject();
		JsonObject item = new JsonObject();
		item.addProperty("order", "desc");
		sort.add("rating", item);
		sorting.add(sort);
		options.add("sort", sorting );
		
//		options.addProperty("type", "matchall");
//		String query = buildQuery(options.get("type").toString(), options);
		System.out.println("final query to search is " + options.toString());
		return options;
	}

	private List<JsonObject> getDefaultSearchResult(JsonObject request) throws IOException {
		
		HttpEntity entity = new NStringEntity(request.toString(), ContentType.APPLICATION_JSON);
		List<JsonObject> elasticsearchresponse = queryElasticsearch(entity, config.getElasticsearchBestResultHandler(), "GET");
		System.out.println("final query to search is " + elasticsearchresponse.get(0).toString());
		return elasticsearchresponse;
	}

	@Override
	public void indexEnrichedgeoCodeResponse(List<JsonObject> geoCodeApiResponse) {
		// TODO Auto-generated method stub

	}

	@Override
	public JsonObject refineGeoCodeApiResponse(List<JsonObject> geoCodeApiResponse) {

		if (geoCodeApiResponse.size() != 0) {
			JsonObject mostSignificantLocation = geoCodeApiResponse.get(0);
			return mostSignificantLocation;
		}
		log.info("geocode api response is empty");
		return null;
	}

	private List<JsonObject> queryElasticsearch(HttpEntity queryEntity, String endpoint, String handler)
			throws IOException {
		RestClient restClient = RestClient
				.builder(new HttpHost(config.getElasticsearchUrl(), config.getElasticsearchPort(), "http")).build();
		Response response = restClient.performRequest(handler, endpoint, Collections.singletonMap("pretty", "true"),
				queryEntity);
		InputStream responseOutput = response.getEntity().getContent();
		BufferedReader finalResponseBuffer = new BufferedReader(new InputStreamReader(responseOutput));
		JsonParser parser = new JsonParser();
		JsonObject array = parser.parse(finalResponseBuffer).getAsJsonObject();
		List<JsonObject> result = new ArrayList<JsonObject>();
		result.add(array);
		return result;
	}

	@Override
	public List<JsonObject> nearBySuggestionSearch(SuggestionRequest suggestionRequest) throws IOException  {
		// TODO Auto-generated method stub
		JsonObject finalRequest = getParamsBasedSuggestionRequst(suggestionRequest);
		
		HttpEntity entity = new NStringEntity(finalRequest.toString(), ContentType.APPLICATION_JSON);
		List<JsonObject> elasticsearchresponse = queryElasticsearch(entity, config.getElasticsearchBestResultHandler(), "GET");
		System.out.println("final query to search is " + elasticsearchresponse.get(0).toString());
		return elasticsearchresponse;
	}
}
