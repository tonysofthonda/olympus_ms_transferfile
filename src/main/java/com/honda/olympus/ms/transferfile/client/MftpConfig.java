package com.honda.olympus.ms.transferfile.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.honda.olympus.ms.transferfile.util.FileUtil;
import com.honda.olympus.ms.transferfile.util.NetUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Data
@Slf4j
@Component
public class MftpConfig 
{
	
	private static final String BASE_DIR = System.getProperty("java.io.tmpdir");
	
	
	private String host;
	@Value("${port}") private int port;
	@Value("${user}") private String user;
	@Value("${pass}") private String pass;
	private String inbound;
	private String destination;
	
	
	public MftpConfig(
		@Value("${host}") String internalHost, 
		@Value("${mftp.ext.host}") String externalHost, 
		@Value("${inbound}") String inbound, 
		@Value("${destination}") String destination) 
	{
		this.host = NetUtil.isSiteLocalAddress() ? internalHost : externalHost;
		this.inbound = FileUtil.fixSlashes( String.format(inbound, BASE_DIR) );
		this.destination = FileUtil.fixSlashes( String.format(destination, BASE_DIR) );
		
		log.info("# mftp host: {}", this.host);
		log.info("# mftp inbound: {}", this.inbound);
		log.info("# mftp destination: {}", this.destination);
		
		FileUtil.createDir(this.destination);
	}
	
}
