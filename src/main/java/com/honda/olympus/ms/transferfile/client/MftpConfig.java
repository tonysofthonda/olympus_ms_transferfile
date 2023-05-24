package com.honda.olympus.ms.transferfile.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.honda.olympus.ms.transferfile.util.FileUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Data
@Slf4j
@Component
public class MftpConfig 
{
	
	private static final String BASE_DIR = System.getProperty("java.io.tmpdir");
	
	private String host;
	private int port;
	@Value("${user}") private String user;
	@Value("${pass}") private String pass;
	private String inbound;
	private String destination;
	
	
	public MftpConfig(
		@Value("${host}") String host, 
		@Value("${port}") int port, 
		@Value("${inbound}") String inbound,  
		@Value("${destination}") String destination) 
	{
		this.host = host;
		this.port = port;
		this.inbound = FileUtil.fixSlashes( String.format(inbound, BASE_DIR) );
		this.destination = FileUtil.fixSlashes( String.format(destination, BASE_DIR) );
		
		log.info("# mftp host: {}", host);
		log.info("# mftp port: {}", port);
		log.info("# mftp inbound: {}", this.inbound);
		log.info("# mftp destination: {}", this.destination);
		
		FileUtil.createDir(this.destination);
	}
	
}
