package com.honda.olympus.ms.transferfile.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.web.server.ResponseStatusException;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

import com.honda.olympus.ms.transferfile.domain.Message;
import com.honda.olympus.ms.transferfile.domain.Status;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@TestMethodOrder(OrderAnnotation.class)
public class TransferServiceTest 
{
	
	static final String SERVICE_NAME = "ms.transferfile";
	
	static MftpService mftpService;
	static LogEventService logEventService;
	static TransferService transferService;
	
	
	@BeforeAll
	static void beforeAll() 
	{
		mftpService = mock(MftpService.class);
		logEventService = mock(LogEventService.class);
		transferService = new TransferService();
		
		transferService.setMftpService(mftpService);
		transferService.setLogEventService(logEventService);
		transferService.setServiceName(SERVICE_NAME);
	}
	
	
	@Test
	@Order(1)
	void exceptionWithInvalidStatus() {
		Message message = new Message(Status._FAIL, Status.FAIL, "");
		
		assertThatThrownBy(() -> transferService.downloadFile(message))
			.isInstanceOf(ResponseStatusException.class)
			.hasMessageContaining("El mensaje tiene un status no aceptado para el proceso ");
	}
	
	
	@Test
	@Order(2)
	void exceptionWithInvalidFile() {
		Message message = new Message(Status._SUCCESS, Status.SUCCESS, "");
		
		assertThatThrownBy( () -> transferService.downloadFile(message) )
			.isInstanceOf(ResponseStatusException.class)
			.hasMessageContaining("NO se recibió el nombre del archivo");
	}
	
	
	@Test
	@Order(3)
	void noExceptionWithValidMessage() {
		Message message = new Message(Status._SUCCESS, Status.SUCCESS, "file.txt");
		assertDoesNotThrow(() -> transferService.downloadFile(message));
	}
	
	
	@Test
	@Order(4)
	void shouldReturnNewFileName() throws Exception
	{
		Method getNewFileName = transferService.getClass().getDeclaredMethod("getNewFileName");
		getNewFileName.setAccessible(true);
		
		String current = (String) getNewFileName.invoke(transferService);
		log.info("# newFileName: {}", current);
		
		assertTrue(Pattern.matches("ahm\\d{8}\\d{6}.txt", current));
	}
		
}
