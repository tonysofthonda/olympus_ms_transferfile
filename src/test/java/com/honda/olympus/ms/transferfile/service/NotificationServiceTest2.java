package com.honda.olympus.ms.transferfile.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import com.honda.olympus.ms.transferfile.client.NotificationClient;
import com.honda.olympus.ms.transferfile.domain.Event;


/**
 * This class contains sociable unit tests used to verify the NotificationService behavior
 * Conditions:
 *    - The class should be executed in isolation (mvn test -Dtest="NotificationServiceTest2")
 *    - The Notification application (ms.notification) must be up
 */

@SpringBootTest(classes = {NotificationService.class, NotificationClient.class})
@TestMethodOrder(OrderAnnotation.class)
public class NotificationServiceTest2 
{
	
	static final String SOURCE = "ms.transferfile";
	static final Integer STATUS = 1; 
	static final String MSG = "SUCCESS";
	static final String FILE = "file1.txt";
	
	
	@Autowired
	NotificationService notificationService;
	
	
	@Test
	@Order(1)
	void shouldNotSendNotificationInRemoteService() 
	{
		Event event = new Event(null, null, null, FILE);
		HttpStatus status = notificationService.sendNotification(event);
		
		assertTrue(status.is4xxClientError());
	}
	
	
	@Test
	@Order(2)
	void shouldSendNotificationInRemoteService() 
	{
		Event event = new Event(SOURCE, STATUS, MSG, FILE);
		HttpStatus status = notificationService.sendNotification(event);
		
		assertTrue(status.is2xxSuccessful());
	}
	
}
