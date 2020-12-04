package com.cgi.iec61850serversimulator;

import java.text.MessageFormat;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



class DeviceOld {
	private static final Logger logger = LoggerFactory.getLogger(DeviceOld.class);
	//LocalDateTime currentTime;
	boolean enbDst;
	int syncPer;
	
	public void deviceInit(boolean enbDst) {
	//TODO: Device initialiseren gebaseerd op de serverModel dat is meegegeven
		updateDst(enbDst);
	}
	

	public void updateDeviceCheck(boolean enbDst) {
	//TODO: Verder invullen om device object up te daten
	//System.out.println(currentTime.toString());

	System.out.println(enbDst);
	//System.out.println(syncPer);
	System.out.println("test!");
	
	}
	
	public void updateDst(boolean enbDst) {
		this.enbDst = enbDst;
		
	}
	
	public void updateSyncPer(int syncPer) {
		this.syncPer = syncPer;
	}
	
	
	
	public void deviceDisplay() {
		logger.info("**Device details**");
		logger.info(MessageFormat.format("Daylight Saving Time active: {0} ", enbDst));
		logger.info(MessageFormat.format("Time resync interval: {0}", syncPer));
	}
}
