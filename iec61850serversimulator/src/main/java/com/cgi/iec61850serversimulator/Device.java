package com.cgi.iec61850serversimulator;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.BdaInt8;
import com.beanit.openiec61850.Fc;

class Device {
	// TODO: Split the class up into several classes
	private static final Logger logger = LoggerFactory.getLogger(Device.class);

	/*
	 * String switchType1; String switchType2; String switchType3; String
	 * switchType4; boolean relayLight2; boolean relayLight3; boolean relayLight4;
	 * String lightType;
	 *
	 * int offsetAstronomSet; int offsetAstronomRise; String sensorTransition;
	 */
	private Clock clock;
	private Relay[] relays;

	private ServerWrapper serverWrapper;

	/*
	 * String eventFilterBitmask; boolean enableEventBuffered;
	 *
	 * // Schedule array?
	 *
	 * String functionalFirmwareVer; String securityFirmwareVer;
	 *
	 * String ipAddressGXF; int portGXF; String ipAddressNTP; int timeSyncInterval;
	 *
	 * boolean enableDHCP; String ipAddressDHCP; String netmaskDHCP; String
	 * gatewayDHCP;
	 */

	public void displayDevice() {
		logger.info("** Printing device:");
		logger.info("** Printing clock.\n");
		this.clock.displayClock();

		logger.info("** Printing relays.\n");
		for (int relayNr = 0; relayNr < 4; relayNr++) {
			this.relays[relayNr].displayRelay();
			for (int scheduleNr = 0; scheduleNr < 50; scheduleNr++) {
				logger.info(this.getRelay(relayNr + 1).getSchedule(scheduleNr + 1).toString());
			}
		}

	}

	public void initalizeDevice(final ServerWrapper serverSapWrapper) {

		this.serverWrapper = serverSapWrapper;
		this.clock = new Clock();
		this.clock.initializeClock(this.serverWrapper.findModelNode("SWDeviceGenericIO/CSLC.Clock", Fc.CF));
		this.relays = new Relay[4];

		for (int relayNr = 0; relayNr < 4; relayNr++) {
			this.relays[relayNr] = new Relay(
					this.serverWrapper.findModelNode("SWDeviceGenericIO/XSWC" + (relayNr + 1) + ".Pos", Fc.CO),
					this.serverWrapper.findModelNode("SWDeviceGenericIO/XSWC" + (relayNr + 1) + ".Sche.sche1", Fc.CF));
			this.setCtlModel(relayNr + 1, 1);
		}
		// CTLModel naar 1 veranderen door de ServerModel te muteren
		// Kijk naar Ruud z'n voorbeeld bij setLightStatus!

	}

	public Clock getClock() {
		return this.clock;
	}

	public Relay getRelay(final int index) {
		return this.relays[index - 1];
	}

	public Schedule getSchedule(final int[] relayScheduleNumbers) {
		return this.getRelay(relayScheduleNumbers[0]).getSchedule(relayScheduleNumbers[1]);
	}

	public void setLightStatus(final int relay, final boolean status) {
		try {
			logger.info("Set light status for relay {} to value {}", relay, status);

			final BdaBoolean lightStatus = (BdaBoolean) this.serverWrapper
					.findModelNode("SWDeviceGenericIO/XSWC" + relay + ".Pos.Oper.ctlVal", Fc.CO);
			// .findModelNode("SWDeviceGenericIO/XSWC" + relay + ".Pos.stVal",
			// Fc.ST);
			lightStatus.setValue(status);

			final List<BasicDataAttribute> attributes = Arrays.asList(lightStatus);
			this.serverWrapper.setValues(attributes);
		} catch (final Exception e) {
			logger.error("Find node or set value failed", e);
		}
	}

	public void setCtlModel(final int relay, final int ctlModelInt) {
		try {
			// Necessary to have CTLModel set to 1 in the ServerModel along with having
			// enbOpr enabled (enabled on default) to be able to switch the status.
			logger.info("Setting CTLModel to 1 to enable relay control");

			final BdaInt8 ctlModel = (BdaInt8) this.serverWrapper
					.findModelNode("SWDeviceGenericIO/XSWC" + relay + ".Pos.ctlModel", Fc.CF);
			byte ctlModelByte = 01;
			logger.info("Byte: " + ctlModelByte);
			ctlModel.setValue((byte) 01);
			logger.info("CTLModel: {}", ctlModelByte);

			final List<BasicDataAttribute> attributes = Arrays.asList(ctlModel);
			this.serverWrapper.setValues(attributes);

		} catch (final Exception e) {
			logger.error("Setting CTLModel failed", e);
		}
	}
}