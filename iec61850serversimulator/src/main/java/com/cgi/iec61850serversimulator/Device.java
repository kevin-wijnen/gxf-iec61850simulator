package com.cgi.iec61850serversimulator;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.BdaTimestamp;
import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.FcDataObject;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.internal.scl.Bda;



class Device {
	//TODO: Split the class up into several classes
	private static final Logger logger = LoggerFactory.getLogger(Device.class);
	
	/*String switchType1;
	String switchType2;
	String switchType3;
	String switchType4;
	boolean relayLight2;
	boolean relayLight3;
	boolean relayLight4;
	String lightType;
	
	int offsetAstronomSet;
	int offsetAstronomRise;
	String sensorTransition;
	String dstBeginT;
	String dstEndT;*/
	boolean enableDST;
	LocalDateTime currentTime;
	
	
	
	/*String eventFilterBitmask;
	boolean enableEventBuffered;
	
	// Schedule array?
	
	String functionalFirmwareVer;
	String securityFirmwareVer;
	
	String ipAddressGXF;
	int portGXF;
	String ipAddressNTP;
	int timeSyncInterval;
	
	boolean enableDHCP;
	String ipAddressDHCP;
	String netmaskDHCP;
	String gatewayDHCP;*/
	
	public void displayDevice() {
		logger.info("** Printing device.\n");
		/*logger.info("Switch 1 type: " + switchType1);
		logger.info("Switch 2 type: " + switchType2);
		logger.info("Switch 3 type: " + switchType3);
		logger.info("Switch 4 type: " + switchType4 + "\n");
		
		logger.info("Relay 2 light on: " + relayLight2);
		logger.info("Relay 3 light on: " + relayLight3);
		logger.info("Relay 4 light on: " + relayLight4);
		logger.info("Relay light type: " + lightType + "\n");
		
		logger.info("Offset astronomical time at sunset: " + offsetAstronomSet);
		logger.info("Offset astronomical time at sunrise: " + offsetAstronomRise);
		logger.info("Light sensor transition: Day > Night" + sensorTransition);
		logger.info("Start date Daylight Saving Time: " + dstBeginT);
		logger.info("End date Daylight Saving Time: " + dstEndT);*/
		logger.info("Daylight Saving Time currently active: " + enableDST + "\n");
		logger.info("Current time: " + currentTime + "\n");
		
		/*
		// Change to showcase which types of events are actually enabled!
		logger.info("Event Notification filter bitmask: " + eventFilterBitmask);
		logger.info("Buffered Event Notification reports enbaled: " + enableEventBuffered + "\n");
	
		logger.info("Current functional firmware version: " + functionalFirmwareVer);
		logger.info("Current security firmware version: " + securityFirmwareVer + "\n");
		
		logger.info("IP address for device registration to the GXF platform: " + ipAddressGXF);
		logger.info("Port for device registration to the GXF platform: " + portGXF);
		logger.info("IP address for the Network Time Protocol: " + ipAddressNTP);
		logger.info("Time sync interval in minutes: " + timeSyncInterval + "\n");
		
		logger.info("DHCP server enabled: " + enableDHCP);
		logger.info("Static IP address when DHCP is disabled: " + ipAddressDHCP);
		logger.info("Netmask when DHCP is disabled: " + netmaskDHCP);
		logger.info("Gateway when DHCP is disabled: " + gatewayDHCP);
		*/
	}
	
	public void InitalizeDevice(ServerModel serverModel) {
		// Using serverModel to copy the simulated device's details at boot up.
		/*serverModel.getChild("Clock", FC cf);
		serverModel.getDataSet(reference)*/
		//System.out.println(serverModel.getDataSet("SWDeviceGenericIO/CSLC.Clock.enbDst"));
		
		// For time-related attributes
		
		//System.out.println(serverModel.findModelNode("SWDeviceGenericIO/CSLC.Clock", Fc.CF));
		ModelNode enbDst = serverModel.findModelNode("SWDeviceGenericIO/CSLC.Clock", Fc.CF);
		List<BasicDataAttribute> bdas = enbDst.getBasicDataAttributes();
		
		System.out.println(bdas);
		for (BasicDataAttribute bda : bdas ) {
			String dataAttribute = bda.getName();
			
			if (dataAttribute.equals("curT")) {
				System.out.println("Found Current Time!");
				
				// For native timestamp: Epoch/UNIX timestamp format is used, from second on. Convert to native timestamp!. 
				byte[] bytesEpochTime = ((BdaTimestamp) bda).getValue();
				ByteBuffer wrappedTime = ByteBuffer.wrap(bytesEpochTime);
				long longTime = wrappedTime.getLong();	
				this.currentTime = LocalDateTime.ofEpochSecond(longTime, 0, ZoneOffset.ofHours(1));
				
				
			}
			
			else if (dataAttribute.equals("enbDst")){
				System.out.println("Found Daylight Saving Time status!");
				boolean enableDST = ((BdaBoolean) bda).getValue();
				this.enableDST = enableDST;
			}
			
		}
	}
	
	public void rebootDevice() {
		
	}
	
	public void updateRelayType(boolean switchType1, boolean switchType2, boolean switchType3, boolean switchType4) {
		
	}
	
	public void updateLightStatus(boolean relayLight2, boolean relayLight3, boolean relayLight4) {
		
	}
	
	public void updateAstronomical(int offsetAstronomSet, int offsetAstronomRise, String sensorTransition) {
	
	}
	
	public void updateEventFilter(String eventFilterBitmask, boolean enableEventBuffered) {
		
	}
	
	public void updateFirmware (String functionalFirmwareURL, LocalDateTime functionalFirmwareDownloadTime, String securityFirmwareURL, LocalDateTime securityFirmwareDownloadTime) {
		
	}
	
	public void updateGXF (String ipAddressOSGP, int portOSGP) {
		
	}
	
	public void updateTimeDST(String dsTbeginT, String dstEndT) {
		
	}
	
	public void switchDST() {
		
	}
	
	public void updateNTP(boolean enableNTP, String ipAddressNTP) {
		
	}
	
	public void updateSyncInterval(int timeSyncInterval) {
		
	}
	
	public void updateDHCP(boolean enableDHCP, String ipAddressDHCP, String netmaskDHCP, String gatewayDHCP) {
		
	}
}
