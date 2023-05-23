package com.honda.olympus.ms.transferfile.util;

import static java.util.stream.Collectors.joining;

import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.server.ResponseStatusException;


public final class HttpUtil 
{	
	
	private HttpUtil() { }
	
	public static void handleBadRequest(Errors errors) {
		if (errors.hasErrors()) {
			String messages = errors.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(joining("; "));
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messages);
		}
	}
	
}

