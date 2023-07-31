package com.WReport.model;

import org.springframework.http.HttpStatusCode;

public class JSONResponse {
	private int code;
	private String message;
	
	public JSONResponse() {
		
	}
	
	public JSONResponse(int code, String message) {
		super();
		this.code = code;
		this.message = message;
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
	
	@Override
	public String toString() {
		return "ErrorResponse [errorCode=" + code + ", message=" + message + "]";
	}

	public void setStatusCode(HttpStatusCode statusCode) {
		this.code = statusCode.value();
	}
	
	

}
