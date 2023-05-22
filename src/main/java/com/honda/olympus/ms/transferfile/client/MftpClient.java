package com.honda.olympus.ms.transferfile.client;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.honda.olympus.ms.transferfile.util.FileUtil;

import lombok.extern.slf4j.Slf4j;



@Slf4j
public class MftpClient 
{ 
	
	private FTPClient ftp;
	private MftpConfig config;
	
	private String fileName;
	private String input;
	private String newFileName;  
	private String output;
	
	private FTPFile remoteFile;
	
	
	
	public MftpClient(MftpConfig config, String fileName, String newFileName) {
		this.config = config;
		
		this.fileName = fileName;
		this.input = FileUtil.withFrontSlash( FileUtil.concat(config.inbound(), fileName) );
		
		this.newFileName = newFileName;
		this.output = FileUtil.withFrontSlash( FileUtil.concat(config.destination(), newFileName) );
		
		log.info("# mftp host: {}", config.host());
		log.info("# mftp inbound: {}", config.inbound());
		log.info("# mftp destination: {}", config.destination());
		
		FileUtil.createDir(config.destination());
	}
	
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
		this.input = FileUtil.withFrontSlash( FileUtil.concat(config.inbound(), fileName) );
	}
	
	
	public void setNewFileName(String newFileName) {
		this.newFileName = newFileName;
		this.output = FileUtil.withFrontSlash( FileUtil.concat(config.destination(), newFileName) );
	}
	
	
	
	public boolean open() {
		try {
			ftp = new FTPClient();
	        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
	        
	        ftp.connect(config.host(), config.port());
	        ftp.login(config.user(), config.pass());
	        
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
	
	
	public boolean fileExists() {
		try {
			FTPFile[] list = ftp.listFiles(input);
			
			if (list.length == 0) {
				log.error("### Can't find remote file '{}'", fileName);
				return false;
			}
			
			remoteFile = list[0];  // keep file info used by isFileEmpty()
			return true;
		}
		catch (IOException ioe) {
			log.error("### Error found while searching remote file '{}'", fileName, ioe);
			return false;
		}
	}
	
	public boolean isFileEmtpy() {  // depends on previous execution of fileExists()
		return remoteFile.getSize() == 0;
	}
	
	
	public boolean downloadFile() {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(output, false);
		    if (!ftp.retrieveFile(input, fos)) 
		    {
		    	log.error("### Can't download remote file '{}'", fileName);
		    	fos.close();
		    	FileUtil.removeFile(output);  // remove leftover empty file
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
	
	
	public boolean deleteFile() {
		try {
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
	
}
