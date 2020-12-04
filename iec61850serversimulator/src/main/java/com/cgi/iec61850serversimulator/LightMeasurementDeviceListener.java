package com.cgi.iec61850serversimulator;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaInt16U;
import com.beanit.openiec61850.ServerEventListener;
import com.beanit.openiec61850.ServerSap;
import com.beanit.openiec61850.ServiceError;

class LightMeasurementDeviceListener implements ServerEventListener{
	
	private static final Logger logger = LoggerFactory.getLogger(LightMeasurementDeviceListener.class);
	LocalDateTime currentTime = null;
	String syncPer;
	Device device = null;
	
	public LightMeasurementDeviceListener(Device device) {
		this.device = device;
	}

	@Override
	public List<ServiceError> write(List<BasicDataAttribute> bdas) {
		logger.info("BDA write request scanning...");
		
		for (BasicDataAttribute bda : bdas) {
			String updatedNodeName = bda.getName();
			System.out.println(updatedNodeName);
			try {
			if (updatedNodeName.equals("enbDst")){	
				logger.info("DST STATUS FOUND!");
				logger.info(bda.getValueString());
				boolean daylightActive = Boolean.parseBoolean(bda.getValueString());
				// TODO: Convert current SwitchDST function to one that actually accepts input instead of just switching!
				device.enableDST = daylightActive;
			}
			
			else if (updatedNodeName.equals("curT")){
				logger.info("CURRENT TIME FOUND!");
				logger.info(bda.getValueString());
				currentTime = LocalDateTime.parse(bda.getValueString());}
			
			else if (updatedNodeName.equals("syncPer")) {
			logger.info("TIME SYNC INTERVAL FOUND!");
			int syncPer = ((BdaInt16U) bda).getValue();
			logger.info("Value: " + syncPer);
			device.updateSyncInterval(syncPer);
			}
			
			else if (updatedNodeName.equals(updatedNodeName.equals("Oper.ctlVal"))) {
				logger.info("SWITCH LIGHT STATUS FOUND!");
				System.out.println(bda.getParent());
			}
			
			/*else if (bda.getName().equals(bda)){
				
			}*/
			
			} catch (RuntimeException re)	 {
				logger.error("Parsen van nodewaarde is gefaald", re);
			}

						
		//device.updateDeviceCheck(daylightActive);
		//BLIJFT VASTHANGEN ALS IE CURRENTTIME OF SYNCPER OPHAALT! SYNCPER IS NULL, dayLightActive werkt wel gewoon!
		// Vasthangen = opnieuw moeten reconnecten om de server opnieuw te starten, programma zelf blijft wel draaien zonder crash. (Dus 'stopped listening'?)
			}
			
			/*if (bda.getParent().toString().contains("SWDeviceGenericIO/CSLC.Clock.enbDst:")) {
				logger.info(bda.getName());
			}*/
			
			/*if (bda.getParent().toString().contains("SWDeviceGenericIO/XSWC") && bda.getParent().toString().contains("Sche.sche"))
				logger.info("Switch schedule data.");
	          	logger.info(bda.getName());
	          	logger.info(bda.getParent().getName());
	     }*/
		logger.info("**Printing device.");
		//device.deviceDisplay();
		
		return null;
	}

	@Override
	public void serverStoppedListening(ServerSap serverSAP) {
		// TODO Auto-generated method stub
		
	}

}