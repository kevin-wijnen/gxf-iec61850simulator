package com.cgi.iec61850serversimulator;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import com.beanit.openiec61850.SclParseException;
import com.beanit.openiec61850.SclParser;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServerSap;

@SpringBootTest
class ServerSimulatorTests {
	
	private static final String modelFileParam = "/home/dev/iec61850bean/bin/SWDevice-010805.icd";
	public static ServerModel serverModel;
	public static ServerSap serverSap;
	// Lijst toevoegen voor de ServerSap 
	
	@Test
	void SclParseTest() throws SclParseException{
		
		System.out.print(SclParser.parse(modelFileParam));
	}
	
	@Test
	void CopyingModelToSapTest() throws InterruptedException, IOException {
		List<ServerModel> serverModels = null;
		try {
			serverModels = SclParser.parse(modelFileParam);
		} catch (SclParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();	
		}
		serverSap = new ServerSap(103, 0, null, serverModels.get(0), null);
		serverModel = serverSap.getModelCopy();
	}
	
	//TODO: Still necessary to put serverSap listening event in here to test server boot up

}

