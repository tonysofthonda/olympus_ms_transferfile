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
	@Value("${mftp.user}") private String user;
	@Value("${mftp.pass}") private String pass;
	private String inbound;
	private String destination;
	
	
	public MftpConfig(
		@Value("${mftp.host}") String host, 
		@Value("${mftp.port}") int port, 
		@Value("${mftp.inbound}") String inbound,  
		@Value("${mftp.destination}") String destination) 
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
