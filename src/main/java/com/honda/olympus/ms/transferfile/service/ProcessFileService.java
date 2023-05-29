package com.honda.olympus.ms.transferfile.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import com.honda.olympus.ms.transferfile.client.ProcessFileClient;
import com.honda.olympus.ms.transferfile.domain.Message;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class ProcessFileService 
{
	
	@Autowired
	private ProcessFileClient processFileClient;
	
	
	public HttpStatus processFile(Message message) {
		try {
			ResponseEntity<String> response = this.processFileClient.processFile(message);
			return response.getStatusCode();
		}
		catch (HttpClientErrorException exception) {
			log.error("### ProcessFile request error: {}", exception.getResponseBodyAsString());
			return exception.getStatusCode();
		}
		catch (RestClientException exception) {
			log.error("### Unable to connect to the ProcessFile endpoint !");
			return null;
		}
	}
	
}
