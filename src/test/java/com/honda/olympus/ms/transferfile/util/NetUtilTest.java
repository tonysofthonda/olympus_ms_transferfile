package com.honda.olympus.ms.transferfile.util;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;


@TestMethodOrder(OrderAnnotation.class)
public class NetUtilTest 
{	
	
	@Test
	@Order(1)
	void shouldConcatBaseUrlWithPath() {	
		String baseUrl = "http://localhost:8080/";
		String path = "/olympus/logevent/v1/event";
		
		String expected = "http://localhost:8080///olympus/logevent/v1/event";
		String actual = NetUtil.concat(baseUrl, path);
		
		assertEquals(expected, actual);
	}
	
	
	@Test
	@Order(2)
	void shouldFixSlashes() {
		String url = "http://localhost:8080///olympus/logevent/v1/event";
		
		String expected = "http://localhost:8080/olympus/logevent/v1/event";
		String actual = NetUtil.fixSlashes(url);
		
		assertEquals(expected, actual);
	}
	
}
