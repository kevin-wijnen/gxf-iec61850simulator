package com.cgi.iec61850serversimulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.beanit.openiec61850.SclParseException;
import com.beanit.openiec61850.SclParser;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServerSap;
import com.beanit.openiec61850.internal.cli.Action;
import com.beanit.openiec61850.internal.cli.ActionProcessor;
import com.beanit.openiec61850.internal.cli.CliParameter;
import com.beanit.openiec61850.internal.cli.CliParameterBuilder;
import com.beanit.openiec61850.internal.cli.CliParseException;
import com.beanit.openiec61850.internal.cli.CliParser;
import com.beanit.openiec61850.internal.cli.IntCliParameter;
import com.beanit.openiec61850.internal.cli.StringCliParameter;

@SpringBootApplication
public class ServerSimulator {
	
	private static final Logger logger = LoggerFactory.getLogger(ServerSimulator.class);
	//private static final Logger logger = LogManager.getLogger(Iec61850serversimulatorApplication.class);
	
	private static final String PRINT_SERVER_MODEL_KEY = "p";
	private static final String PRINT_SERVER_MODEL_KEY_DESCRIPTION = "print server's model";
	private static final String DEVICE_SHOW_MODEL = "d";
	private static final String DEVICE_SHOW_MODEL_DESCRIPTION = "print device object";
	
	private static final IntCliParameter portParam =
	    new CliParameterBuilder("-p")
	        .setDescription(
	            "The port to listen on. On unix based systems you need root privilages for ports < 1000. Default: 102")
	        .buildIntParameter("port", 10102);
	
	private static final StringCliParameter modelFileParam =
	      new CliParameterBuilder("-m")
	          .setDescription("The SCL file that contains the server's information model.")
	          .setMandatory()
	          .buildStringParameter("model-file");
	
	public static ServerModel serverModel;
	public static ServerSap serverSap = null;
	
	public static void main(String[] args) throws IOException{
		
		SpringApplication.run(ServerSimulator.class, args);
		logger.info("Applicatie starten...");
		
	    List<CliParameter> cliParameters = new ArrayList<>();
	    cliParameters.add(modelFileParam);
	    cliParameters.add(portParam);

	    CliParser cliParser =
	    		new CliParser("iec61850bean-console-server", "An IEC 61850 MMS console server.");
	    cliParser.addParameters(cliParameters);
	    
	    try {
	        cliParser.parseArguments(args);
	      } catch (CliParseException e1) {
	        System.err.println("Error parsing command line parameters: " + e1.getMessage());
	        System.out.println(cliParser.getUsageString());
	        System.exit(1);
	      }
	    
	    List<ServerModel> serverModels = null;
	    try {
	      serverModels = SclParser.parse(modelFileParam.getValue());
	    } catch (SclParseException e) {
	      System.out.println("Error parsing SCL/ICD file: " + e.getMessage());
	      return;
	    }
	    logger.info("ServerSap aanmaken...");
	    serverSap = new ServerSap(102, 0, null, serverModels.get(0), null);
	    serverSap.setPort(portParam.getValue());
	    logger.info("ServerSap aangemaakt met als poort: " + portParam.getValue());
	    Runtime.getRuntime()
	        .addShutdownHook(
	            new Thread() {
	              @Override
	              public void run() {
	                if (serverSap != null) {
	                  serverSap.stop();
	                }
	                System.out.println("Server was stopped.");
	              }
	            });
	    logger.info("Model copy gestart");
	    serverModel = serverSap.getModelCopy();
	    logger.info("Model copy done!");
	    
	    // Device intialization by copying from serverModel
	    Device device = new Device();
	    device.initalizeDevice(serverModel);
	    //Functie toevoegen om details van serverModel naar Device te doen
	    	
	    //ActionExecutor actionExecutor = new ActionExecutor(PRINT_SERVER_MODEL_KEY, serverSap, serverModel);
	    
	    logger.info("SERVER START LISTENING");
	    //serverSap.startListening(new EventListener());
	    serverSap.startListening(new LightMeasurementDeviceListener(device));
	    
		final ActionProcessor actionProcessor = new ActionProcessor(new ActionExecutor(serverSap, serverModel, device));
	    actionProcessor.addAction(
	        new Action(PRINT_SERVER_MODEL_KEY, PRINT_SERVER_MODEL_KEY_DESCRIPTION));
	    actionProcessor.addAction(new Action(DEVICE_SHOW_MODEL, DEVICE_SHOW_MODEL_DESCRIPTION));
	    //actionProcessor.addAction(new Action(WRITE_VALUE_KEY, WRITE_VALUE_KEY_DESCRIPTION));

	    actionProcessor.start();
		
		/*logger.info("Applicatie starten...");
		System.out.println("Hallo, wereld!");
		logger.info("Applicatie gesloten.");*/
	    
		
	}

}
