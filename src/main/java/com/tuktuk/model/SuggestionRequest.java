package com.tuktuk.model;

public class SuggestionRequest {

	double latitude, longitude;
	public int radiusInMeters;

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getRadiusInMeters() {
		return radiusInMeters;
	}

	public void setRadiusInMeters(int radiusInMeters) {
		this.radiusInMeters = radiusInMeters;
	}
}
