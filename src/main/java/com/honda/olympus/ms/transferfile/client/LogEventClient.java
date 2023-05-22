package com.honda.olympus.ms.transferfile.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.honda.olympus.ms.transferfile.domain.Event;
import static com.honda.olympus.ms.transferfile.util.NetUtil.*;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class LogEventClient 
{
	
	private String url; 
	
	
	public LogEventClient(
		@Value("${ms.logevent.url}") String url, 
		@Value("${ms.logevent.path}") String path, 
		@Value("${ms.logevent.port}") int port) 
	{
		String baseUrl = isSiteLocalAddress() ? buildLocalBaseUrl(false, port) : url;
		this.url = fixSlashes(concat(baseUrl, path));
		
		log.info("# ms.logevent url: {}", this.url);
	}
	
	
	public ResponseEntity<String> logEvent(Event event) {
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Event> httpEntity = new HttpEntity<>(event, headers);
		return restTemplate.exchange(this.url, HttpMethod.POST, httpEntity, String.class);
	}
	
}
