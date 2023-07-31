package com.WReport.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.WReport.exception.InterceptorIOException;
import com.WReport.exception.InvalidInputException;
import com.WReport.exception.WeatherException;
import com.WReport.exception.NotFoundException;
import com.WReport.model.CurrentModel;
import com.WReport.model.CurrentModel.Weather;
import com.WReport.model.GeoModel;
import com.WReport.model.LocationModel;
import com.WReport.model.SessionModel;
import com.WReport.model.StateModel;
import com.WReport.repo.SessionModelRepository;
import com.WReport.validator.CountryCodeValidator;

@Service
public class WeatherServiceImpl implements WeatherService{
	
	@Autowired
	private RestTemplate restTemplate;
	
	//String prese dalle application.properties
	//URL Strings
	//https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}
	@Value("${baseUrlCurrent.url}")
	private String baseUrlCurrent;
	//http://api.openweathermap.org/geo/1.0/zip?zip={zip code},{country code}&appid={API key}
	@Value("${zipUrlGeo.url}")
	private String zipUrlGeo;
	//http://api.openweathermap.org/geo/1.0/direct?q={city name},{state code},{country code}&limit={limit}&appid={API key}
	@Value("${stateGeo.url}")
	private String stateGeo;
	@Value("${keyCurrent.key}")
	private String keyCurrent;
	@Value("${keyGeo.key}")
	private String keyGeo;
	
	//Regex String
	@Value("${regex}")
	private String regex;
	
	//Error Strings
	@Value("${invalidZipCountry.input}")
	private String invalidZipCountry;
	@Value("${session.notPresent}")
	private String sessionNotPresent;
	@Value("${id.notValid}")
	private String idNotValid;
	@Value("${preferences.notPresent}")
	private String preferencesNotPresent;
	
	//Logger
	@Value("${logger.id.error}")
	private String loggerIdError;
	@Value("${logger.api.error}")
	private String loggerApiError;
	@Value("${logger.api.info}")
	private String loggerApiInfo;
	@Value("${logger.code.error}")
	private String loggerZipCountryError;
	@Value("${logger.session.error}")
	private String loggerSessionError;
	@Value("${logger.locationAdd.info}")
	private String loggerLocationAdded;
	@Value("${logger.locationAddList.info}")
	private String loggerLocationAddedList;
	@Value("${logger.locationList.info}")
	private String loggerLocationList;
	@Value("${logger.locationDeleted.info}")
	private String loggerLocationDeleted;
	@Value("${logger.sessionDeleted.info}")
	private String loggerSessionDeleted;
	@Value("${logger.sessionCreated.info}")
	private String loggerSessionCreated;
	@Value("${logger.sessionUpdated.info}")
	private String loggerSessionUpdated;
	@Value("${logger.preferencesUpdated.info}")
	private String loggerPreferencesUpdated;
	@Value("${logger.preferencesDeleted.info}")
	private String loggerPreferencesDeleted;
	@Value("${logger.IOException.error}")
	private String ioError;
	
	
	@Autowired
	private CountryCodeValidator countryCodeValidator;
	
	//Usiamo operazioni CRUD nel service grazie all'interfaccia depository che estende CRUDRepository
	private final SessionModelRepository sessionModelRepository;
	
	@Autowired
	public WeatherServiceImpl(SessionModelRepository sessionModelRepository) {
		this.sessionModelRepository = sessionModelRepository;
	}
	
	public SessionModel saveSessionModel(SessionModel sessionModel) {
		return sessionModelRepository.save(sessionModel);
	}
	
	public Optional<SessionModel> getSessionById(long id) {
		return sessionModelRepository.findById(id);
	}
	
	
	private static final Logger serviceLogger = LoggerFactory.getLogger(WeatherServiceImpl.class);
	
	@Override
	public boolean savePreferences(SessionModel e) {
		try {
			boolean b;
			//Prendiamo l'ID
		long id = e.getId();
		//Se l'id non è valido lancia l'exception
		if(id == 0.0) {
			serviceLogger.error(loggerIdError);
			throw new WeatherException(idNotValid);
		}
		List<LocationModel> eList = e.getLocationModels();
		for(LocationModel element : eList) {
			//Se sono vuoti zipCode o Country throw exception
			if(element.getZipcode().equals("") || element.getCountry().equals("")) {
				serviceLogger.error(loggerZipCountryError);
				throw new InvalidInputException(invalidZipCountry);
			}
		}
		
		Optional<SessionModel> optionalSession = getSessionById(id);
			//Se non esiste lo creiamo session e lo salviamo
			if(optionalSession.isEmpty()) {
				//Creiamo due liste di LocationModel, uno vuoto e uno una copia di quello in e
				List<LocationModel> mainArray = e.getLocationModels();
				List<LocationModel> newArray = new ArrayList<LocationModel>();
				SessionModel session = new SessionModel(e.getId(), true, newArray);
					//Iteriamo la lista di LocationModel
					for(LocationModel el : mainArray) {
							//Cerchiamo le informazioni che ci servono con getLocationModel
							LocationModel newElement = getLocationModel(el.getZipcode(), el.getCountry());
							//Aggiungiamo l'elemento nella lista e facciamo la connessione ManyToOne/OneToMany
							if(newElement != null) {
									newArray.add(newElement);
									session.addLocationModel(newElement);
									serviceLogger.info(loggerLocationAdded);
								}
					}
				//Settiamo la lista precedentemente vuota, ora piena di oggetti che ci servono,
				//Come lista della nuova session
				//Restituiamo un boolean vero al controller
				sessionModelRepository.save(session);
				serviceLogger.info(loggerSessionCreated);
				b = true;
				return b;
			}
		
			//Se invece esiste prendiamo di nuovo le informazioni aggiornate
			else {
				//Prendiamo la lista di oggetti in e
				List<LocationModel> mainArray = e.getLocationModels();
				List<LocationModel> newArray = new ArrayList<LocationModel>();
				e.setLocationModels(newArray);
					//Iteriamo la lista di LocationModel
					for(LocationModel el : mainArray) {	
							//Cerchiamo le informazioni che ci servono con getLocationModel
							LocationModel newElement = getLocationModel(el.getZipcode(), el.getCountry());
							//Aggiungiamo l'elemento nella lista e facciamo la connessione ManyToOne/OneToMany
								if(newElement != null) {
									newArray.add(newElement);
									e.addLocationModel(newElement);
									serviceLogger.info(loggerLocationAdded);
								}
						}
				//Settiamo la lista precedentemente vuota, ora piena di oggetti che ci servono,
				//Come lista della nuova session
				sessionModelRepository.save(e);
				serviceLogger.info(loggerSessionUpdated);
				b = false;
				return b;
			}
		//Se nel body in input non c'è lo zipcode o il country lanciamo un exception
		}catch(NullPointerException ex) {
			serviceLogger.error(loggerZipCountryError);
			throw new NullPointerException(invalidZipCountry);
		}	
	}

	@Override
	public List<LocationModel> getPrefrences(long id) {
		
		Optional<SessionModel> optionalSession = getSessionById(id);
		//Vediamo se la session è presente nella cache
		if(optionalSession.isPresent()) {
			
			SessionModel s = optionalSession.get();
	
			List<LocationModel> locations = s.getLocationModels();
			List<LocationModel> weatherDescriptions = new ArrayList<>();
			//Iteriamo la lista per prendere tutte le location della lista
			for(LocationModel mainModel : locations) {
				serviceLogger.info(loggerLocationAddedList);
				weatherDescriptions.add(mainModel);
			}
			serviceLogger.info(loggerLocationList);
			//restituiamo una lista con le informazioni che ci servono
			return weatherDescriptions;
			
		}
		
		else {
			serviceLogger.error(loggerSessionError);
			//Non presente nella cache
			throw new NotFoundException(preferencesNotPresent);
		}
	
	}

	@Override
	public List<LocationModel> searchPreferences(SessionModel e) {
		try {
			boolean cache = e.isCache();
			//Se il valore "cache" è vero allora prendiamolo dalla cache
			if(cache) {
				long id = e.getId();
				//Se l'id non è valido lancia l'exception
				if(id == 0.0) {
					serviceLogger.error(loggerIdError);
					throw new WeatherException(idNotValid);
				}
				Optional<SessionModel> optionalSession = getSessionById(id);
					//Controlliamo se è presente nel database
					if(optionalSession.isPresent()) {
						SessionModel session = optionalSession.get();
							//Prendiamo solo i MainModel del body e
							List<LocationModel> mainArray = session.getLocationModels();
							List<LocationModel> descArray = new ArrayList<>();
								for(LocationModel el : mainArray) {
									for(LocationModel el1 : e.getLocationModels()) {
										//Se non sono validi zipCode o Country throw exception
										if(el1.getZipcode().equals("") || el1.getCountry().equals("")) {
											throw new WeatherException(invalidZipCountry);
										}
										if(!isInputValid(el1.getZipcode(), el1.getCountry())) {
											throw new InvalidInputException(invalidZipCountry);
										}
										//Solo se zipcode e country code combaciano col body e con la location in session
										//lo inseriamo nella lista da restituire
										if(el.getZipcode().equals(el1.getZipcode()) && el.getCountry().equals(el1.getCountry())){
											descArray.add(el);
											serviceLogger.info(loggerLocationAddedList);
										}
									}
								}
						return descArray;
						} else {
							serviceLogger.error(loggerSessionError);
							//La sessione non è nella cache
							throw new NotFoundException(sessionNotPresent);
						}
			} else {
				//Se il parametro "cache" è falso allora riprendiamo le informazioni aggiornate
				long id = e.getId();
				Optional<SessionModel> optionalSession = getSessionById(id);
					if(optionalSession.isPresent()) {
						//Creiamo una nuova lista di location da settare per la sessione e
						//Creiamo una nuova lista di location da restituire al controller
						//Entrambe vengono riempite con gli elementi presenti in e
						List<LocationModel> mainArray = e.getLocationModels();
						List<LocationModel> locationArray = new ArrayList<LocationModel>();
						List<LocationModel> returnArray = new ArrayList<LocationModel>();
						e.setLocationModels(locationArray);
						//Prendiamo le informazioni aggiornate
							for(LocationModel el : mainArray) {
								if(el.getZipcode().equals(null)) {
									serviceLogger.error(loggerZipCountryError);
									throw new WeatherException(invalidZipCountry);
								}
									
								if(!isInputValid(el.getZipcode(), el.getCountry())) {
									serviceLogger.error(loggerZipCountryError);
									throw new InvalidInputException(invalidZipCountry);
								}
							
								LocationModel newElement = getLocationModel(el.getZipcode(), el.getCountry());
									if(newElement != null) {
										returnArray.add(newElement);
										e.addLocationModel(newElement);
										serviceLogger.info(loggerLocationAdded);
									}
										
							}
							//Settiamo la cache true per dire che l'abbiamo salvata
							e.setCache(true);
							sessionModelRepository.save(e);
							serviceLogger.info(loggerPreferencesUpdated);
							//Ritorna la lista di location con solo le località aggiornate
							return returnArray;
						}else {
							serviceLogger.error(loggerSessionError);
							//La sessione non è nella cache
							throw new NotFoundException(preferencesNotPresent);
						}
				} 
		}catch(NullPointerException ex) {
			serviceLogger.error(loggerZipCountryError);
			throw new NullPointerException(invalidZipCountry);
		}
		
	}

	@Override
	public boolean deleteSession(long id) {
		boolean b;
		Optional<SessionModel> optionalSession = getSessionById(id);
			//Se la sessione è presente in cache viene eliminata e restituisce true
			if(optionalSession.isPresent()) {
				b = true;
				sessionModelRepository.deleteById(id);
				serviceLogger.info(loggerSessionDeleted);
				return b;
			}
			//Se la sessione non è presente nella cache viene restituito false
			else {
				b = false;
				serviceLogger.error(loggerSessionError);
				return b;
			}
				
	}

	@Override
	public List<LocationModel> deletePreferences(SessionModel e) {
		
		try{
			long id = e.getId();
			//Controlliamo se l'id del body è valido
			if(id == 0.0) {
				serviceLogger.error(loggerIdError);
				throw new WeatherException(idNotValid);
			}
			
			Optional<SessionModel> optionalSession = getSessionById(id);
			
			List<LocationModel> eList = e.getLocationModels();
			
			for(LocationModel element : eList) {
				//Se non sono validi zipCode o Country throw exception
				if(element.getZipcode().equals("") || element.getCountry().equals("")) {
					serviceLogger.error(loggerZipCountryError);
					throw new WeatherException(invalidZipCountry);
				}
				if(!isInputValid(element.getZipcode(), element.getCountry())) {
					serviceLogger.error(loggerZipCountryError);
					throw new InvalidInputException(invalidZipCountry);
				}
			}
			
			//Se la session è presente in cache, creiamo una lista delle location cancellate nella sessione
			if(optionalSession.isPresent()) {		
				
				SessionModel session = optionalSession.get();
				List<LocationModel> deletedLocations = new ArrayList<LocationModel>();
				
				for(LocationModel mainModel : session.getLocationModels()) {
					for(LocationModel el : eList) {
						//Se e solo se la combinazione zipcode e country combacia, aggiungiamo location nella lista
						//e la eliminiamo dalla session
						if(el.getZipcode().equals(mainModel.getZipcode()) && el.getCountry().equalsIgnoreCase(mainModel.getCountry())) {
							mainModel.setSessionModel(null);
							deletedLocations.add(mainModel);
							serviceLogger.info(loggerLocationDeleted);
						}
					}
				}
				//Una volta finito salviamo le modifiche nella cache
				sessionModelRepository.save(session);
				serviceLogger.info(loggerPreferencesDeleted);
				return deletedLocations;
			} else {
				//Se non è presente in cache
				serviceLogger.error(loggerSessionError);
				throw new NotFoundException(sessionNotPresent);
			}
		}
		//Se non esistono i campi zipcode o country nel body lanciamo un'exception
		catch(NullPointerException ex) {
			serviceLogger.error(loggerZipCountryError);
			throw new NullPointerException(invalidZipCountry);			
		}
		
		
		
	}	

	//Metodo principale con le chiamate API esterne per prendere le informazioni necessarie
	public LocationModel getLocationModel(String zipCode, String country) {
		
		try {
			
			if(!isInputValid(zipCode, country)) {
				serviceLogger.error(loggerZipCountryError);
				throw new InvalidInputException(invalidZipCountry);
			}
			
			//Prendiamo tutte le informazioni sulla location che ci servono
			ResponseEntity<GeoModel> response = restTemplate.getForEntity(zipUrlGeo+ zipCode + "," + country + "&appid="+keyGeo, GeoModel.class);
			
			GeoModel geoModels = response.getBody();	
			
			//Prendiamo tutte le informazioni sul meteo della location che ci servono
			ResponseEntity<CurrentModel> secondResponse = restTemplate.getForEntity(baseUrlCurrent + geoModels.getLat() + "&lon=" + geoModels.getLon()
																					  + "&units=metric&appid=" + keyCurrent, CurrentModel.class);
			
			CurrentModel modelResponse = secondResponse.getBody();
			
			//Prendiamo l'informazione dello stato/regione
			ResponseEntity<StateModel[]> stateResponse = restTemplate.getForEntity(stateGeo + geoModels.getName() + "&limit=1&appid=" + keyGeo, StateModel[].class);
			
			StateModel[] state = stateResponse.getBody();
			
			//Creiamo un oggetto con tutto quello che ci serve
			LocationModel locationResponse = new LocationModel();
			
			//Settiamo i parametri dell'oggetto usando i due oggetti creati prima
			locationResponse.setZipcode(geoModels.getZip());
			locationResponse.setLocation(geoModels.getName());
			locationResponse.setCountry(geoModels.getCountry());
			if(state == null || (state != null && state.length == 0)) {
				locationResponse.setState(null);
			} else {
				locationResponse.setState(state[0].getState());
			}			
			locationResponse.setLon(geoModels.getLon());
			locationResponse.setLat(geoModels.getLat());
			List<Weather> weather = modelResponse.getWeather();
				for(Weather weathers : weather) {
					String description = weathers.getDescription();
					locationResponse.setWeatherDesc(description);
				}
			locationResponse.setTemp(modelResponse.getMain().getTemp());
			locationResponse.setMaxTemp(modelResponse.getMain().getTemp_max());
			locationResponse.setMinTemp(modelResponse.getMain().getTemp_min());
			locationResponse.setTime(LocalTime.now());
			
			serviceLogger.info(loggerApiInfo);
			return locationResponse;
			
		}
		catch(HttpClientErrorException e) {
			serviceLogger.error(loggerApiError);
			throw e;
		}
		catch(InterceptorIOException e) {
			serviceLogger.error(ioError);
			throw e;
		}
	}
		
	//Metodo che utilizza il validator per controllare se zipcode e country sono delle proprietà giuste
		public boolean isInputValid(String zipCode, String country) {
				boolean isValidZipCode = zipCode != null && zipCode.matches(regex);
				boolean isValidCountryCode = countryCodeValidator.isCountryCodeValid(country);
				return isValidZipCode && isValidCountryCode;
		}
}
