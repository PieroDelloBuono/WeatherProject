package com.WReport.model;

import java.util.Arrays;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class HttpClientErrorHandler {
	
	private HttpStatusCode statusCode;
	private byte[] responseBody;
	private HttpHeaders responseHeaders;
	private String statusText;
	private String message;
	
	public HttpClientErrorHandler() {
		
	}

	public HttpClientErrorHandler(HttpStatus statusCode, byte[] responseBody, HttpHeaders responseHeaders,
			String statusText, String message) {
		this.statusCode = statusCode;
		this.responseBody = responseBody;
		this.responseHeaders = responseHeaders;
		this.statusText = statusText;
		this.message = message;
	}

	public HttpStatusCode getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(HttpStatusCode httpStatusCode) {
		this.statusCode = httpStatusCode;
	}

	public byte[] getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(byte[] responseBody) {
		this.responseBody = responseBody;
	}

	public HttpHeaders getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(HttpHeaders responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "HttpClientErrorHandler [statusCode=" + statusCode + ", responseBody=" + Arrays.toString(responseBody)
				+ ", responseHeaders=" + responseHeaders + ", statusText=" + statusText + ", message=" + message + "]";
	}

}
