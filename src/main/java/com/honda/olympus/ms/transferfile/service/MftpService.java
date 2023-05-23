package com.honda.olympus.ms.transferfile.service;

import static java.lang.String.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.honda.olympus.ms.transferfile.client.MftpClient;
import com.honda.olympus.ms.transferfile.client.MftpConfig;
import com.honda.olympus.ms.transferfile.domain.Event;
import com.honda.olympus.ms.transferfile.domain.Message;

import lombok.Setter;

import static com.honda.olympus.ms.transferfile.domain.Status.*;


@Setter
@Service
public class MftpService 
{
	
	private static final String MSG_CONNECTION_OK = "Conexión al MFTP %s, %s fue exitosa";
	private static final String MSG_CONNECTION_ERROR = "Fallo de conexión al sitio MFTP, con los siguientes datos: %s, %s";
	private static final String MSG_SEARCH_ERROR = "El archivo %s, no encontrado en la ubicación %s";
	private static final String MSG_INFO_ERROR = "El archivo %s no tiene información";
	private static final String MSG_DOWNLOAD_ERROR = "Fallo al momento de realizar la copia del archivo %s, a la siguiente ubicación definida %s";
	
	
	@Autowired private MftpConfig config;
	@Autowired private LogEventService logEventService;
	@Autowired private NotificationService notificationService;
	@Autowired private ProcessFileService processFileService;
	
	
	@Value("${ms.transferfile.name}")
	private String serviceName; 
	
	
	public void transferFile(String fileName, String newFileName) {
		MftpClient client = new MftpClient(config, fileName, newFileName);
		transferFile(client, fileName, newFileName);
	}
	
	public void transferFile(MftpClient client, String fileName, String newFileName) 
	{	
		// connect to mftp server
		if (!client.open()) {
			Event event = connectionErrorEvent();
			logEventService.logEvent(event);
			notificationService.sendNotification(event);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, event.msg());
		}
		logEventService.logEvent( connectionOkEvent() );
		
		// search file
		if (!client.fileExists()) {
			client.close();
			Event event = searchErrorEvent(fileName);
			logEventService.logEvent(event);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, event.msg());
		}
		
		// verify if empty
		if (client.isFileEmtpy()) {
			client.deleteFile();
			client.close();
			Event event = infoErrorEvent(fileName);
			logEventService.logEvent(event);
			notificationService.sendNotification(event);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, event.msg());
		}
		
		// download file
		if (client.downloadFile()) {
			client.deleteFile();
			client.close();
			logEventService.logEvent( downloadOkEvent(newFileName)  );
			processFileService.processFile( downloadOkMessage(newFileName) );
			return;
		}
		else {
			client.close();
			Event event = downloadErrorEvent(fileName, newFileName);
			logEventService.logEvent(event);
			notificationService.sendNotification(event);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, event.msg());
		}
	}
	
	
	private Event connectionOkEvent() {
		return new Event(serviceName, _SUCCESS, format(MSG_CONNECTION_OK, config.getHost(), config.getPort()), "");
	}
	
	private Event connectionErrorEvent() {
		return new Event(serviceName, _FAIL, format(MSG_CONNECTION_ERROR, config.getHost(), config.getPort()), "");
	}
	
	private Event searchErrorEvent(String fileName) {
		return new Event(serviceName, _FAIL, format(MSG_SEARCH_ERROR, fileName, config.getInbound()), fileName);
	}
	
	private Event infoErrorEvent(String fileName) {
		return new Event(serviceName, _FAIL, format(MSG_INFO_ERROR, fileName), fileName);
	}
	
	private Event downloadOkEvent(String newFileName) {
		return new Event(serviceName, _SUCCESS, SUCCESS, newFileName);
	}
	
	private Message downloadOkMessage(String newFileName) {
		return new Message(_SUCCESS, SUCCESS, newFileName);
	}
	
	private Event downloadErrorEvent(String fileName, String newFileName) {
		return new Event(serviceName, _FAIL, format(MSG_DOWNLOAD_ERROR, newFileName, config.getDestination()), fileName);
	}
	
}
