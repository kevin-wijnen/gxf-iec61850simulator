package com.cgi.iec61850serversimulator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.beanit.openiec61850.internal.cli.ActionException;
import com.beanit.openiec61850.internal.cli.ActionListener;
import com.cgi.iec61850serversimulator.Iec61850serversimulatorApplication.ActionExecutor;

@SpringBootTest
class Iec61850serversimulatorApplicationTests {

	/*void contextLoads() {
	}*/
	Iec61850serversimulatorApplication simulator = new Iec61850serversimulatorApplication();
	
	@Test
	void testServerPrint() throws ActionException{
		ActionExecutor test = new ActionExecutor();
		//String message = test.actionCalled("p");
	}
}