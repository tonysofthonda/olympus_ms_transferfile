package com.honda.olympus.ms.transferfile.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
	static final String EMPTY_FILE = "empty.txt";
	static final String EMPTY_CONTENT = "";
	
	static FakeFtpServer fakeFtpServer;
	static MftpConfig mftpConfig;
	static MftpClient mftpClient;
	
	
	
	@BeforeAll
	static void beforeAll() {
		loadFakeFtpServer();
		loadMftpConfig();
		loadMftpClient();
		
	}
	
	static void loadFakeFtpServer() {
		fakeFtpServer = new FakeFtpServer();
		fakeFtpServer.addUserAccount(new UserAccount(USER, PWD, INBOUND));
		
		FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add( new DirectoryEntry(INBOUND) );
        fileSystem.add( new FileEntry(INBOUND + FILE_NAME, FILE_CONTENT) );
        fileSystem.add( new FileEntry(INBOUND + EMPTY_FILE, EMPTY_CONTENT) );
        
        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.setServerControlPort(PORT);
        fakeFtpServer.start();
	}
	
	static void loadMftpConfig() {
		mftpConfig = new MftpConfig(HOST, HOST, INBOUND, DESTINATION);
		mftpConfig.setPort(PORT);
		mftpConfig.setUser(USER);
		mftpConfig.setPass(PWD);
		mftpConfig.setInbound(INBOUND);
	}
	
	static void loadMftpClient() {
		mftpClient = new MftpClient(mftpConfig, FILE_NAME, NEW_FILE_NAME);
	}
	
	
	
	@Test
	@Order(1)
	void shouldConnectToFtpServer() {
		assertTrue( mftpClient.open() );
	}
	
	
	
	@Test
	@Order(2)
	void fileShouldExist() {
		assertTrue( mftpClient.fileExists() );
	}
	
	@Test
	@Order(3)
	void fileShouldNotExists() {
		mftpClient.setFileName("file1.txt");
		assertFalse( mftpClient.fileExists() );
		mftpClient.setFileName(FILE_NAME);
	}
	
	
	
	@Test
	@Order(4)
	void fileShouldNotBeEmpty() {
		mftpClient.fileExists();
		assertFalse( mftpClient.isFileEmtpy() );
	}
	
	@Test
	@Order(5)
	void fileShouldBeEmpty() {
		mftpClient.setFileName(EMPTY_FILE);
		mftpClient.fileExists();
		assertTrue( mftpClient.isFileEmtpy()  );
		mftpClient.setFileName(FILE_NAME);
		mftpClient.fileExists();
	}
	
	
	
	@Test
	@Order(6)
	void shouldDownloadFile() {
		assertTrue( mftpClient.downloadFile() );
	}
	
	@Test
	@Order(7)
	void shouldNotDownloadFile() {
		mftpClient.setFileName("file1.txt");
		mftpClient.setNewFileName("newFile1.txt");
		assertFalse( mftpClient.downloadFile() );
		mftpClient.setFileName(FILE_NAME);
		mftpClient.setNewFileName(NEW_FILE_NAME);
	}
	
	
	
	@Test
	@Order(8)
	void shouldDeleteFile() {
		assertTrue( mftpClient.deleteFile() );
	}
	
	@Test
	@Order(9)
	void shouldNotDeleteFile() {
		mftpClient.setFileName("file1.txt");
		assertFalse( mftpClient.deleteFile() );
		mftpClient.setFileName(FILE_NAME);
	}
	
	
	
	@Test
	@Order(10)
	void shouldDisconnectFromFtpServer() {
		assertTrue( mftpClient.close() );
	}
	
	@Test
	@Order(11)
	void shouldNotConnectToFtpServer() {
		fakeFtpServer.stop();
		assertFalse( mftpClient.open() );
	}
	
	@Test
	@Order(12)
	void shouldNotDisconnectFromFtpServer() {
		assertFalse( mftpClient.close() );
	}
	
}
