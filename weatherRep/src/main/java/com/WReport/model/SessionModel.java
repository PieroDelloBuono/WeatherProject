package com.WReport.model;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class SessionModel implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private long id;
	
	@Column(name = "cache")
	private boolean cache;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "sessionModel")
	@JsonManagedReference
	private List<LocationModel> locationModels;
	
	public SessionModel() {
		this.id = 0;
		this.cache = false;
		this.locationModels = new ArrayList<>();
	}
	
	public SessionModel(long id, boolean cache, List<LocationModel> locationModels) {
		this.id = id;
		this.cache = cache;
		this.locationModels = locationModels;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public boolean isCache() {
		return cache;
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<LocationModel> getLocationModels() {
		if(locationModels == null) {
			locationModels = new ArrayList<>();
		}
		return locationModels;
	}

	public void setLocationModels(List<LocationModel> locationModels) {
		this.locationModels = locationModels;
	}
	
	public void updateLocationModel(LocationModel updatedModel) {
		for(int i = 0; i < locationModels.size(); i++) {
			LocationModel currentModel = locationModels.get(i);
			if(currentModel.getZipcode().equals(updatedModel.getZipcode())) {
				locationModels.set(i, updatedModel);
				break;
			}
		}
		//Usiamo lo stream in caso ci da problemi il Optional<MainModel>
		/*Optional<MainModel> foundModel = mainModels.stream()
				.filter(currentModel -> currentModel.equals(updatedModel))
				.findFirst();
		
		
		foundModel.ifPresent(model -> {
			int index = mainModels.indexOf(model);
			mainModels.set(index, updatedModel);
		});*/
	}
	
	public void removeLocationModel(LocationModel locationModel) {
		this.locationModels.remove(locationModel);
	}
	
	public void addLocationModel(LocationModel locationModel) {
		locationModel.setSessionModel(this);
		locationModels.add(locationModel);
	}

	@Override
	public String toString() {
		return "EntityModel [id=" + id + ", cache=" + cache + ", mainModels=" + locationModels + "]";
	}	
	
	
}
