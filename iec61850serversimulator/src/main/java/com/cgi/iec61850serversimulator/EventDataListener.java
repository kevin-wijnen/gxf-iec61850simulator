package com.cgi.iec61850serversimulator;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
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

class EventDataListener implements ServerEventListener {

    private static final Logger logger = LoggerFactory.getLogger(EventDataListener.class);
    LocalDateTime currentTime = null;
    String syncPer;
    Device device = null;

    public EventDataListener(final Device device) {
        this.device = device;

    }

    @Override
    public List<ServiceError> write(final List<BasicDataAttribute> bdas) {
        // TODO: Fixing relayScheduleNumbers integration to not lose connection
        // with GUI/SoapUI! Problem lies with the setters.
        logger.info("BDA write request scanning...");

        for (final BasicDataAttribute bda : bdas) {
            final String dataAttribute = bda.getName();
            final String referenceString = bda.getReference().toString();
            logger.info(dataAttribute);
            final int[] relayScheduleNumbers;
            // System.out.println("Relay array aangemaakt." +
            // relayScheduleNumbers);
            switch (dataAttribute) {

            // Clock data
            case "curT":
                logger.info("Current Time value found.");

                // For native timestamp: Epoch/UNIX timestamp format is used,
                // from second on. Convert to native timestamp!.
                final byte[] bytesEpochTime = ((BdaTimestamp) bda).getValue();
                final ByteBuffer wrappedTime = ByteBuffer.wrap(bytesEpochTime);
                final long longTime = wrappedTime.getLong();
                this.device.getClock().setCurrentTime(LocalDateTime.ofEpochSecond(longTime, 0, ZoneOffset.ofHours(1)));
                // System.out.println("CurT variabel test");
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

                // Ruud: example to modify a light status, which is what should
                // happen at the point in
                // time when a schedule moment should change a light status.
                this.device.setLightStatus(2, newValue);

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
            case "ctlVal": {
                final int relayIndex = this.extractRelayIndex(bda.getReference());
                final boolean lightStatus = ((BdaBoolean) bda).getValue();
                System.out.println(lightStatus);

                this.device.getRelay(relayIndex).setLight(lightStatus);

                logger.info("Relay " + relayIndex + "'s light status ON is " + lightStatus);
                break;
            }

            // Schedule data

            case "enable": {
                logger.info("Schedule Enabled Status found.");
                // TODO: Why is the previous instance out of scope?
                final int relayIndex = this.extractRelayIndex(bda.getReference());
                final int scheduleIndex = this.extractScheduleIndex(bda.getReference());
                // relayScheduleNumbers =
                // extractRelayScheduleNumbers(bda.getReference());

                logger.info("Value to set for schedule 1 of relay 1:", ((BdaBoolean) bda).getValue());
                try {
                    // device.getSchedule(relayScheduleNumbers).setEnabled(((BdaBoolean)
                    // bda).getValue());
                    this.device.getRelay(relayIndex)
                            .getSchedule(scheduleIndex)
                            .setEnabled(((BdaBoolean) bda).getValue());
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
                    this.device.getRelay(relayIndex).getSchedule(scheduleIndex).setDayInt(((BdaInt32) bda).getValue());
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
                    this.device.getRelay(relayIndex)
                            .getSchedule(scheduleIndex)
                            .setTimeOn((short) ((BdaInt32) bda).getValue());
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
                    this.device.getRelay(relayIndex)
                            .getSchedule(scheduleIndex)
                            .setTimeOnTypeInt(((BdaInt8) bda).getValue());
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
                    this.device.getRelay(relayIndex)
                            .getSchedule(scheduleIndex)
                            .setTimeOff((short) ((BdaInt32) bda).getValue());
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
                    this.device.getRelay(relayIndex)
                            .getSchedule(scheduleIndex)
                            .setTimeOffTypeInt(((BdaInt8) bda).getValue());
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
                    this.device.getRelay(relayIndex)
                            .getSchedule(scheduleIndex)
                            .setBurningMinsOn((short) ((BdaInt16U) bda).getValue());
                } catch (final Exception e) {
                    logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
                }
                break;
            }

            case "srBefWd": {
                logger.info("Before Astrological Time Offset found.");
                final int relayIndex = this.extractRelayIndex(bda.getReference());
                final int scheduleIndex = this.extractScheduleIndex(bda.getReference());
                try {
                    this.device.getRelay(relayIndex)
                            .getSchedule(scheduleIndex)
                            .setBeforeOffset((short) ((BdaInt16U) bda).getValue());
                } catch (final Exception e) {
                    logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
                }
                break;
            }

            case "srAftWd": {
                logger.info("After Astrological Time Offset found.");
                final int relayIndex = this.extractRelayIndex(bda.getReference());
                final int scheduleIndex = this.extractScheduleIndex(bda.getReference());
                try {
                    this.device.getRelay(relayIndex)
                            .getSchedule(scheduleIndex)
                            .setAfterOffset((short) ((BdaInt16U) bda).getValue());
                } catch (final Exception e) {
                    logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
                }
                break;
            }

            case "Descr": {
                logger.info(" found.");
                final int relayIndex = this.extractRelayIndex(bda.getReference());
                final int scheduleIndex = this.extractScheduleIndex(bda.getReference());
                try {
                    this.device.getRelay(relayIndex)
                            .getSchedule(scheduleIndex)
                            .setDescription(((BdaVisibleString) bda).getValueString());
                } catch (final Exception e) {
                    logger.info("Schedules above 50 are not implemented in the GXF platform. Skip ...");
                }
                break;
            }

            default:
                logger.info("Unimplemented value found, " + "'" + dataAttribute + "'" + ", skipped.");
                break;
            }
            /*
             * System.out.println(bda.getReference());
             * System.out.println(bda.getFc()); String updatedNodeName =
             * bda.getName(); System.out.println(updatedNodeName);
             * System.out.println(bda.getValueString()); try {
             *
             *
             *
             * /*if (updatedNodeName.equals("enbDst")){
             * logger.info("DST STATUS FOUND!");
             * logger.info(bda.getValueString()); boolean daylightActive =
             * Boolean.parseBoolean(bda.getValueString());
             * device.clock.changeDST(daylightActive); }
             *
             * else if (updatedNodeName.equals("curT")){
             * logger.info("CURRENT TIME FOUND!");
             * logger.info(bda.getValueString()); currentTime =
             * LocalDateTime.parse(bda.getValueString());}
             *
             * else if (updatedNodeName.equals("syncPer")) {
             * logger.info("TIME SYNC INTERVAL FOUND!"); int syncPer =
             * ((BdaInt16U) bda).getValue(); logger.info("Value: " + syncPer);
             * device.updateSyncInterval(syncPer); }
             *
             * else if (updatedNodeName.equals("Oper.ctlVal")) {
             * logger.info("SWITCH LIGHT STATUS FOUND!");
             * System.out.println(bda.getParent()); }
             *
             * /*else if (bda.getName().equals(bda)){
             *
             * }
             *
             * } catch (RuntimeException re) {
             * logger.error("Parsen van nodewaarde is gefaald", re); }
             */

            // device.updateDeviceCheck(daylightActive);
            // BLIJFT VASTHANGEN ALS IE CURRENTTIME OF SYNCPER OPHAALT! SYNCPER
            // IS NULL, dayLightActive werkt wel gewoon!
            // Vasthangen = opnieuw moeten reconnecten om de server opnieuw te
            // starten, programma zelf blijft wel draaien zonder crash. (Dus
            // 'stopped listening'?)
        }

        /*
         * if (bda.getParent().toString().contains(
         * "SWDeviceGenericIO/CSLC.Clock.enbDst:")) {
         * logger.info(bda.getName()); }
         */

        /*
         * if (bda.getParent().toString().contains("SWDeviceGenericIO/XSWC") &&
         * bda.getParent().toString().contains("Sche.sche"))
         * logger.info("Switch schedule data."); logger.info(bda.getName());
         * logger.info(bda.getParent().getName()); }
         */
        logger.info("**Printing device.");
        // device.deviceDisplay();

        return null;
    }

    @Override
    public void serverStoppedListening(final ServerSap serverSAP) {
        // TODO Auto-generated method stub

    }

    public int[] extractRelayScheduleNumbers(final ObjectReference reference) {
        System.out.println(reference);
        System.out.println(reference.toString());

        final int[] relaySchedule = new int[2];
        final String referenceString = reference.toString();
        System.out.println("Reference string aangemaakt.");

        relaySchedule[0] = Integer.parseInt(Character.toString(referenceString.charAt(22)));
        System.out.println("Eerste integer in relaySchedule aangemaakt: " + relaySchedule[0]);

        // Check to see if schedule number is over 9, to take second digit into
        // account
        try {
            final String scheduleNumber = Character.toString(referenceString.charAt(33))
                    + Character.toString(referenceString.charAt(34));
            relaySchedule[1] = Integer.parseInt(scheduleNumber);
            System.out.println("Eerste nummer gevonden voor tweede getal.");
        } catch (final Exception e) {
            System.out.println("Tweede getal gevonden! Nu nog naar integer maken.");
            relaySchedule[1] = Integer.parseInt(Character.toString(referenceString.charAt(33)));
            System.out.println("Tweede getal omgezet.");
        }

        System.out.println(relaySchedule[0]);
        System.out.println(relaySchedule[1]);

        return relaySchedule;
    }

    public int extractRelayIndex(final ObjectReference reference) {
        final String referenceString = reference.toString();

        final int[] regexResults = new int[2];
        final Pattern numberPattern = Pattern.compile("[0-9]{1,}");
        final Matcher numberMatcher = numberPattern.matcher(reference.toString());

        for (int i = 0; i < 2; i++) {
            numberMatcher.find();
            regexResults[i] = Integer.parseInt(numberMatcher.group());
        }
        final int relayIndex = regexResults[0];

        return relayIndex;

    }

    public int extractScheduleIndex(final ObjectReference reference) {
        final String referenceString = reference.toString();
        // int scheduleIndex =
        // Integer.parseInt(Character.toString(referenceString.charAt(33)));

        final int[] regexResults = new int[2];
        final Pattern numberPattern = Pattern.compile("[0-9]{1,}");
        final Matcher numberMatcher = numberPattern.matcher(reference.toString());

        for (int i = 0; i < 2; i++) {
            numberMatcher.find();
            regexResults[i] = Integer.parseInt(numberMatcher.group());
        }
        final int scheduleIndex = regexResults[1];

        return scheduleIndex;
    }

}