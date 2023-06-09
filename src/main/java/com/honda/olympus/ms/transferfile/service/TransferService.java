package com.honda.olympus.ms.transferfile.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import com.honda.olympus.ms.transferfile.domain.Event;
import com.honda.olympus.ms.transferfile.domain.Message;
import com.honda.olympus.ms.transferfile.domain.Status;

import lombok.Setter;


@Setter
@Service
public class TransferService 
{
	
	private static final String PREFIX = "ahm";
	private static final String FILE_EXT = ".txt";
	private static final String DATE_FORMAT = "yyyyMMdd";
	private static final String TIME_FORMAT = "HHmmss";
	
	private static final String MSG_STATUS_ERROR = "El mensaje tiene un status no aceptado para el proceso %s";
	private static final String MSG_FILE_ERROR = "NO se recibió el nombre del archivo";
	
	
	@Autowired
	private MftpService mftpService;
	
	@Autowired
	private LogEventService logEventService;
	
	
	@Value("${service.name}")
	private String serviceName; 
	
	
	public void downloadFile(Message message) 
	{
		if (message.getStatus() == Status._SUCCESS) 
		{
			if (StringUtils.hasText(message.getFile())) {
				mftpService.downloadFile(message.getFile(), getNewFileName());
			}
			else {
				Event event = fileErrorEvent();
				logEventService.logEvent(event);
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, event.getMsg());
			}
		}
		else {
			Event event = statusErrorEvent(message);
			logEventService.logEvent(event);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, event.getMsg());
		}
	}
	
	
	private String getNewFileName() {
		LocalDateTime ldt = LocalDateTime.now();
		return new StringBuilder()
			.append(PREFIX)
			.append(ldt.toLocalDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))
            .append(ldt.toLocalTime().format(DateTimeFormatter.ofPattern(TIME_FORMAT)))
            .append(FILE_EXT)
			.toString();
	}
	
	private Event statusErrorEvent(Message message) {
		return new Event(serviceName, Status._FAIL, String.format(MSG_STATUS_ERROR, message), message.getFile());
	}
	
	private Event fileErrorEvent() {
		return new Event(serviceName, Status._FAIL, MSG_FILE_ERROR, "");
	}
	
}
