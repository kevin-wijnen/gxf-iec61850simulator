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
                logger.info(dataAttribute);
                logger.info("Bda Type: " + bda.getBasicType());
                // System.out.println("Relay array created." +
                // relayScheduleNumbers);
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
                        final int relayIndex = this.extractRelayIndex(bda.getReference());
                        logger.info("{}", relayIndex);

                        final boolean lightStatus = ((BdaBoolean) bda).getValue();
                        System.out.println(lightStatus);

                        // Sends updated Relay to Relay object
                        this.device.getRelay(relayIndex).setLight(lightStatus);

                        // Sends updated Relay to database
                        this.databaseUtils.updateDatabaseRelay(this.device.getRelay(relayIndex));

                        logger.info("Relay " + relayIndex + "'s light status ON is " + lightStatus);
                    } catch (final Exception e) {
                        logger.info("Something went terribly wrong!");
                        logger.info(e.toString());

                    }
                    break;
                }

                // Schedule data

                case "enable": {
                    logger.info("Schedule Enabled Status found.");
                    // TODO: Why is the previous instance out of scope?
                    final int relayIndex = this.extractRelayIndex(bda.getReference());
                    final int scheduleIndex = this.extractScheduleIndex(bda.getReference());

                    logger.info("Value to set for schedule {} of relay {}: {}", scheduleIndex, relayIndex,
                            ((BdaBoolean) bda).getValue());
                    try {

                        // Sends updated Schedule to Schedule object
                        this.device.getRelay(relayIndex).getSchedule(scheduleIndex)
                                .setEnabled(((BdaBoolean) bda).getValue());

                        // Sends updated Schedule to database
                        this.databaseUtils
                                .updateDatabaseSchedule(this.device.getRelay(relayIndex).getSchedule(scheduleIndex));

                        modified = true;

                    } catch (final Exception e) {
                        logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
                    }
                    break;
                }

                case "day": {
                    logger.info("Day found.");
                    final int relayIndex = this.extractRelayIndex(bda.getReference());
                    final int scheduleIndex = this.extractScheduleIndex(bda.getReference());

                    try {
                        int newDay = ((BdaInt32) bda).getValue();
                        int currentDay = this.device.getRelay(relayIndex).getSchedule(scheduleIndex).getDayInt();
                        if (newDay != currentDay) {
                            modified = true;
                            this.device.getRelay(relayIndex).getSchedule(scheduleIndex).setDayInt(newDay);

                            // Sends updated Schedule to database
                            this.databaseUtils.updateDatabaseSchedule(
                                    this.device.getRelay(relayIndex).getSchedule(scheduleIndex));
                        }
                        modified = true;
                    } catch (final Exception e) {
                        logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
                    }
                    break;
                }

                case "tOn": {
                    logger.info("Time On found.");
                    final int relayIndex = this.extractRelayIndex(bda.getReference());
                    final int scheduleIndex = this.extractScheduleIndex(bda.getReference());
                    try {
                        this.device.getRelay(relayIndex).getSchedule(scheduleIndex);
                        int timeInt = (((BdaInt32) bda).getValue());
                        int timeHour = timeInt / 100;
                        int timeMinute = timeInt % 100;
                        LocalTime timeLocalTime = LocalTime.of(timeHour, timeMinute);

                        this.device.getRelay(relayIndex).getSchedule(scheduleIndex).setTimeOn(timeLocalTime);

                        // Sends updated Schedule to database
                        this.databaseUtils
                                .updateDatabaseSchedule(this.device.getRelay(relayIndex).getSchedule(scheduleIndex));

                        modified = true;
                    } catch (final Exception e) {
                        logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
                    }
                    break;
                }

                case "tOnT": {
                    logger.info("Time On Type found.");
                    final int relayIndex = this.extractRelayIndex(bda.getReference());
                    final int scheduleIndex = this.extractScheduleIndex(bda.getReference());
                    try {
                        this.device.getRelay(relayIndex).getSchedule(scheduleIndex)
                                .setTimeOnTypeInt(((BdaInt8) bda).getValue());

                        // Sends updated Schedule to database
                        this.databaseUtils
                                .updateDatabaseSchedule(this.device.getRelay(relayIndex).getSchedule(scheduleIndex));

                        modified = true;
                    } catch (final Exception e) {
                        logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
                    }
                    break;
                }

                case "tOff": {
                    logger.info("Time Off found.");
                    final int relayIndex = this.extractRelayIndex(bda.getReference());
                    final int scheduleIndex = this.extractScheduleIndex(bda.getReference());
                    try {
                        this.device.getRelay(relayIndex).getSchedule(scheduleIndex);
                        int timeInt = (((BdaInt32) bda).getValue());
                        int timeHour = timeInt / 100;
                        int timeMinute = timeInt % 100;
                        LocalTime timeLocalTime = LocalTime.of(timeHour, timeMinute);

                        this.device.getRelay(relayIndex).getSchedule(scheduleIndex).setTimeOff(timeLocalTime);

                        // Sends updated Schedule to database
                        this.databaseUtils
                                .updateDatabaseSchedule(this.device.getRelay(relayIndex).getSchedule(scheduleIndex));

                        modified = true;
                    } catch (final Exception e) {
                        logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
                    }
                    break;
                }

                case "tOffT": {
                    logger.info("Time Off Type found.");
                    final int relayIndex = this.extractRelayIndex(bda.getReference());
                    final int scheduleIndex = this.extractScheduleIndex(bda.getReference());
                    try {
                        this.device.getRelay(relayIndex).getSchedule(scheduleIndex)
                                .setTimeOffTypeInt(((BdaInt8) bda).getValue());

                        // Sends updated Schedule to database
                        this.databaseUtils
                                .updateDatabaseSchedule(this.device.getRelay(relayIndex).getSchedule(scheduleIndex));

                        modified = true;
                    } catch (final Exception e) {
                        logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
                    }
                    break;
                }

                case "minOnPer": {
                    logger.info("Burning Minutes found.");
                    final int relayIndex = this.extractRelayIndex(bda.getReference());
                    final int scheduleIndex = this.extractScheduleIndex(bda.getReference());
                    try {
                        this.device.getRelay(relayIndex).getSchedule(scheduleIndex)
                                .setBurningMinsOn((short) ((BdaInt16U) bda).getValue());

                        // Sends updated Schedule to database
                        this.databaseUtils
                                .updateDatabaseSchedule(this.device.getRelay(relayIndex).getSchedule(scheduleIndex));

                        modified = true;
                    } catch (final Exception e) {
                        logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
                    }
                    break;
                }

                case "srBefWd": {
                    logger.info("Before Astronomical Time Offset found.");
                    final int relayIndex = this.extractRelayIndex(bda.getReference());
                    final int scheduleIndex = this.extractScheduleIndex(bda.getReference());
                    try {
                        this.device.getRelay(relayIndex).getSchedule(scheduleIndex)
                                .setBeforeOffset((short) ((BdaInt16U) bda).getValue());

                        // Not used in database

                        modified = true;
                    } catch (final Exception e) {
                        logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
                    }
                    break;
                }

                case "srAftWd": {
                    logger.info("After Astronomical Time Offset found.");
                    final int relayIndex = this.extractRelayIndex(bda.getReference());
                    final int scheduleIndex = this.extractScheduleIndex(bda.getReference());
                    try {
                        this.device.getRelay(relayIndex).getSchedule(scheduleIndex)
                                .setAfterOffset((short) ((BdaInt16U) bda).getValue());

                        // Not used in database

                        modified = true;
                    } catch (final Exception e) {
                        logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
                    }
                    break;
                }

                case "Descr": {
                    logger.info("Description found.");
                    System.out.println(referenceString);
                    final int relayIndex = this.extractRelayIndex(bda.getReference());
                    final int scheduleIndex = this.extractScheduleIndex(bda.getReference());
                    try {
                        this.device.getRelay(relayIndex).getSchedule(scheduleIndex)
                                .setDescription(((BdaVisibleString) bda).getValueString());

                        // Not used in database

                    } catch (final Exception e) {
                        logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
                    }
                    break;
                }

                default:
                    // When BDA is not used in a class
                    logger.info("Unimplemented value found, " + "'" + dataAttribute + "'" + ", skipped.");
                    break;
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
        // int scheduleIndex =
        // Integer.parseInt(Character.toString(referenceString.charAt(33)));

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