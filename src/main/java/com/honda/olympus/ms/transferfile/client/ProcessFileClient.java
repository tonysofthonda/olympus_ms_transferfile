package com.honda.olympus.ms.transferfile.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.honda.olympus.ms.transferfile.domain.Message;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class ProcessFileClient 
{
	
	private String url; 
	
	
	public ProcessFileClient(@Value("${ms.processfile.url}") String url) {
		this.url = url;
		log.info("# ms.processfile url: {}", url);
	}
	
	
	public ResponseEntity<String> processFile(Message message) {
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Message> httpEntity = new HttpEntity<>(message, headers);
		return restTemplate.exchange(this.url, HttpMethod.POST, httpEntity, String.class);
	}
	
}
