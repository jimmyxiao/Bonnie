package com.bonniedraw.notification.service.impl;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bonniedraw.notification.service.PushNotificationsService;

@Service
public class PushNotificationsServiceImpl implements PushNotificationsService {
	private static final String FIREBASE_SERVER_KEY = "AAAAhwQRRww:APA91bGYB0twiYVgI4U5KEs2SRXJFExGsqwPbiByvV9iH9y1OAt-xeKqtiFBTwAqyFxOXkDrR4MN824dT9AnJHIt7cUGbLZ6gZVKRVlHUIOtmb3vISp-K2Fomhbjys-EHlpEFjubdb0r";
	private static final String FIREBASE_API_URL = "https://fcm.googleapis.com/fcm/send";
	
	@Override
	@Async
	public CompletableFuture<String> send(HttpEntity<String> entity) {
		RestTemplate restTemplate = new RestTemplate();
		ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + FIREBASE_SERVER_KEY));
		interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
		restTemplate.setInterceptors(interceptors);
		String firebaseResponse = restTemplate.postForObject(FIREBASE_API_URL, entity, String.class);
		
		return CompletableFuture.completedFuture(firebaseResponse);
	}
	
}
