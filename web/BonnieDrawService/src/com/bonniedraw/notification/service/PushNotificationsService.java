package com.bonniedraw.notification.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpEntity;

public interface PushNotificationsService {
	public CompletableFuture<String> send(HttpEntity<String> entity);
}
