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
import com.honda.olympus.ms.transferfile.util.NetUtil;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class ProcessFileClient 
{
	
	private static final String PATH = "/olympus/processfile/v1/file";
	private static final int PORT = 8085;
	
	private String url; 
	
	
	public ProcessFileClient(@Value("${processfile.url}") String processFileUrl) 
	{
		this.url = new StringBuilder()
			.append(NetUtil.isSiteLocalAddress() ? NetUtil.getLocalUrl(PORT) : processFileUrl)
			.append(PATH)
			.toString();
		
		log.info("# ms processfile url: {}", this.url);
	}
	
	
	public ResponseEntity<String> processFile(Message message) {
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Message> httpEntity = new HttpEntity<>(message, headers);
		return restTemplate.exchange(this.url, HttpMethod.POST, httpEntity, String.class);
	}
	
}
