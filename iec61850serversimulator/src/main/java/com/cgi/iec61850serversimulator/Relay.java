package com.cgi.iec61850serversimulator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.ServerModel;

class Relay {
	private static final Logger logger = LoggerFactory.getLogger(Relay.class);
	
	// Relay 1, 2, 3, 4
	int indexNumber;
	boolean lightStatus;

	public void initializeRelay(ServerModel serverModel, int indexNumber) {
	this.indexNumber = indexNumber;
	
	ModelNode relayInfo = serverModel.findModelNode("SWDeviceGenericIO/XSWC*.SwType.Oper".replace("*", Integer.toString(this.indexNumber)), Fc.CO);
	List<BasicDataAttribute> bdas = relayInfo.getBasicDataAttributes();
	
	for (BasicDataAttribute bda : bdas ) {
		String dataAttribute = bda.getName();
		
		switch(dataAttribute) {
		case "Oper.ctlVal":
			logger.info("Relay Light Status found.");
			this.lightStatus = ((BdaBoolean) bda).getValue();
			break;
			}
		}
	}
	public void displayRelay() {
		logger.info("**Printing relay " + Integer.toString(indexNumber) + "**");
		logger.info("Light status:  " + Boolean.toString(lightStatus) + "\n");
	}
}