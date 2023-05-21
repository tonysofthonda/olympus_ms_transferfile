package com.honda.olympus.ms.transferfile.client;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.honda.olympus.ms.transferfile.util.NetUtil;
import com.honda.olympus.ms.transferfile.util.FileUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MftpClient 
{ 
	
	private FTPClient ftp;
	private Config config;
	
	
	@Autowired
	public MftpClient(Config config) {
		this.config = config;
		log.info("# mftp host: {}", config.host);
		log.info("# mftp inbound: {}", config.inbound);
		log.info("# mftp destination: {}", config.destination);
		FileUtil.createDir(config.destination);
	}
	
	
	public boolean open() {
		try {
			ftp = new FTPClient();
	        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
	        
	        ftp.connect(config.host, config.port);
	        ftp.login(config.user, config.pass);
	        
	        int reply = ftp.getReplyCode();
	        if (!FTPReply.isPositiveCompletion(reply)) 
	        {
	        	ftp.disconnect();
	        	log.error("### Can't connect to the ftp server");
	        	return false;
	        }
	        return true;
		}
		catch (IOException ioe) {
			log.error("### Error found while connecting to ftp server", ioe);
			return false;
		}
    }
	
	
	public boolean fileExists(String fileName) {
		try {
			String input = FileUtil.withFrontSlash( FileUtil.concat(config.inbound, fileName) );
			FTPFile[] list = ftp.listFiles(input);
			
			if (list.length == 0) {
				log.error("### Can't find remote file '{}'", fileName);
				return false;
			}
			return true;
		}
		catch (IOException ioe) {
			log.error("### Error found while searching remote file '{}'", fileName, ioe);
			return false;
		}
	}
	
	
	public boolean downloadFile(String fileName, String newFileName) 
	{
		String input = FileUtil.withFrontSlash( FileUtil.concat(config.inbound, fileName) );
		String output = FileUtil.withFrontSlash( FileUtil.concat(config.destination, newFileName) );
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(output, false);
		    if (!ftp.retrieveFile(input, fos)) 
		    {
		    	log.error("### Can't download remote file '{}'", fileName);
		    	fos.close();
		    	FileUtil.removeFile(output);
				return false;
		    }
			fos.close();
		    return true;
		}
		catch (IOException ioe) {
			log.error("### Error found while downloading remote file '{}'", fileName, ioe);
			if (fos != null) {
				try { fos.close(); } catch (IOException e) { }
			}
			
			FileUtil.removeFile(newFileName);
			return false;
		}
	}
	
	
	public boolean deleteFile(String fileName) {
		try {
			String input = FileUtil.withFrontSlash( FileUtil.concat(config.inbound, fileName) );
			
			if (!ftp.deleteFile(input)) {
				log.error("### Can't delete remote file '{}'", fileName);
				return false;
			}
			return true;
		}
		catch (IOException ioe) {
			log.error("### Error found while deleting remote file '{}'", fileName, ioe);
			return false;
		}
	}
	
	
	public boolean close() {
		try {
			ftp.logout();
			ftp.disconnect();
			return true;
		}
		catch (IOException ioe) {
			log.error("### Error found while closing connection to ftp server", ioe);
			return false;
		}
    }
	
	
	
	@Data
	@Component
	@Scope(scopeName = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public static class Config 
	{
		private static final String BASE_DIR = System.getProperty("java.io.tmpdir");
		
		private String host;
		@Value("${port}") private int port;
		@Value("${user}") private String user;
		@Value("${pass}") private String pass;
		private String inbound;
		private String destination;
		
		public Config(
			@Value("${host}") String internalHost, 
			@Value("${mftp.ext.host}") String externalHost, 
			@Value("${inbound}") String inbound, 
			@Value("${destination}") String destination) 
		{
			this.host = NetUtil.isSiteLocalAddress() ? internalHost : externalHost;
			this.inbound = FileUtil.withFrontSlash( String.format(inbound, BASE_DIR) );
			this.destination = FileUtil.withFrontSlash( String.format(destination, BASE_DIR) );
		}
	}//
	
}
