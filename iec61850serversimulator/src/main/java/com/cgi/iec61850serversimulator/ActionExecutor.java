package com.cgi.iec61850serversimulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServerSap;
import com.beanit.openiec61850.internal.cli.ActionException;
import com.beanit.openiec61850.internal.cli.ActionListener;

/**
 * Class which takes certain button inputs for debug features during runtime.
 */

public class ActionExecutor implements ActionListener {

	private static final Logger logger = LoggerFactory.getLogger(ServerSimulator.class);
	private static final String PRINT_SERVER_MODEL_KEY = "p";
	private static final String DEVICE_SHOW_MODEL = "d";
	public ServerSap serverSap = null;
	public ServerModel serverModel = null;
	public Device device = null;

	public ActionExecutor(final ServerSap serverSap, final ServerModel serverModel, final Device device) {
		this.serverSap = serverSap;
		this.serverModel = serverModel;
		this.device = device;
	}

	@Override
	public void actionCalled(final String actionKey) throws ActionException {
		try {
			switch (actionKey) {
			case PRINT_SERVER_MODEL_KEY:
				logger.info("** Printing model.");
				logger.info("Server model:" + this.serverModel);

				break;

			case DEVICE_SHOW_MODEL:
				logger.info(this.device.toString());
			}
		} catch (final Exception e) {
			throw new ActionException(e);
		}
	}

	@Override
	public void quit() {
		logger.info("Shutting down application...");
		this.serverSap.stop();
		return;

	}

}