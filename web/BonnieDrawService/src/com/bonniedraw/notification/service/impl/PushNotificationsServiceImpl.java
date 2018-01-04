package com.bonniedraw.notification.service.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

import com.bonniedraw.notification.service.PushNotificationsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

//@Service
public class PushNotificationsServiceImpl implements PushNotificationsService {
	private static final String FIREBASE_SERVER_KEY = "AAAAhwQRRww:APA91bGYB0twiYVgI4U5KEs2SRXJFExGsqwPbiByvV9iH9y1OAt-xeKqtiFBTwAqyFxOXkDrR4MN824dT9AnJHIt7cUGbLZ6gZVKRVlHUIOtmb3vISp-K2Fomhbjys-EHlpEFjubdb0r";
	private static final String SENDER_ID = "579888826124";
	private static final String FIREBASE_API_URL = "https://fcm.googleapis.com/fcm/send";
	private static final String FIREBASE_GROUP_API_URL = "https://android.googleapis.com/gcm/notification";
	private static final String FIREBASE_GET_GROUP_API_URL = "https://android.googleapis.com/gcm/notification?notification_key_name=";
	
	@Async
	public String createGroup(List<String> registerTokens, String groupName) throws JsonProcessingException{
		ObjectMapper mapper = new ObjectMapper();		
		String notificationKey = null;
		HttpClient httpClient = HttpClientBuilder.create().build();
		ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		ResponseEntity<String> response = null;
		JsonNode jsonBody = mapper.createObjectNode();
		String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonBody);
		ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
		interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
		interceptors.add(new HeaderRequestInterceptor("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8,application/json"));
		interceptors.add(new HeaderRequestInterceptor("project_id", SENDER_ID));
		restTemplate.setInterceptors(interceptors);
		
		response = restTemplate.exchange(FIREBASE_GET_GROUP_API_URL + groupName, HttpMethod.GET,  new HttpEntity<>(jsonString), String.class);
		HttpStatus httpStatus = response.getStatusCode();
		if(httpStatus == HttpStatus.OK){
			try {
				String body = response.getBody();
				JsonNode actualObj = mapper.readTree(body);
				notificationKey = actualObj.get("notification_key").textValue();
				if(notificationKey != null){
					jsonBody = mapper.createObjectNode();			
					((ObjectNode) jsonBody).put("operation", "remove");
					((ObjectNode) jsonBody).put("notification_key_name", groupName);
					((ObjectNode) jsonBody).put("notification_key", notificationKey);
					((ObjectNode) jsonBody).putArray("registration_ids").add(mapper.valueToTree(registerTokens));				
					jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonBody);
					response = restTemplate.exchange(FIREBASE_GROUP_API_URL, HttpMethod.POST, new HttpEntity<>(jsonString), String.class);
//					((ObjectNode) jsonBody).put("operation", "create");
				}
			} catch (IOException e) {
			}
		}
		return notificationKey;
	}
	
	@Override
	@Async
	public CompletableFuture<String> send(HttpEntity<String> entity) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
		interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
		restTemplate.setInterceptors(interceptors);
		String firebaseResponse = null;
		firebaseResponse = restTemplate.postForObject(FIREBASE_API_URL, entity, String.class);
//		ResponseEntity<String> response = restTemplate.exchange(FIREBASE_API_URL, HttpMethod.POST, entity, String.class);

		return CompletableFuture.completedFuture(firebaseResponse);
	}
	
}
