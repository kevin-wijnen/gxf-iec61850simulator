package com.cgi.iec61850serversimulator.functionclass;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.BdaInt16;
import com.beanit.openiec61850.BdaInt16U;
import com.beanit.openiec61850.BdaInt32;
import com.beanit.openiec61850.BdaInt8;
import com.beanit.openiec61850.BdaTimestamp;
import com.beanit.openiec61850.BdaVisibleString;
import com.beanit.openiec61850.ObjectReference;
import com.beanit.openiec61850.ServerEventListener;
import com.beanit.openiec61850.ServerSap;
import com.beanit.openiec61850.ServiceError;
import com.cgi.iec61850serversimulator.dataclass.Device;

public class EventDataListener implements ServerEventListener {

    private static final Logger logger = LoggerFactory.getLogger(EventDataListener.class);

    private Device device = null;
    private Scheduler scheduler;

    private DatabaseUtils databaseUtils;

    public Scheduler getScheduler() {
        return this.scheduler;
    }

    public EventDataListener(final Device device, Scheduler scheduler, DatabaseUtils databaseUtils) {
        this.device = device;
        this.scheduler = scheduler;
        this.databaseUtils = databaseUtils;
    }

    @Override
    public List<ServiceError> write(final List<BasicDataAttribute> bdas) {
        logger.info("BDA write request scanning...");
        boolean modified = false;

        try {
            for (final BasicDataAttribute bda : bdas) {
                final String dataAttribute = bda.getName();
                final String referenceString = bda.getReference().toString();

                // Initializing relay and schedule numbers. 0 = unused
                int relayNr = 0;
                int scheduleNr = 0;

                if (referenceString.contains("XSWC")) {
                    relayNr = this.extractRelayIndex(bda.getReference());

                }
                if (referenceString.contains(".Sche.")) {
                    scheduleNr = this.extractScheduleIndex(bda.getReference());
                }

                // Schedules above 50 are not accepted by GXF, thus they will be skipped.
                if (scheduleNr <= 50) {

                    switch (dataAttribute) {

                    // Clock data
                    case "curT":
                        logger.info("Current Time value found.");

                        // For native time stamp: Epoch/UNIX time stamp format is used,
                        // from second on. Convert to native time stamp!.
                        final byte[] bytesEpochTime = ((BdaTimestamp) bda).getValue();
                        final ByteBuffer wrappedTime = ByteBuffer.wrap(bytesEpochTime);
                        final long longTime = wrappedTime.getLong();
                        this.device.getClock()
                                .setCurrentTime(LocalDateTime.ofEpochSecond(longTime, 0, ZoneOffset.ofHours(1)));
                        break;

                    case "tZ":
                        logger.info("Time Zone value found. (Offset in minutes from UTC!)");
                        this.device.getClock().setTimeZoneOffset(((BdaInt16) bda).getValue());
                        break;

                    case "dstBegT":
                        logger.info("Daylight Saving Time Start Date value found.");
                        this.device.getClock().setBeginDateDST(((BdaVisibleString) bda).getValueString());
                        break;

                    case "dstEndT":
                        logger.info("Daylight Saving Time Start Date value found.");
                        this.device.getClock().setEndDateDST(((BdaVisibleString) bda).getValueString());
                        break;

                    case "dvt":
                        logger.info("Daylight Saving Time Deviation value found.");
                        this.device.getClock().setDeviationDST(((BdaInt16) bda).getValue());
                        break;

                    case "enbDst":
                        logger.info("Daylight Saving Time Status value found.");
                        this.device.getClock().setEnableDST(((BdaBoolean) bda).getValue());
                        break;

                    case "enbNtpC":
                        logger.info("NTP Client Enabled value found.");
                        final boolean newValue = ((BdaBoolean) bda).getValue();
                        this.device.getClock().setEnableNTP(newValue);

                        break;

                    case "ntpSvrA":
                        logger.info("NTP Server IP Address value found.");
                        this.device.getClock().setIpAddressNTP(((BdaVisibleString) bda).getValueString());
                        break;

                    case "syncPer":
                        logger.info("Time Sync Interval (in minutes) value found.");
                        this.device.getClock().setTimeSyncInterval(((BdaInt16U) bda).getValue());
                        break;

                    // Relay data
                    // Remember: Set CTLModel to 1, then it would work with enbOpr
                    // enabled!

                    // Reminder: Manually setting ctlVal with GUI client does not work with ctlVal
                    // directly. Set the value with Oper, not just ctlVal only!
                    // Do not use ctlVal as light status value! ctlVal changes stVal, the actual
                    // light status value!
                    case "ctlVal": {
                        try {
                            logger.info(referenceString);
                            logger.info("{}", relayNr);

                            final boolean lightStatus = ((BdaBoolean) bda).getValue();
                            System.out.println(lightStatus);

                            // Sends updated Relay to Relay object
                            this.device.getRelay(relayNr).setLight(lightStatus);

                            // Sends updated Relay to database
                            this.databaseUtils.updateDatabaseRelay(this.device.getRelay(relayNr));

                            logger.info("Relay " + relayNr + "'s light status ON is " + lightStatus);
                        } catch (final Exception e) {
                            logger.info("Something went terribly wrong!");
                            logger.info(e.toString());

                        }
                        break;
                    }

                    // Schedule data

                    case "enable": {

                        logger.info("Schedule Enabled Status found.");
                        logger.info("Value to set for schedule {} of relay {}: {}", scheduleNr, relayNr,
                                ((BdaBoolean) bda).getValue());

                        // Sends updated Schedule to Schedule object
                        this.device.getRelay(relayNr).getSchedule(scheduleNr).setEnabled(((BdaBoolean) bda).getValue());

                        // Sends updated Schedule to database
                        this.databaseUtils
                                .updateDatabaseSchedule(this.device.getRelay(relayNr).getSchedule(scheduleNr));

                        modified = true;
                        break;
                    }

                    case "day": {

                        logger.info("Day found.");
                        int newDay = ((BdaInt32) bda).getValue();
                        int currentDay = this.device.getRelay(relayNr).getSchedule(scheduleNr).getDayInt();
                        if (newDay != currentDay) {
                            modified = true;
                            this.device.getRelay(relayNr).getSchedule(scheduleNr).setDayInt(newDay);

                            // Sends updated Schedule to database
                            this.databaseUtils
                                    .updateDatabaseSchedule(this.device.getRelay(relayNr).getSchedule(scheduleNr));
                            modified = true;
                        }
                        break;
                    }

                    case "tOn": {

                        logger.info("Time On found.");
                        this.device.getRelay(relayNr).getSchedule(scheduleNr);
                        int timeInt = (((BdaInt32) bda).getValue());
                        int timeHour = timeInt / 100;
                        int timeMinute = timeInt % 100;
                        LocalTime timeLocalTime = LocalTime.of(timeHour, timeMinute);

                        this.device.getRelay(relayNr).getSchedule(scheduleNr).setTimeOn(timeLocalTime);

                        // Sends updated Schedule to database
                        this.databaseUtils
                                .updateDatabaseSchedule(this.device.getRelay(relayNr).getSchedule(scheduleNr));

                        modified = true;
                        break;
                    }

                    case "tOnT": {

                        logger.info("Time On Type found.");
                        this.device.getRelay(relayNr).getSchedule(scheduleNr)
                                .setTimeOnTypeInt(((BdaInt8) bda).getValue());

                        // Sends updated Schedule to database
                        this.databaseUtils
                                .updateDatabaseSchedule(this.device.getRelay(relayNr).getSchedule(scheduleNr));

                        modified = true;

                        break;
                    }

                    case "tOff": {

                        logger.info("Time Off found.");
                        this.device.getRelay(relayNr).getSchedule(scheduleNr);
                        int timeInt = (((BdaInt32) bda).getValue());
                        int timeHour = timeInt / 100;
                        int timeMinute = timeInt % 100;
                        LocalTime timeLocalTime = LocalTime.of(timeHour, timeMinute);

                        this.device.getRelay(relayNr).getSchedule(scheduleNr).setTimeOff(timeLocalTime);

                        // Sends updated Schedule to database
                        this.databaseUtils
                                .updateDatabaseSchedule(this.device.getRelay(relayNr).getSchedule(scheduleNr));

                        modified = true;

                        break;
                    }

                    case "tOffT": {

                        logger.info("Time Off Type found.");
                        this.device.getRelay(relayNr).getSchedule(scheduleNr)
                                .setTimeOffTypeInt(((BdaInt8) bda).getValue());

                        // Sends updated Schedule to database
                        this.databaseUtils
                                .updateDatabaseSchedule(this.device.getRelay(relayNr).getSchedule(scheduleNr));

                        modified = true;
                        break;
                    }

                    case "minOnPer": {

                        logger.info("Burning Minutes found.");
                        this.device.getRelay(relayNr).getSchedule(scheduleNr)
                                .setBurningMinsOn((short) ((BdaInt16U) bda).getValue());

                        // Sends updated Schedule to database
                        this.databaseUtils
                                .updateDatabaseSchedule(this.device.getRelay(relayNr).getSchedule(scheduleNr));

                        modified = true;
                        break;
                    }

                    case "srBefWd": {

                        logger.info("Before Astronomical Time Offset found.");
                        this.device.getRelay(relayNr).getSchedule(scheduleNr)
                                .setBeforeOffset((short) ((BdaInt16U) bda).getValue());

                        // Not used in database

                        modified = true;

                        break;
                    }

                    case "srAftWd": {

                        logger.info("After Astronomical Time Offset found.");
                        this.device.getRelay(relayNr).getSchedule(scheduleNr)
                                .setAfterOffset((short) ((BdaInt16U) bda).getValue());

                        // Not used in database

                        modified = true;

                        break;
                    }

                    case "Descr": {

                        logger.info("Description found.");
                        this.device.getRelay(relayNr).getSchedule(scheduleNr)
                                .setDescription(((BdaVisibleString) bda).getValueString());

                        // Not used in database

                        break;
                    }

                    default:
                        // When BDA is not used in a class
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Exception EventDataListener loop", e);
        }

        logger.info("\n\n Received BDA data applied to data objects.");

        // Flag for Schedule modifications
        if (modified) {
            logger.warn("Schedules are modified! Calculate switching moments...");
            try {
                this.scheduler.calculateTasks(this.device);
            } catch (SwitchingMomentCalculationException e) {
                logger.warn("SwitchingMomentCalculationException: ", e);
            }
        }

        return null;
    }

    @Override
    public void serverStoppedListening(final ServerSap serverSAP) {
        // TODO Auto-generated method stub

    }

    public int extractRelayIndex(final ObjectReference reference) {
        final String referenceString = reference.toString();

        final int[] regexResults = new int[1];
        final Pattern numberPattern = Pattern.compile("[0-9]{1,}");
        final Matcher numberMatcher = numberPattern.matcher(referenceString);
        numberMatcher.find();
        regexResults[0] = Integer.parseInt(numberMatcher.group());
        final int relayIndex = regexResults[0];

        return relayIndex;

    }

    public int extractScheduleIndex(final ObjectReference reference) {
        final String referenceString = reference.toString();

        final int[] regexResults = new int[2];
        final Pattern numberPattern = Pattern.compile("[0-9]{1,}");
        final Matcher numberMatcher = numberPattern.matcher(referenceString);

        for (int i = 0; i < 2; i++) {
            numberMatcher.find();
            regexResults[i] = Integer.parseInt(numberMatcher.group());
        }
        final int scheduleIndex = regexResults[1];

        return scheduleIndex;
    }
}