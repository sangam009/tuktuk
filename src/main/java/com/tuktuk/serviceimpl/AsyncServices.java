package com.tuktuk.serviceimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

	private static Producer<Long, String> createProducer() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		return new KafkaProducer<>(props);
	}

	static void runProducer(final int sendMessageCount, List<JsonObject> geoCodeApiResponse) throws Exception {
		final Producer<Long, String> producer = createProducer();
		long time = System.currentTimeMillis();
		int count = 0;
		System.out.println("the value of sendmessage count is " + count);
		try {
			for (long index = time; index < time + sendMessageCount; index++) {
				final ProducerRecord<Long, String> record = new ProducerRecord<>("suggestionReconcile", index,
						geoCodeApiResponse.get(count).toString());

				RecordMetadata metadata = producer.send(record).get();

				long elapsedTime = System.currentTimeMillis() - time;
				System.out.printf("sent record(key=%s value=%s) " + "meta(partition=%d, offset=%d) time=%d\n",
						record.key(), record.value(), metadata.partition(), metadata.offset(), elapsedTime);

			}
		} finally {
			producer.flush();
			producer.close();
		}
	}

	/* method to publish data in kafka topic for async syncing of data */
	@Async("executor")
	public void addNearbySearchPayloadToKafka(JsonObject geoCodeApiResponse) throws Exception {
		System.out.println("enetered the async method");
		List<JsonObject> payload = new ArrayList<JsonObject>();
		payload.add(0, geoCodeApiResponse);
		runProducer(payload.size(), payload);
	}

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
