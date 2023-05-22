package com.honda.olympus.ms.transferfile.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.honda.olympus.ms.transferfile.domain.Event;
import com.honda.olympus.ms.transferfile.domain.Message;
import com.honda.olympus.ms.transferfile.domain.Status;


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
	
	
	@Value("${ms.transferfile.name}")
	private String serviceName; 
	
	
	public void transferFile(Message message) 
	{
		if (message.status() == Status._SUCCESS) 
		{
			if (StringUtils.hasText(message.file())) {
				mftpService.transferFile(message.file(), getNewFileName());
			}
			else {
				logEventService.logEvent( fileErrorEvent() );
			}
		}
		else {
			logEventService.logEvent( statusErrorEvent(message.file()) );
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
	
	private Event statusErrorEvent(String fileName) {
		return new Event(serviceName, Status._FAIL, String.format(MSG_STATUS_ERROR, fileName), fileName);
	}
	
	private Event fileErrorEvent() {
		return new Event(serviceName, Status._FAIL, MSG_FILE_ERROR, "");
	}
	
}
