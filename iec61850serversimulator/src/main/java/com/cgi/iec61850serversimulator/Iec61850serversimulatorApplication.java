package com.cgi.iec61850serversimulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.qos.logback.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.SclParseException;
import com.beanit.openiec61850.SclParser;
import com.beanit.openiec61850.ServerEventListener;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServerSap;
import com.beanit.openiec61850.ServiceError;
import com.beanit.openiec61850.internal.cli.Action;
import com.beanit.openiec61850.internal.cli.ActionException;
import com.beanit.openiec61850.internal.cli.ActionListener;
import com.beanit.openiec61850.internal.cli.ActionProcessor;
import com.beanit.openiec61850.internal.cli.CliParameter;
import com.beanit.openiec61850.internal.cli.CliParameterBuilder;
import com.beanit.openiec61850.internal.cli.CliParseException;
import com.beanit.openiec61850.internal.cli.CliParser;
import com.beanit.openiec61850.internal.cli.IntCliParameter;
import com.beanit.openiec61850.internal.cli.StringCliParameter;

// Code integrated from the OpenIEC61850 application from BeanIt, licensed under Apache 2.0.

@SpringBootApplication
public class Iec61850serversimulatorApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(Iec61850serversimulatorApplication.class);
	//private static final Logger logger = LogManager.getLogger(Iec61850serversimulatorApplication.class);
	
	private static final String PRINT_SERVER_MODEL_KEY = "p";
	private static final String PRINT_SERVER_MODEL_KEY_DESCRIPTION = "print server's model";
	
	private static final IntCliParameter portParam =
	    new CliParameterBuilder("-p")
	        .setDescription(
	            "The port to listen on. On unix based systems you need root privilages for ports < 1000. Default: 102")
	        .buildIntParameter("port", 10000);
	
	private static final StringCliParameter modelFileParam =
	      new CliParameterBuilder("-m")
	          .setDescription("The SCL file that contains the server's information model.")
	          .setMandatory()
	          .buildStringParameter("model-file");
	
    public static class ActionExecutor implements ActionListener {

        @Override
        public void actionCalled(String actionKey) throws ActionException {
          try {
            switch (actionKey) {
              case PRINT_SERVER_MODEL_KEY:
                System.out.println("** Printing model.");
                //logger.info("Server model:" + serverModel);
                //System.out.println(serverModel);

                break;
            }
          } catch (Exception e) {
            throw new ActionException(e);
          }
        }

		@Override
		public void quit() {
			logger.info("Shutting down application...");
			serverSap.stop();
			return;
			
		}
    }
	
	private static final ActionProcessor actionProcessor = new ActionProcessor(new ActionExecutor());
	private static ServerModel serverModel;
	private static ServerSap serverSap = null;
    static class EventListener implements ServerEventListener {

        @Override
        public void serverStoppedListening(ServerSap serverSap) {
          System.out.println("The SAP stopped listening");
        }

        @Override
        public List<ServiceError> write(List<BasicDataAttribute> bdas) {
          for (BasicDataAttribute bda : bdas) {
            System.out.println("got a write request: " + bda);
          }
          return null;
        }
      }
	
	public static void main(String[] args) throws IOException{
		
		SpringApplication.run(Iec61850serversimulatorApplication.class, args);
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
	    //logger.info("ServerSap aangemaakt met als poort: " + portParam.getValue());
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
	    //logger.info("Model copy gestart");
	    serverModel = serverSap.getModelCopy();
	    //logger.info("Model copy done!");
	    
	    //logger.info("SERVER START LISTENING");
	    serverSap.startListening(new EventListener());

	    actionProcessor.addAction(
	        new Action(PRINT_SERVER_MODEL_KEY, PRINT_SERVER_MODEL_KEY_DESCRIPTION));
	    //actionProcessor.addAction(new Action(WRITE_VALUE_KEY, WRITE_VALUE_KEY_DESCRIPTION));

	    actionProcessor.start();
		
		/*logger.info("Applicatie starten...");
		System.out.println("Hallo, wereld!");
		logger.info("Applicatie gesloten.");*/
	    
		
	}

}
