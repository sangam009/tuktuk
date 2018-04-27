package com.tuktuk.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:basic.properties")
public class ConfigurationProperties {
	@Value("${elasticsearch.bestresulthandler}")
	String elasticsearchBestResultHandler;

	@Value("${server.port}")
	int serverPort;

	@Value("${elasticsearch.url}")
	String elasticsearchUrl;

	@Value("${elasticsearch.port}")
	int elasticsearchPort;

	@Value("${google.key}")
	String googleKey;

	@Value("${elasticsearch.handler}")
	String elasticsearchHandler;

	@Value("${google.indexRadius}")
	Double indexRadius;

	@Value("${elasticsearch.indexHandler}")
	String indexHanler;

	@Value("${elasticsearch.endpoint}")
	String endpoint;

	public String getElasticsearchBestResultHandler() {
		return elasticsearchBestResultHandler;
	}

	public void setElasticsearchBestResultHandler(String elasticsearchBestResultHandler) {
		this.elasticsearchBestResultHandler = elasticsearchBestResultHandler;
	}

	public String getIndexHanler() {
		return indexHanler;
	}

	public void setIndexHanler(String indexHanler) {
		this.indexHanler = indexHanler;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getElasticsearchUrl() {
		return elasticsearchUrl;
	}

	public void setElasticsearchUrl(String elasticsearchUrl) {
		this.elasticsearchUrl = elasticsearchUrl;
	}

	public int getElasticsearchPort() {
		return elasticsearchPort;
	}

	public void setElasticsearchPort(int elasticsearchPort) {
		this.elasticsearchPort = elasticsearchPort;
	}

	public String getGoogleKey() {
		return googleKey;
	}

	public void setGoogleKey(String googleKey) {
		this.googleKey = googleKey;
	}

	public String getElasticsearchHandler() {
		return elasticsearchHandler;
	}

	public void setElasticsearchHandler(String elasticsearchHandler) {
		this.elasticsearchHandler = elasticsearchHandler;
	}

	public Double getIndexRadius() {
		return indexRadius;
	}

	public void setIndexRadius(Double indexRadius) {
		this.indexRadius = indexRadius;
	}

}
