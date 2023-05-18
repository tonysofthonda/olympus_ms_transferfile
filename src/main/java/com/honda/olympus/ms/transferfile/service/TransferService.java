package com.honda.olympus.ms.transferfile.service;

import org.springframework.stereotype.Service;
import static org.springframework.util.StringUtils.hasText;

import com.honda.olympus.ms.transferfile.domain.Message;


@Service
public class TransferService 
{

	public void transferFile(Message message)
	{
		if (message.getStatus() == 1 || hasText(message.getFile())) {
			
		}
	}
	
}
