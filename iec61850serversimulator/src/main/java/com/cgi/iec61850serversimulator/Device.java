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
	String sensorTransition;*/
	Clock clock;
	Relay relay1;
	Relay relay2;
	Relay relay3;
	Relay relay4;
	
	
	
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
		logger.info("** Printing device:");
		logger.info("** Printing clock.\n");
		clock.displayClock();
		
		logger.info("** Printing relays.\n");
		relay1.displayRelay();
		relay2.displayRelay();
		relay3.displayRelay();
		relay4.displayRelay();
	}
	
	public void initalizeDevice(ServerModel serverModel) {

		this.clock = new Clock();
		clock.initializeClock(serverModel.findModelNode("SWDeviceGenericIO/CSLC.Clock", Fc.CF));
		this.relay1 = new Relay();
		relay1.initializeRelay(serverModel.findModelNode("SWDeviceGenericIO/XSWC1.Pos", Fc.CO));
		this.relay2 = new Relay();
		relay2.initializeRelay(serverModel.findModelNode("SWDeviceGenericIO/XSWC2.Pos", Fc.CO));
		this.relay3 = new Relay();
		relay3.initializeRelay(serverModel.findModelNode("SWDeviceGenericIO/XSWC3.Pos", Fc.CO));
		this.relay4 = new Relay();
		relay4.initializeRelay(serverModel.findModelNode("SWDeviceGenericIO/XSWC4.Pos", Fc.CO));
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
