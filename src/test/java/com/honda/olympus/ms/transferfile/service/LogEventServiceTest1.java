package com.honda.olympus.ms.transferfile.service;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.honda.olympus.ms.transferfile.domain.Event;


/**
 * This class contains a sociable unit test used to verify the LogEventService's behavior
 * Conditions:
 *    - The class must be executed in isolation (mvn test -Dtest="LogEventServiceTest1")
 *    - The LogEvent application (ms.logevent) must be down
 */

@SpringBootTest
public class LogEventServiceTest1 
{
	
	@Autowired
	private LogEventService logEventService;
	
	
	@Test
	void logExceptionWhenLogEventServiceIsUnreachable() { 
		assertNull( logEventService.logEvent(new Event()) );
	}
	
}
