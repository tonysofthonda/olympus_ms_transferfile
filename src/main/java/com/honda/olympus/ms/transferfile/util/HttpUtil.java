package com.honda.olympus.ms.transferfile.util;

import static java.util.stream.Collectors.joining;

import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public final class HttpUtil 
{	
	
	private HttpUtil() { }
	
	
	public static void handleBadRequest(Errors errors) {
		if (errors.hasErrors()) {
			String messages = errors.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(joining("; "));
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messages);
		}
	}
	
	
	public static void handleBadResponse(String message, Exception exception) {
		log.error("### {}", message, exception);
		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message);
	}
	
}

