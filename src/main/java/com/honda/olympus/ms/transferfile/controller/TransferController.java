package com.honda.olympus.ms.transferfile.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.honda.olympus.ms.transferfile.domain.Message;
import com.honda.olympus.ms.transferfile.service.TransferService;
import com.honda.olympus.ms.transferfile.util.HttpUtil;



@RestController
public class TransferController 
{
	
	@Autowired
	private TransferService transferService;
	
	
	@PostMapping(path = "/message", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> transferFile(@Valid @RequestBody Message message, Errors errors)
	{
		HttpUtil.handleBadRequest(errors);
		this.transferService.transferFile(message);
		
		return ResponseEntity.ok(HttpStatus.OK.getReasonPhrase());
	}
	
}
