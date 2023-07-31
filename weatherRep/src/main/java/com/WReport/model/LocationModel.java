package com.WReport.model;

import java.io.Serializable;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
//Questa è l'entity che prenderà tutte le informazioni delle due classi e verrà salvata in cache
public class LocationModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "zipcode")
	//Codice "postale"
	private String zipcode;
	@Column(name = "location")
	//"name" di LocationModel
	private String location;
	@Column(name = "country")
	//"country" di LocationModel
	private String country;
	@Column(name = "state")
	//se non è vuoto dovrebbe essere "state" di LocationModel
	private String state;
	@Column(name = "lon")
	//Prese tutte e due da LocationModel, "lat" e "lon"
	private float lon;
	@Column(name = "lat")
	private float lat;
	@Column(name = "weatherDesc")
	//"description" di Weather.description della classe CurrentModel
	private String weatherDesc;
	@Column(name = "temp")
	//"temp", "temp_max" e "temp_min" di Main nella classe CurrentModel
	private double temp;
	@Column(name = "maxTemp")
	private double maxTemp;
	@Column(name = "minTemp")
	private double minTemp;
	@Column(name = "timeStamp")
	private LocalTime time;
	
	@ManyToOne
	@JoinColumn(name = "Session_Model_id")
	@JsonBackReference
	private SessionModel sessionModel;
	
	public LocationModel() {
	}

	public LocationModel(String zipcode, String location, String country, String state, float lon, float lat,
			String weatherDesc, double temp, double maxTemp, double minTemp, SessionModel sessionModel, LocalTime time) {
		this.zipcode = zipcode;
		this.location = location;
		this.country = country;
		this.state = state;
		this.lon = lon;
		this.lat = lat;
		this.weatherDesc = weatherDesc;
		this.temp = temp;
		this.maxTemp = maxTemp;
		this.minTemp = minTemp;
		this.time = time;
		this.sessionModel = sessionModel;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public float getLon() {
		return lon;
	}

	public void setLon(float lon) {
		this.lon = lon;
	}

	public float getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}

	public String getWeatherDesc() {
		return weatherDesc;
	}

	public void setWeatherDesc(String weather) {
		this.weatherDesc = weather;
	}

	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}

	public double getMaxTemp() {
		return maxTemp;
	}

	public void setMaxTemp(double maxTemp) {
		this.maxTemp = maxTemp;
	}

	public double getMinTemp() {
		return minTemp;
	}

	public void setMinTemp(double minTemp) {
		this.minTemp = minTemp;
	}
	
	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public SessionModel getSessionModel() {
		return sessionModel;
	}

	public void setSessionModel(SessionModel sessionModel) {
		this.sessionModel = sessionModel;
	}

	@Override
	public String toString() {
		return "LocationModel [zipcode=" + zipcode + ", location=" + location + ", country=" + country
				+ ", state=" + state + ", lon=" + lon + ", lat=" + lat + ", weatherDesc=" + weatherDesc + ", temp="
				+ temp + ", maxTemp=" + maxTemp + ", minTemp=" + minTemp + ", time=" + time + ", sessionModel="
				+ sessionModel + "]";
	}
	
		
}
