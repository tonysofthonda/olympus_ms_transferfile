package com.honda.olympus.ms.transferfile.controller;

import static java.util.stream.Collectors.joining;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.honda.olympus.ms.transferfile.domain.Event;
import com.honda.olympus.ms.transferfile.domain.Message;
import com.honda.olympus.ms.transferfile.domain.Status;
import com.honda.olympus.ms.transferfile.service.LogEventService;
import com.honda.olympus.ms.transferfile.service.TransferService;


@RestController
public class TransferController 
{
	
	@Autowired 
	private LogEventService logEventService;
	
	@Autowired
	private TransferService transferService;
	
	
	@Value("${service.name}")
	private String serviceName; 
	
	
	@PostMapping(path = "/message", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> transferFile(@Valid @RequestBody Message message, Errors errors)
	{
		handleBadRequest(message, errors);
		this.transferService.transferFile(message);
		
		return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
	}
	
	
	public void handleBadRequest(Message message, Errors errors) {
		if (errors.hasErrors()) {
			String valMsg = errors.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(joining("; "));
			
			Event event = new Event(serviceName, Status._FAIL, valMsg, message.getFile());
			logEventService.logEvent(event);
			
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, valMsg);
		}
	}
	
}
