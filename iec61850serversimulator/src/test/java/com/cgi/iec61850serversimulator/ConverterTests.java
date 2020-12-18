package com.cgi.iec61850serversimulator;

import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ConverterTests {
	
	private LocalDateTime timeConverter(String currentTime) {
		// Pattern from bda.getValueString()
		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy");
		
		LocalDateTime convertedTime = LocalDateTime.parse(currentTime, timeFormat);
		
		return convertedTime;}
	
	
	/*@Test
	void timestampConvertingTest(){
		String currentTime = "Thu Jan 01 01:00:00 CET 1970";
		LocalDateTime wantedTime = LocalDateTime.of(1970, Month.JANUARY, 01, 01, 00, 00);
		
		
		LocalDateTime convertedTime = timeConverter(currentTime);
		assertTrue(convertedTime instanceof LocalDateTime);
		assertEquals(convertedTime, wantedTime);
		System.out.println(convertedTime);
	}*/
	
	@Test
	void bytesEpochConverterTest() {
		// 8 byte timestamp is Epoch second timestamp, originating from BdaTimestamp
		byte[] bytesEpochTime = new byte[] {0, 0, 0, 0, 0, 0, 0, 0};
		ByteBuffer wrappedTime = ByteBuffer.wrap(bytesEpochTime);
		long longTime = wrappedTime.getLong();	
		LocalDateTime wantedTime = LocalDateTime.of(1970, Month.JANUARY, 01, 01, 00, 00);
		LocalDateTime epochTimeCET = LocalDateTime.ofEpochSecond(longTime, 0, ZoneOffset.ofHours(1));
		assertEquals(epochTimeCET, wantedTime);
		
	}
}