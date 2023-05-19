package com.honda.olympus.ms.transferfile.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import com.honda.olympus.ms.transferfile.client.ProcessFileClient;
import com.honda.olympus.ms.transferfile.domain.Message;


/**
 * This class contains sociable unit tests used to verify the ProcessFileService behavior
 * Conditions:
 *    - The class should be executed in isolation (mvn test -Dtest="ProcessFileServiceTest2")
 *    - The ProcessFile application (ms.processfile) must be up
 */

@SpringBootTest(classes = {ProcessFileService.class, ProcessFileClient.class})
@TestMethodOrder(OrderAnnotation.class)
public class ProcessFileServiceTest2 
{
	
	static final Integer STATUS = 1; 
	static final String MSG = "SUCCESS";
	static final String FILE = "file1.txt";
	
	
	@Autowired
	ProcessFileService processFileService;
	
	
	@Test
	@Order(1)
	void shouldNotProcessFileInRemoteService() 
	{
		Message message = new Message(null, null, FILE);
		HttpStatus status = processFileService.processFile(message);
		
		assertTrue(status.is4xxClientError());
	}
	
	
	@Test
	@Order(2)
	void shouldProcessFileInRemoteService() 
	{
		Message message = new Message(STATUS, MSG, FILE);
		HttpStatus status = processFileService.processFile(message);
		
		assertTrue(status.is2xxSuccessful());
	}

}
