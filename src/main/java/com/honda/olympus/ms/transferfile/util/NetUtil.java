package com.honda.olympus.ms.transferfile.util;

import java.net.InetAddress;
import java.net.UnknownHostException;


public final class NetUtil 
{
	
	private static final String HTTP = "http";
	private static final String HTTPS = "https";
	private static final String BASE_SEP = "://";
	private static final String LOCALHOST = "localhost";
	private static final String SLASH = "/";
	private static final String COLON = ":";
	
	
	private NetUtil() { }
	
	
	public static boolean isSiteLocalAddress() {
		try { 
			return InetAddress.getLocalHost().isSiteLocalAddress(); 
		} 
		catch (UnknownHostException e) { 
			return false;
		}
	}
	
	
	public static String buildLocalBaseUrl(boolean isSecure, Integer port) {
		return new StringBuilder()
			.append(isSecure ? HTTPS : HTTP)
			.append(BASE_SEP)
			.append(LOCALHOST)
			.append(COLON + port)
			.append(SLASH)
			.toString();
	}
	
	
	public static String concat(String baseUrl, String path) {
		return baseUrl + SLASH + path;
	}
	
	
	public static String fixSlashes(String url) {
		int index = url.indexOf(BASE_SEP) + BASE_SEP.length();
		return url.substring(0, index) + url.substring(index).replaceAll("/{2,}", "/");
	}
	
}
