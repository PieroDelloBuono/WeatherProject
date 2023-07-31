package com.WReport.model;

import java.util.List;

public class PreferencesListResponse {
	private int code;
	private String message;
	private List<LocationModel> list;
	
	public PreferencesListResponse() {
		
	}
	
	public PreferencesListResponse(int code, String message, List<LocationModel> list) {
		this.code = code;
		this.message = message;
		this.list = list;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public List<LocationModel> getList() {
		return list;
	}

	public void setList(List<LocationModel> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "DeletedPreferencesResponse [code=" + code + ", message=" + message + ", list=" + list + "]";
	}
	
	

}
