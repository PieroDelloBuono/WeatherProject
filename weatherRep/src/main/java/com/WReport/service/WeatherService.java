package com.WReport.service;

import java.util.List;

import com.WReport.model.LocationModel;
import com.WReport.model.SessionModel;

public interface WeatherService {
	boolean savePreferences(SessionModel e);
	List<LocationModel> getPrefrences(long id);
	List<LocationModel> searchPreferences(SessionModel e);
	boolean deleteSession(long id);
	List<LocationModel> deletePreferences(SessionModel e);
}
