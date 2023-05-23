package com.honda.olympus.ms.transferfile.service;

import org.junit.jupiter.api.TestMethodOrder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.web.server.ResponseStatusException;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.honda.olympus.ms.transferfile.client.MftpClient;
import com.honda.olympus.ms.transferfile.client.MftpConfig;


@TestMethodOrder(OrderAnnotation.class)
public class MftpServiceTest 
{
	
	static final String SERVICE_NAME = "ms.transferfile";
	static final String FILE_NAME = "file1.txt";
	static final String NEW_FILE_NAME = "file2.txt";
	static final String EMPTY = "";
	
	static MftpConfig config;
	static LogEventService logEventService;
	static NotificationService notificationService;
	static ProcessFileService processFileService;
	static MftpService mftpService;
	
	
	@BeforeAll
	static void beforeAll() {
		config = mock(MftpConfig.class);
		logEventService = mock(LogEventService.class);
		notificationService = mock(NotificationService.class);
		processFileService = mock(ProcessFileService.class);
		
		mftpService = new MftpService();
		mftpService.setConfig(config);
		mftpService.setLogEventService(logEventService);
		mftpService.setNotificationService(notificationService);
		mftpService.setProcessFileService(processFileService);
		mftpService.setServiceName(SERVICE_NAME);
	}
	
	
	@Test
	@Order(1)
	void exceptionWithUnreachableFtpServer() 
	{
		MftpClient mftpClient = mock(MftpClient.class);
		when(mftpClient.open()).thenReturn(false);
		
		assertThatThrownBy( () -> mftpService.transferFile(mftpClient, FILE_NAME, NEW_FILE_NAME) )
			.isInstanceOf(ResponseStatusException.class)
			.hasMessageContaining("Fallo de conexión al sitio MFTP, con los siguientes datos");
	}
	
	
	@Test
	@Order(2)
	void exceptionWithMissingFile() 
	{
		MftpClient mftpClient = mock(MftpClient.class);
		
		when(mftpClient.open()).thenReturn(true);
		when(mftpClient.fileExists()).thenReturn(false);
		
		assertThatThrownBy( () -> mftpService.transferFile(mftpClient, FILE_NAME, NEW_FILE_NAME) )
			.isInstanceOf(ResponseStatusException.class)
			.hasMessageContaining("El archivo " + FILE_NAME + ", no encontrado en la ubicación");
	}
	
	
	@Test
	@Order(3)
	void exceptionWithEmptyFile()
	{
		MftpClient mftpClient = mock(MftpClient.class);
		
		when(mftpClient.open()).thenReturn(true);
		when(mftpClient.fileExists()).thenReturn(true);
		when(mftpClient.isFileEmtpy()).thenReturn(true);
		
		assertThatThrownBy( () -> mftpService.transferFile(mftpClient, FILE_NAME, NEW_FILE_NAME) )
			.isInstanceOf(ResponseStatusException.class)
			.hasMessageContaining("El archivo " + FILE_NAME + " no tiene información");
	}
	
	
	@Test
	@Order(4)
	void exceptionWithNoFileDownload()
	{
		MftpClient mftpClient = mock(MftpClient.class);
		
		when(mftpClient.open()).thenReturn(true);
		when(mftpClient.fileExists()).thenReturn(true);
		when(mftpClient.isFileEmtpy()).thenReturn(false);
		when(mftpClient.downloadFile()).thenReturn(false);
		
		assertThatThrownBy( () -> mftpService.transferFile(mftpClient, FILE_NAME, NEW_FILE_NAME) )
			.isInstanceOf(ResponseStatusException.class)
			.hasMessageContaining("Fallo al momento de realizar la copia del archivo " + NEW_FILE_NAME 
				+ ", a la siguiente ubicación definida");
	}
	
	
	@Test 
	@Order(5)
	void noExceptionWithOkFileDownload() 
	{
		MftpClient mftpClient = mock(MftpClient.class);
		
		when(mftpClient.open()).thenReturn(true);
		when(mftpClient.fileExists()).thenReturn(true);
		when(mftpClient.isFileEmtpy()).thenReturn(false);
		when(mftpClient.downloadFile()).thenReturn(true);
		
		assertDoesNotThrow( () -> mftpService.transferFile(mftpClient, FILE_NAME, NEW_FILE_NAME) );
	}
	
}
