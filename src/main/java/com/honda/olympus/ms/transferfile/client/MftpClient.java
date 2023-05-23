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
		this.input = FileUtil.fixSlashes( FileUtil.concat(config.getInbound(), fileName) );
		
		this.newFileName = newFileName;
		this.output = FileUtil.fixSlashes( FileUtil.concat(config.getDestination(), newFileName) );
	}
	
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
		this.input = FileUtil.fixSlashes( FileUtil.concat(config.getInbound(), fileName) );
	}
	
	
	public void setNewFileName(String newFileName) {
		this.newFileName = newFileName;
		this.output = FileUtil.fixSlashes( FileUtil.concat(config.getDestination(), newFileName) );
	}
	
	
	
	public boolean open() {
		try {
			ftp = new FTPClient();
	        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
	        
	        ftp.connect(config.getHost(), config.getPort());
	        ftp.login(config.getUser(), config.getPass());
	        
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
			ftp.enterLocalPassiveMode();
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
		log.error("### Remote file '{}' is empty (will be deleted !)", fileName);
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
