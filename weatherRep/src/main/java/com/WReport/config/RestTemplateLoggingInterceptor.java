package com.WReport.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import org.springframework.util.StreamUtils;

import com.WReport.exception.InterceptorIOException;

import java.io.ByteArrayInputStream;

public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(RestTemplateLoggingInterceptor.class);
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		try {
			logRequest(request, body);
		ClientHttpResponse response = execution.execute(request, body);
		//Creiamo una nuova response usata solo per il logging, così la risposta originale viene letta senza problemi
		response = new LoggingClientHttpResponse(response);
		logResponse(response);
		return response;
		} catch(IOException e) {
			throw new InterceptorIOException(e.getMessage());
		}		
	}
	
	private void logRequest(HttpRequest request, byte[] body) {
		logger.debug("==================================== Request Begin =======================================");
		logger.debug("URI: {}", request.getURI());
		logger.debug("Method: {}", request.getMethod());
		logger.debug("Headers: {}", request.getHeaders());
		logger.debug("Response body: {}", new String(body, StandardCharsets.UTF_8));
		logger.debug("==================================== Request End =========================================");
	}
	
	private void logResponse(ClientHttpResponse response) throws IOException{
		logger.debug("==================================== Response Begin =======================================");
		logger.debug("Status Code: {}", response.getStatusCode());
		logger.debug("Status text: {}", response.getStatusText());
		logger.debug("Headers: {}", response.getHeaders());
		logger.debug("Response body: {}", StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8));
		logger.debug("==================================== Response End =========================================");
	}
		
	//Creiamo una classe customizzata per fare il logging del response body
	//Senza questa classe la chiamata API viene letta solo una volta e poi scartata 
	private static class LoggingClientHttpResponse implements ClientHttpResponse {
		
		private final ClientHttpResponse originalResponse;
		private final byte[] bodyBytes;
		
		public LoggingClientHttpResponse(ClientHttpResponse originalResponse) throws IOException {
			this.originalResponse = originalResponse;
			this.bodyBytes = StreamUtils.copyToByteArray(originalResponse.getBody());
		}
		
		@Override
		public InputStream getBody() throws IOException {
			return new ByteArrayInputStream(bodyBytes);
		}
		@Override
		public HttpHeaders getHeaders() {
			return originalResponse.getHeaders();
		}
		@Override
		public HttpStatusCode getStatusCode() throws IOException {
			return originalResponse.getStatusCode();
		}
		@SuppressWarnings("deprecation")
		@Override
		public int getRawStatusCode() throws IOException {
			return originalResponse.getRawStatusCode();
		}
		@Override
		public String getStatusText() throws IOException {
			return originalResponse.getStatusText();
		}
		@Override
		public void close() {
			originalResponse.close();
			
		}
	}
}
