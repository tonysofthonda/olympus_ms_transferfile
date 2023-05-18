package com.honda.olympus.ms.transferfile.util;

import java.net.InetAddress;
import java.net.UnknownHostException;


public final class NetUtil 
{
	
	private static final String LOCAL_URL = "http://localhost";
	
	private NetUtil() { }
	
	public static boolean isSiteLocalAddress() {
		try { 
			return InetAddress.getLocalHost().isSiteLocalAddress(); 
		} 
		catch (UnknownHostException e) { 
			return false;
		}
	}
	
	public static String getLocalUrl(Integer port) {
		return new StringBuilder()
			.append(LOCAL_URL).append(":").append(port).append("/")
			.toString(); 
	}
	
}
