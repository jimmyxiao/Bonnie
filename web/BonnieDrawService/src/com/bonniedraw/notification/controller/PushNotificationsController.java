package com.bonniedraw.notification.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonniedraw.notification.service.PushNotificationsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
public class PushNotificationsController {
	
	private final String TOPIC = "bonniedraw";
	
	@Autowired
	PushNotificationsService pushNotificationsService;
 
	@RequestMapping(value = "/send", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<String> send() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode body = mapper.createObjectNode();

		JsonNode notification = mapper.createObjectNode();
		((ObjectNode)notification).put("title", "JSA Notification");
		((ObjectNode)notification).put("body", "Happy Message!");
		
		JsonNode data = mapper.createObjectNode();
		((ObjectNode)data).put("Key-1", "JSA Data 1");
		((ObjectNode)data).put("Key-2", "JSA Data 2");
 
		((ObjectNode) body).put("to", "/topics/" + TOPIC);
		((ObjectNode) body).put("priority", "high");
		((ObjectNode) body).set("notification", notification);
		((ObjectNode) body).set("data", data);
		String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
 
		HttpEntity<String> request = new HttpEntity<>(jsonString);
 
		CompletableFuture<String> pushNotification = pushNotificationsService.send(request);
		CompletableFuture.allOf(pushNotification).join();
 
		try {
			String firebaseResponse = pushNotification.get();
			return new ResponseEntity<>(firebaseResponse, HttpStatus.OK);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>("Push Notification ERROR!", HttpStatus.BAD_REQUEST);
	}
	
}
