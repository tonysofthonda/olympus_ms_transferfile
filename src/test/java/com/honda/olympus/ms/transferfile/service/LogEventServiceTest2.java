package com.honda.olympus.ms.transferfile.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import com.honda.olympus.ms.transferfile.domain.Event;


/**
 * This class contains sociable unit tests used to verify the LogEventService's behavior
 * Conditions:
 *    - It is recommended to run the class in isolation (mvn test -Dtest="LogEventServiceTest2")
 *    - The LogEvent application (ms.logevent) must be up
 */

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class LogEventServiceTest2
{
	
	static final String SOURCE = "ms.transferfile";
	static final Integer STATUS = 1; 
	static final String MSG = "SUCCESS";
	static final String FILE = "file1.txt";
	
	
	@Autowired
	private LogEventService logEventService;
	
	
	@Test
	@Order(1)
	void shouldNotLogEventInRemoteService() 
	{
		Event event = new Event(null, null, null, FILE);
		HttpStatus status = logEventService.logEvent(event);
		
		assertTrue(status.is4xxClientError());
	}
	
	
	@Test
	@Order(2)
	void shouldLogEventInRemoteService() 
	{
		Event event = new Event(SOURCE, STATUS, MSG, FILE);
		HttpStatus status = logEventService.logEvent(event);
		
		assertTrue(status.is2xxSuccessful());
	}
	
}
