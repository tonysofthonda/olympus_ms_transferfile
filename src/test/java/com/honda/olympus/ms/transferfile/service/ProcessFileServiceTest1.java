package com.honda.olympus.ms.transferfile.service;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.honda.olympus.ms.transferfile.client.ProcessFileClient;
import com.honda.olympus.ms.transferfile.domain.Message;


/**
 * This class contains a sociable unit test used to verify the ProcessFileService behavior
 * Conditions:
 *    - The class should be executed in isolation (mvn test -Dtest="ProcessFileServiceTest1")
 *    - The ProcessFile application (ms.processfile) must be down
 */

@SpringBootTest(classes = {ProcessFileService.class, ProcessFileClient.class})
public class ProcessFileServiceTest1 
{
	
	@Autowired
	ProcessFileService processFileService; 
	
	
	@Test
	void logExceptionWhenProcessFileServiceIsUnreachable() { 
		assertNull( processFileService.processFile(new Message()) );
	}
	
}
