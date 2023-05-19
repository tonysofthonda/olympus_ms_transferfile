package com.honda.olympus.ms.transferfile.service;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.honda.olympus.ms.transferfile.client.NotificationClient;
import com.honda.olympus.ms.transferfile.domain.Event;


/**
 * This class contains a sociable unit test used to verify the NotificationService behavior
 * Conditions:
 *    - The class should be executed in isolation (mvn test -Dtest="NotificationServiceTest1")
 *    - The Notification application (ms.notification) must be down
 */

@SpringBootTest(classes = {NotificationService.class, NotificationClient.class})
public class NotificationServiceTest1 
{

	@Autowired
	NotificationService notificationService;
	
	
	@Test
	void logExceptionWhenNotificationServiceIsUnreachable() { 
		assertNull( notificationService.sendNotification(new Event()) );
	}
	
}
