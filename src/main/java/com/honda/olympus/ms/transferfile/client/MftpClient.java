package com.honda.olympus.ms.transferfile.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import com.honda.olympus.ms.transferfile.util.FileUtil;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MftpClient 
{ 
	
	private MftpConfig config;
	private String fileName;
	private String input;
	private String newFileName;  
	private String output;
	
	private Channel channel = null;
	private ChannelSftp channelSftp = null;
	private InputStream is;
	 public static final int DEFAULT_BUFFER_SIZE = 8192;

	
	
	public MftpClient(MftpConfig config, String fileName, String newFileName) {
		this.config = config;
		this.fileName = fileName;
		this.input = FileUtil.fixSlashes( FileUtil.concat(config.getInbound(), fileName) );
		this.newFileName = newFileName;
		this.output = FileUtil.fixSlashes( FileUtil.concat(config.getDestination(), newFileName) );
	}
	
	
	// setter methods added for testing purposes only
	
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
			String pass = config.getPass();
			JSch jsch = new JSch();
			Session session = jsch.getSession(config.getUser(),config.getHost(), config.getPort());
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(pass);
			session.connect();
			log.debug("Connection established.");
			log.debug("Creating SFTP Channel.");

			channel = session.openChannel("sftp");
			channel.connect();

			return true;
		} catch (JSchException e4) {
			log.error("### Error found while connecting to ftp server", e4);
			return false;

		}
    }
	
	
	public boolean fileExists() {
		try {
			this.channelSftp = (ChannelSftp) channel;
			this.channelSftp.get(this.input,this.output);
		
			return true;
		}
		catch (SftpException ioe) {
			log.error("### Can't find remote file '{}'", fileName);
			return false;
		}
	}

	public boolean isFileEmtpy() {
	
		try {
		
			Path path = Paths.get(this.output);
			
			long bytes = Files.size(path);
			if(bytes <=0) {
				log.error("### Remote file '{}' is empty (will be deleted !)", fileName);
				Files.delete(path);
				return true;
		
			}
			
		} catch (IOException e) {
			log.error("## Exception due to: {} ",e.getLocalizedMessage());
			return false;
		}
	
		return false;
	}
	

	public boolean downloadFile() {
		try {
			
			Path path = Paths.get(this.output);
			
			if(path == null) {
				log.error("### Error found while downloading remote file '{}'", fileName);
				return false;
			}
			
			
		    return true;
		}
		catch (Exception ioe) {
			log.error("### Error found while downloading remote file '{}'", fileName, ioe);
			FileUtil.removeFile(newFileName);
			return false;
		}
	}
	
	public boolean deleteFile() {
		try {
			
			this.channelSftp.rm(this.input);
			
			return true;
		}
		catch (SftpException ioe) {
			log.error("### Error found while deleting remote file '{}'", fileName, ioe);
			return false;
		}
	}
	
	
	public boolean close() {
		try {
			this.channel.disconnect();
			return true;
		}
		catch (Exception ioe) {
			log.error("### Error found while closing connection to ftp server", ioe);
			return false;
		}
    }
	
}
