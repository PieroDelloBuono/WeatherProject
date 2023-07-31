package com.WReport.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.WReport.exception.WeatherException;
import com.WReport.exception.InterceptorIOException;
import com.WReport.exception.InvalidInputException;
import com.WReport.exception.NotFoundException;
import com.WReport.model.SessionModel;
import com.WReport.model.LocationModel;
import com.WReport.model.JSONResponse;
import com.WReport.model.PreferencesListResponse;
import com.WReport.model.HttpClientErrorHandler;
import com.WReport.service.WeatherService;

@RestController
@RequestMapping("/api")
public class WeatherController {
	
	//String saved 
	@Value("${preferences.notFound}")
	private String preferencesNotFound;
	@Value("${preferences.deleted}")
	private String preferencesDeleted;
	@Value("${sessionNotPresent}")
	private String sessionNotPresent;
	@Value("${session.deleted}")
	private String sessionDeleted;
	@Value("${locationList}")
	private String locationList;
	@Value("${session.noLocations}")
	private String sessionNoLocations;
	@Value("${session.saved}")
	private String sessionSaved;
	@Value("${session.updated}")
	private String sessionUpdated;
	
	//Logger Strings
	//Logger
	@Value("${logger.id.error}")
	private String loggerIdError;
	@Value("${logger.api.error}")
	private String loggerApiError;
	@Value("${logger.code.error}")
	private String loggerZipCountryError;
	@Value("${logger.session.error}")
	private String loggerSessionError;
	@Value("${logger.sessionDeleted.info}")
	private String loggerSessionDeleted;
	@Value("${logger.sessionCreated.info}")
	private String loggerSessionCreated;
	@Value("${logger.sessionUpdated.info}")
	private String loggerSessionUpdated;
	@Value("${logger.preferencesDeleted.info}")
	private String loggerPreferencesDeleted;
	@Value("${logger.preferencesNotFound.error}")
	private String loggerPreferencesNotFound;
	@Value("${logger.locationListEmpty.warn}")
	private String loggerListEmpty;
	@Value("${logger.locationReturned.info}")
	private String loggerReturned;
	@Value("${logger.IOException.error}")
	private String ioError;
	
	private static final Logger controllerLogger = LoggerFactory.getLogger(WeatherController.class);
	
	@Autowired
	private WeatherService weatherService;
	
	@PostMapping("savePreferences")
	public ResponseEntity<?> salvaPref(@RequestBody SessionModel e){
		try{
			boolean b = weatherService.savePreferences(e);
			if(b) {
				JSONResponse response = new JSONResponse();
				response.setCode(HttpStatus.OK.value());
				response.setMessage(sessionSaved);
				controllerLogger.info(loggerSessionCreated);
				return ResponseEntity.ok(response);
			}
			else {
				JSONResponse response = new JSONResponse();
				response.setCode(HttpStatus.OK.value());
				response.setMessage(sessionUpdated);
				controllerLogger.info(loggerSessionUpdated);
				return ResponseEntity.ok(response);
			}
			
		}
		//Questa exception serve se l'id non è valido
		catch(WeatherException ex) {
			JSONResponse errorResponse = new JSONResponse();
			errorResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			errorResponse.setMessage(ex.getMessage());
			controllerLogger.error(loggerIdError);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
			
		}
		//Questa exception serve se zipcode e/o country non sono presenti nel body
		catch(NullPointerException ex) {
			JSONResponse errorResponse = new JSONResponse();
			errorResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			errorResponse.setMessage(ex.getMessage());
			controllerLogger.error(loggerZipCountryError);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
			
		}
		//Questa exception serve se zipcode e/o country sono vuoti
		catch(InvalidInputException ex) {
			JSONResponse errorResponse = new JSONResponse();
			errorResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			errorResponse.setMessage(ex.getMessage());
			controllerLogger.error(loggerZipCountryError);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		
		}
		//Questa exception serve per gestire le IOException dell'interceptor
		catch(InterceptorIOException ex){
			JSONResponse errorResponse = new JSONResponse();
			errorResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			errorResponse.setMessage(ex.getMessage());
			controllerLogger.error(ioError);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
		//Questa exception serve per controllare gli excepiton delle chiamate esterne API
		catch(HttpClientErrorException ex) {
			JSONResponse errorResponse = new JSONResponse();
			errorResponse.setStatusCode(ex.getStatusCode());
			errorResponse.setMessage(ex.getMessage());
			controllerLogger.error(loggerApiError);
			return ResponseEntity.status(errorResponse.getCode()).body(errorResponse);
			
		}
	}
	
	@GetMapping("getPreferences/{id}")
	public ResponseEntity<?> getPrefrences(@PathVariable("id") long id){
			try{
				List<LocationModel> list = (List<LocationModel>) weatherService.getPrefrences(id);
				if(list.isEmpty()) {
					JSONResponse response = new JSONResponse();
					response.setCode(HttpStatus.OK.value());
					response.setMessage(sessionNoLocations);
					controllerLogger.warn(loggerListEmpty);
					return ResponseEntity.ok(response);
				}
				controllerLogger.info(loggerReturned);
				return ResponseEntity.ok(list);
				
			}
			//Questa exception serve se la sessione non è presente in cache
			catch (NotFoundException ex){
				JSONResponse errorResponse = new JSONResponse();
				errorResponse.setCode(HttpStatus.NOT_FOUND.value());
				errorResponse.setMessage(ex.getMessage());
				controllerLogger.error(loggerIdError);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
			}
	}
	
	@GetMapping("searchPreferences")
	public ResponseEntity<?> cercaPref(@RequestBody SessionModel e){
			try{
				List<LocationModel> list = weatherService.searchPreferences(e);
					if(list.isEmpty()) {
						JSONResponse errorResponse = new JSONResponse();
						errorResponse.setCode(HttpStatus.NOT_FOUND.value());
						errorResponse.setMessage(preferencesNotFound);
						controllerLogger.error(loggerPreferencesNotFound);
						return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
					}
				PreferencesListResponse response = new PreferencesListResponse();
				response.setCode(HttpStatus.OK.value());
				response.setMessage(locationList);
				response.setList(list);
				controllerLogger.info(loggerReturned);
				return ResponseEntity.ok(response);
				
			}
			//Questa exception serve se l'id non è valido
			catch(WeatherException ex) {
				JSONResponse errorResponse = new JSONResponse();
				errorResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				errorResponse.setMessage(ex.getMessage());
				controllerLogger.error(loggerIdError);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
				
			}
			//Questa exception serve se zipcode e/o country non sono presenti nel body
			catch(NullPointerException ex) {
				JSONResponse errorResponse = new JSONResponse();
				errorResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				errorResponse.setMessage(ex.getMessage());
				controllerLogger.error(loggerZipCountryError);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
				
			}
			//Questa exception serve se zipcode e/o country sono vuoti
			catch(InvalidInputException ex) {
				JSONResponse errorResponse = new JSONResponse();
				errorResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				errorResponse.setMessage(ex.getMessage());
				controllerLogger.error(loggerZipCountryError);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
				
			}
			//Questa exception serve se la sessione non è presente in cache
			catch(NotFoundException ex) {
				JSONResponse errorResponse = new JSONResponse();
				errorResponse.setCode(HttpStatus.NOT_FOUND.value());
				errorResponse.setMessage(ex.getMessage());
				controllerLogger.error(loggerSessionError);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
			
			}
			//Questa exception serve per gestire le IOException dell'interceptor
			catch(InterceptorIOException ex){
				JSONResponse errorResponse = new JSONResponse();
				errorResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
				errorResponse.setMessage(ex.getMessage());
				controllerLogger.error(ioError);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
			}
			//Questa exception serve per controllare gli excepiton delle chiamate esterne API
			catch(HttpClientErrorException ex) {
				JSONResponse errorResponse = new JSONResponse();
				errorResponse.setStatusCode(ex.getStatusCode());
				errorResponse.setMessage(ex.getMessage());
				controllerLogger.error(loggerApiError);
				return ResponseEntity.status(errorResponse.getCode()).body(errorResponse);
				
			}
	}
	
	@DeleteMapping("deleteSession/{id}")
	public ResponseEntity<?> deleteById(@PathVariable("id") long id) {
			boolean b = weatherService.deleteSession(id);
			JSONResponse response = new JSONResponse();
			
			if(b) {
				response.setCode(HttpStatus.OK.value());
				response.setMessage(sessionDeleted);
				controllerLogger.info(loggerSessionDeleted);
				return ResponseEntity.ok(response);
			}
			else {
				response.setCode(HttpStatus.NOT_FOUND.value());
				response.setMessage(sessionNotPresent);
				controllerLogger.error(loggerSessionError);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
	}
	
	@DeleteMapping("deletePreferences")
	public ResponseEntity<?> deletePreferences(@RequestBody SessionModel e) {
		try{
			JSONResponse response = new JSONResponse();
			PreferencesListResponse deletedResponse = new PreferencesListResponse();
			List<LocationModel> list = weatherService.deletePreferences(e);
			if(!list.isEmpty()) {
				deletedResponse.setCode(HttpStatus.OK.value());
				deletedResponse.setMessage(preferencesDeleted);
				deletedResponse.setList(list);
				controllerLogger.info(loggerPreferencesDeleted);
				return ResponseEntity.ok(deletedResponse);
			}
			else {
				response.setCode(HttpStatus.NOT_FOUND.value());
				response.setMessage(preferencesNotFound);
				controllerLogger.error(loggerPreferencesNotFound);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); 
			}
		}
		//Questa exception serve se l'id non è valido
		catch(WeatherException ex) {
			JSONResponse errorResponse = new JSONResponse();
			errorResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			errorResponse.setMessage(ex.getMessage());
			controllerLogger.error(loggerIdError);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
			
		}
		//Questa exception serve se country e/o zipcode non sono presenti nel body
		catch(NullPointerException ex) {
			JSONResponse errorResponse = new JSONResponse();
			errorResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			errorResponse.setMessage(ex.getMessage());
			controllerLogger.error(loggerZipCountryError);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
			
		}
		//Questa exception serve se country e/o zipcode non sono validi
		catch(InvalidInputException ex) {
			JSONResponse errorResponse = new JSONResponse();
			errorResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			errorResponse.setMessage(ex.getMessage());
			controllerLogger.error(loggerZipCountryError);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
			
		}
		//Questa exception serve se la sessione non è presente in cache
		catch(NotFoundException ex) {
			JSONResponse errorResponse = new JSONResponse();
			errorResponse.setCode(HttpStatus.NOT_FOUND.value());
			errorResponse.setMessage(ex.getMessage());
			controllerLogger.error(loggerSessionError);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
		
		}
		//Questa exception serve per gestire le IOException dell'interceptor
				catch(InterceptorIOException ex){
					JSONResponse errorResponse = new JSONResponse();
					errorResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
					errorResponse.setMessage(ex.getMessage());
					controllerLogger.error(ioError);
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
				}
		//Questa exception serve per controllare gli excepiton delle chiamate esterne API
		catch(HttpClientErrorException ex) {
			
			HttpClientErrorHandler errorHandler = new HttpClientErrorHandler();
			errorHandler.setMessage(ex.getMessage());
			errorHandler.setResponseBody(ex.getResponseBodyAsByteArray());
			errorHandler.setResponseHeaders(ex.getResponseHeaders());
			errorHandler.setStatusCode(ex.getStatusCode());
			errorHandler.setStatusText(ex.getStatusText());
			controllerLogger.error(loggerApiError);
			return ResponseEntity.status(errorHandler.getStatusCode()).body(errorHandler);					
		}
			
	}
	
}
