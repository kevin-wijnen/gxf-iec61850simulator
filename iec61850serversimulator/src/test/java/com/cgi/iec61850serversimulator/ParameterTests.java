package com.cgi.iec61850serversimulator;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.beanit.openiec61850.internal.cli.CliParameter;
import com.beanit.openiec61850.internal.cli.CliParameterBuilder;
import com.beanit.openiec61850.internal.cli.CliParser;
import com.beanit.openiec61850.internal.cli.IntCliParameter;
import com.beanit.openiec61850.internal.cli.StringCliParameter;

@SpringBootTest
class ParameterTests {
	
	@Test
	void Test(){
		
		final IntCliParameter portParam =
			    new CliParameterBuilder("-p")
			        .setDescription(
			            "The port to listen on. On unix based systems you need root privilages for ports < 1000. Default: 102")
			        .buildIntParameter("port", 10000);
			
		final StringCliParameter modelFileParam =
			    new CliParameterBuilder("-m")
			        .setDescription("The SCL file that contains the server's information model.")
			        .setMandatory()
			        .buildStringParameter("model-file");
		
	    List<CliParameter> cliParameters = new ArrayList<>();
	    cliParameters.add(modelFileParam);
	    cliParameters.add(portParam);
	    
	    CliParser cliParser =
	    		new CliParser("iec61850bean-console-server", "An IEC 61850 MMS console server.");
	    cliParser.addParameters(cliParameters);
	    
	    
	    // TODO: Find way to parse arguments.
//	    
//	    try {
//	        cliParser.parseArguments(cliParameters);
//	      } catch (CliParseException e1) {
//	        System.err.println("Error parsing command line parameters: " + e1.getMessage());
//	        System.out.println(cliParser.getUsageString());
//	        System.exit(1);
//	      }	
//		
			
	}
}
