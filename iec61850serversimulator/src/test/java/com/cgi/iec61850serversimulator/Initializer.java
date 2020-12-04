package com.cgi.iec61850serversimulator;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.beanit.openiec61850.BasicDataAttribute;

@SpringBootTest

class Initializer {
	// Test for using Switch instead of many if-else's
	String[] bdaTestNames = new String[] {"boolean1", "boolean2", "int", "testString", "thisIsInvalid"};
	String[] bdaTestValues = new String[] {"true", "false", "0", "helloWorld"};
	
	@Test
	void SwitchCaseTest(){

		for (String bda : bdaTestNames){
			String bdaTestName = bda;
			
			switch(bdaTestName) {
			case "boolean1":
				System.out.println(bdaTestName + " found!");
				System.out.println(bdaTestValues[0]);
				assertEquals("boolean1", bdaTestName);
				break;
				
			case "boolean2":
				System.out.println(bdaTestName + " found!");
				System.out.println(bdaTestValues[1]);
				assertEquals("boolean2", bdaTestName);
				break;
				
			case "int":
				System.out.println(bdaTestName + " found!");
				System.out.println(bdaTestValues[2]);
				assertEquals("int", bdaTestName);
				break;
				
			case "testString":
				System.out.println(bdaTestName + " found!");
				System.out.println(bdaTestValues[3]);
				assertEquals("testString", bdaTestName);
				break;
				
			default:
				System.out.println(bdaTestName + " not found.");
				assertEquals("thisIsInvalid", bdaTestName);
				break;
			}
			
		}
	}
	
	
}
