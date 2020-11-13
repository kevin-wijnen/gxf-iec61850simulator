package com.cgi.iec61850serversimulator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.beanit.openiec61850.internal.cli.ActionException;
import com.beanit.openiec61850.internal.cli.ActionListener;

@SpringBootTest
class Iec61850serversimulatorApplicationTests {

	/*void contextLoads() {
	}*/
	ServerSimulator simulator = new ServerSimulator();
	
	@Test
	void testServerPrint() throws ActionException{
		//ActionExecutor test = new ActionExecutor();
		//String message = test.actionCalled("p");
	}
}