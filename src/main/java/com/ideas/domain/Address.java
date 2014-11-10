package com.ideas.domain;

public class Address {
	private final double latitude;
	private final double longitude;
	private String pickUpLocation;
	
	public Address(double latitude, double longitude, String address) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.pickUpLocation = address;
	}
	
	public double getLatitude(){
		return latitude;
	}
	
	public double getLongitude(){
		return longitude;
	}
	
	public String getPickUpLocation(){
		return pickUpLocation;
	}
}
