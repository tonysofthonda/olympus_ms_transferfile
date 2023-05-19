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
import com.honda.olympus.ms.transferfile.util.NetUtil;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class NotificationClient 
{

	private static final String PATH = "/olympus/notification/v1/event";
    private static final int PORT = 8080;
	
	private String url;                                    
	
	
	public NotificationClient(@Value("${notification.url}") String notificationUrl)
	{
		this.url = new StringBuilder()
			.append(NetUtil.isSiteLocalAddress() ? NetUtil.getLocalUrl(PORT) : notificationUrl)
			.append(PATH)
			.toString();
		
		log.info("# ms notification url: {}", this.url);
	}
	
	
	public ResponseEntity<String> sendNotification(Event event) {
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Event> httpEntity = new HttpEntity<>(event, headers);
		return restTemplate.exchange(this.url, HttpMethod.POST, httpEntity, String.class);
	}
	
}
