package com.honda.olympus.ms.transferfile.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;



@TestMethodOrder(OrderAnnotation.class)
public class MftpClientTest
{
	
	static final String HOST = "localhost";
	static final int PORT = 2222;
	static final String USER = "mft_win_hdm_qa";
	static final String PWD = "Flag@458";
	static final String INBOUND = "/ms.transferfile/inbound/";
	static final String DESTINATION = "%s/ms.transferfile/destination/";
	
	static final String FILE_NAME = "processFileExample.txt";
	static final String NEW_FILE_NAME = "ahm20200401153355.txt";
	static final String FILE_CONTENT = "CHANGEORD1000000SSORG_TYPECONFIG_ID000000009...";
	
	static FakeFtpServer fakeFtpServer;
	static MftpClient mftpClient;
	
	
	@BeforeAll
	static void beforeAll() {
		configureFtpServer();
		configureMftpClient();
		
	}
	
	
	@AfterAll
	static void afterAll() throws IOException {
		// mftpClient.close();
		// fakeFtpServer.stop();
	}
	
	
	static void configureFtpServer() {
		fakeFtpServer = new FakeFtpServer();
		fakeFtpServer.addUserAccount(new UserAccount(USER, PWD, INBOUND));
		
		FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add( new DirectoryEntry(INBOUND) );
        fileSystem.add( new FileEntry(INBOUND + FILE_NAME, FILE_CONTENT) );
        
        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.setServerControlPort(PORT);
        fakeFtpServer.start();
	}
	
	
	static void configureMftpClient() {
		MftpClient.Config config = new MftpClient.Config(HOST, HOST, INBOUND, DESTINATION);
		config.setPort(PORT);
		config.setUser(USER);
		config.setPass(PWD);
		config.setInbound(INBOUND);
		mftpClient = new MftpClient(config);
	}
	
	
	
	@Test
	@Order(1)
	void shouldConnectToFtpServer() {
		assertTrue( mftpClient.open() );
	}
	
	
	
	@Test
	@Order(2)
	void fileShouldExist() {
		assertTrue( mftpClient.fileExists(FILE_NAME) );
	}
	
	@Test
	@Order(3)
	void fileShouldNotExists() {
		assertFalse( mftpClient.fileExists("file1.txt") );
	}
	
	
	
	@Test
	@Order(4)
	void shouldDownloadFile() {
		assertTrue( mftpClient.downloadFile(FILE_NAME, NEW_FILE_NAME) );
	}
	
	@Test
	@Order(5)
	void shouldNotDownloadFile() {
		assertFalse( mftpClient.downloadFile("file1.txt", "newFile1.txt") );
	}
	
	
	
	@Test
	@Order(6)
	void shouldDeleteFile() {
		assertTrue( mftpClient.deleteFile(FILE_NAME) );
	}
	
	@Test
	@Order(7)
	void shouldNotDeleteFile() {
		assertFalse( mftpClient.deleteFile("file1.txt") );
	}
	
	
	
	@Test
	@Order(8)
	void shouldDisconnectFromFtpServer() {
		assertTrue( mftpClient.close() );
	}
	
	@Test
	@Order(9)
	void shouldNotConnectToFtpServer() {
		fakeFtpServer.stop();
		assertFalse( mftpClient.open() );
	}
	
	@Test
	@Order(10)
	void shouldNotDisconnectFromFtpServer() {
		assertFalse( mftpClient.close() );
	}
	
}
