package com.WReport.model;

import java.util.List;

public class CurrentModel{
	
	//Creiamo due class innestate per le cose che ci interessano
	private List<Weather> weather;
	private Main main;
	
		public static class Weather{
			private int id;
			private String main;
			private String description;
			
			public Weather() {
			}
			
			public Weather(int id, String main, String description) {
				this.id = id;
				this.main = main;
				this.description = description;
			}
			
			
			public int getId() {
				return id;
			}
			public void setId(int id) {
				this.id = id;
			}
			public String getMain() {
				return main;
			}
			public void setMain(String main) {
				this.main = main;
			}
			public String getDescription() {
				return description;
			}
			public void setDescription(String description) {
				this.description = description;
			}

			@Override
			public String toString() {
				return "Weather [id=" + id + ", main=" + main + ", description=" + description + "]";
			}
			
		}
		
		public static class Main{
			private double temp;
			private double temp_min;
			private double temp_max;
			
			public Main() {
			}
			
			public Main(double temp, double temp_min, double temp_max) {
				this.temp = temp;
				this.temp_min = temp_min;
				this.temp_max = temp_max;
			}
			
			public double getTemp() {
				return temp;
			}
			public void setTemp(double temp) {
				this.temp = temp;
			}
			public double getTemp_min() {
				return temp_min;
			}
			public void setTemp_min(double temp_min) {
				this.temp_min = temp_min;
			}
			public double getTemp_max() {
				return temp_max;
			}
			public void setTemp_max(double temp_max) {
				this.temp_max = temp_max;
			}
			
			@Override
			public String toString() {
				return "Main [temp=" + temp + ", temp_min=" + temp_min + ", temp_max=" + temp_max + "]";
			}
			
			
		}

		public CurrentModel() {
		}
		
		public CurrentModel(List<Weather> weather, Main main) {
			this.weather = weather;
			this.main = main;
		}

		public List<Weather> getWeather() {
			return weather;
		}

		public void setWeather(List<Weather> weather) {
			this.weather = weather;
		}

		public Main getMain() {
			return main;
		}

		public void setMain(Main main) {
			this.main = main;
		}

		@Override
		public String toString() {
			return "CurrentModel [weather=" + weather + ", main=" + main + "]";
		}
		
		
	
}
