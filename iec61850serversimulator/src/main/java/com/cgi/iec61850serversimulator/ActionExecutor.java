package com.cgi.iec61850serversimulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.internal.cli.ActionException;
import com.beanit.openiec61850.internal.cli.ActionListener;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServerSap;

public class ActionExecutor implements ActionListener {
	
	private static final Logger logger = LoggerFactory.getLogger(ActionExecutor.class);
	private static final String PRINT_SERVER_MODEL_KEY = "p";
	public ServerSap serverSap = null;
	public ServerModel serverModel = null;
	//private static final String PRINT_SERVER_MODEL_KEY = "p";
	//private static final String PRINT_SERVER_MODEL_KEY_DESCRIPTION = "print server's model";
	
    public ActionExecutor(ServerSap serverSap, ServerModel serverModel) {
		this.serverSap = serverSap;
		this.serverModel = serverModel;
		
	}
    
	@Override
    public void actionCalled(String actionKey) throws ActionException {
      try {
        switch (actionKey) {
          case PRINT_SERVER_MODEL_KEY:
            System.out.println("** Printing model.");
            logger.info("Server model:" + serverModel);

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