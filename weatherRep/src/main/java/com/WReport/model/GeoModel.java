package com.WReport.model;

public class GeoModel{
	
	private String name;
	private float lat;
	private float lon;
	private String country;
	private String zip;
	
	public GeoModel() {
	}
	
	public GeoModel(String name, float lat, float lon, String country,
			String zip) {
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.country = country;
		this.zip = zip;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getLat() {
		return lat;
	}
	public void setLat(float lat) {
		this.lat = lat;
	}
	public float getLon() {
		return lon;
	}
	public void setLon(float lon) {
		this.lon = lon;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	@Override
	public String toString() {
		return "GeoModel [name=" + name + ", lat=" + lat + ", lon=" + lon
				+ ", country=" + country + ", zip=" + zip + "]";
	}
	
	

}
