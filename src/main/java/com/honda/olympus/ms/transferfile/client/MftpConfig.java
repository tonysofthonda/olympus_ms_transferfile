package com.honda.olympus.ms.transferfile.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.honda.olympus.ms.transferfile.util.FileUtil;
import com.honda.olympus.ms.transferfile.util.NetUtil;

import lombok.Data;


@Data
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
		this.inbound = FileUtil.withFrontSlash( String.format(inbound, BASE_DIR) );
		this.destination = FileUtil.withFrontSlash( String.format(destination, BASE_DIR) );
	}
	
	
	public String host() { return host; }
	public int port() { return port; }
	public String user() { return user; }
	public String pass() { return pass; }
	public String inbound() { return inbound; }
	public String destination() { return destination; }
	
}
