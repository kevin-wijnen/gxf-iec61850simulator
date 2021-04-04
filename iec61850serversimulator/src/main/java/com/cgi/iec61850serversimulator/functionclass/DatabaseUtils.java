package com.cgi.iec61850serversimulator.functionclass;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.BdaInt16U;
import com.beanit.openiec61850.BdaInt32;
import com.beanit.openiec61850.BdaInt8;
import com.beanit.openiec61850.BdaVisibleString;
import com.beanit.openiec61850.Fc;
import com.cgi.iec61850serversimulator.dataclass.Relay;
import com.cgi.iec61850serversimulator.dataclass.Schedule;
import com.cgi.iec61850serversimulator.datamodel.RelayEntity;
import com.cgi.iec61850serversimulator.datamodel.ScheduleEntity;
import com.cgi.iec61850serversimulator.datarepository.RelayRepository;
import com.cgi.iec61850serversimulator.datarepository.ScheduleRepository;

public class DatabaseUtils {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    private static final String SWITCH_ROOT = "SWDeviceGenericIO/XSWC";

    private RelayRepository relayRepository;
    private ScheduleRepository scheduleRepository;

    private ServerWrapper serverWrapper;

    public DatabaseUtils(ServerWrapper serverWrapper) {
        this.serverWrapper = serverWrapper;
    }

    public RelayRepository getRelayRepository() {
        return this.relayRepository;
    }

    public void setRelayRepository(RelayRepository relayRepository) {
        this.relayRepository = relayRepository;
    }

    public ScheduleRepository getScheduleRepository() {
        return this.scheduleRepository;
    }

    public void setScheduleRepository(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    // Relay functions
    public void checkRelay(Relay relay) {
        // Check database if relay exists
        // Make sure relayNr is 1, 2, 3, 4; not 0, 1, 2, 3!

        // Checks if relay can be found with index number. If not found, then it will
        // copy the model's relay to the database.
        boolean found = this.relayRepository.existsByIndexNumber(relay.getIndexNumber());

        if (found) {
            logger.info("Relay found in database. Updating model.");
            this.updateModelRelay(relay);
        } else {
            logger.info("Relay not found in database, add it to database.");
            this.initializeDatabaseRelay(relay);
        }
    }

    public void updateModelRelay(Relay relay) {
        // Update relay model with BDA
        logger.info("Updating model relay  database.");
        int relayNr = relay.getIndexNumber();
        List<RelayEntity> databaseRelay = this.relayRepository.findByIndexNumber(relayNr);
        final BdaBoolean modelRelayLightStatus = (BdaBoolean) this.serverWrapper
                .findModelNode(SWITCH_ROOT + relayNr + ".Pos.stVal", Fc.ST);
        modelRelayLightStatus.setValue(databaseRelay.get(0).isLightStatus());
        List<BasicDataAttribute> bdas = new ArrayList<BasicDataAttribute>();
        bdas.add(modelRelayLightStatus);
        relay.setLight(databaseRelay.get(0).isLightStatus());
        this.serverWrapper.setValues(bdas);

    }

    public void initializeDatabaseRelay(Relay relay) {
        // Add missing relay to database

        int index_number = relay.getIndexNumber();
        boolean light_status = relay.getLight();
        RelayEntity toUpdateRelay = new RelayEntity(index_number, light_status);
        this.relayRepository.save(toUpdateRelay);
    }

    public void updateDatabaseRelay(Relay relay) {
        // Updating database relay

        int index_number = relay.getIndexNumber();
        boolean light_status = relay.getLight();
        List<RelayEntity> relayEntities = this.relayRepository.findByIndexNumber(index_number);
        RelayEntity toUpdateRelay = relayEntities.get(0);
        toUpdateRelay.setLightStatus(light_status);
        this.relayRepository.save(toUpdateRelay);
    }

    // Schedule functions

    public void checkSchedule(Schedule schedule, int relayNr) {
        // Check database if schedule exists
        // Make sure relayNr is 1, 2, 3, 4; not 0, 1, 2, 3!

        // Checks if relay can be found with index number. If not found, then it will
        // copy the model's relay to the database.
        boolean found = this.scheduleRepository.existsByIndexNumberAndRelayId(schedule.getIndexNumber(), relayNr);

        if (found) {
            logger.info("Schedule found in database. Updating model.");
            this.updateModelSchedule(schedule, relayNr);
        } else {

            if (schedule.isEnabled()) {
                logger.info("Enabled schedule not found in database, add it to database.");
                this.initializeDatabaseSchedule(schedule, relayNr);
            } else {
                logger.info("Schedule not enabled, not added to database.");
            }
        }
    }

    public void updateModelSchedule(Schedule schedule, int relayNr) {
        // Update relay model with BDA
        logger.info("Updating model schedule with database.");
        int scheduleNr = schedule.getIndexNumber();
        String scheduleRoot = SWITCH_ROOT + relayNr + ".Sche.sche" + scheduleNr;
        List<ScheduleEntity> databaseSchedules = this.scheduleRepository.findByIndexNumberAndRelayId(scheduleNr,
                relayNr);
        ScheduleEntity databaseSchedule = databaseSchedules.get(0);
        BdaBoolean modelScheduleEnabled = (BdaBoolean) this.serverWrapper.findModelNode(scheduleRoot + ".enable",
                Fc.CF);
        modelScheduleEnabled.setValue(databaseSchedule.isEnabled());
        BdaInt32 modelScheduleDay = (BdaInt32) this.serverWrapper.findModelNode(scheduleRoot + ".day", Fc.CF);
        modelScheduleDay.setValue(databaseSchedule.getDay());

        BdaInt32 modelScheduleTimeOn = (BdaInt32) this.serverWrapper.findModelNode(scheduleRoot + ".tOn", Fc.CF);
        BdaInt32 modelScheduleTimeOff = (BdaInt32) this.serverWrapper.findModelNode(scheduleRoot + ".tOff", Fc.CF);
        // Converting LocalTime to HHMM in Int32
        int convertedTimeOn = TimeCalculator.localTimeToHoursMinutesInt(databaseSchedule.getTimeOn());
        modelScheduleTimeOn.setValue(convertedTimeOn);
        int convertedTimeOff = TimeCalculator.localTimeToHoursMinutesInt(databaseSchedule.getTimeOff());
        modelScheduleTimeOff.setValue(convertedTimeOff);

        BdaInt8 modelScheduleTimeOnType = (BdaInt8) this.serverWrapper.findModelNode(scheduleRoot + ".tOnT", Fc.CF);
        modelScheduleTimeOnType.setValue((byte) databaseSchedule.getTimeTypeOn());
        BdaInt8 modelScheduleTimeOffType = (BdaInt8) this.serverWrapper.findModelNode(scheduleRoot + ".tOnT", Fc.CF);
        modelScheduleTimeOffType.setValue((byte) databaseSchedule.getTimeTypeOff());
        BdaInt16U modelScheduleBurningMinutes = (BdaInt16U) this.serverWrapper.findModelNode(scheduleRoot + ".minOnPer",
                Fc.CF);
        modelScheduleBurningMinutes.setValue(databaseSchedule.getBurningMinutes());
        // Unused
        BdaInt16U modelScheduleMinOffPer = (BdaInt16U) this.serverWrapper.findModelNode(scheduleRoot + ".minOffPer",
                Fc.CF);
        modelScheduleMinOffPer.setDefault();
        // Astronomic offsets, unused as of now
        BdaInt16U modelScheduleSensorBefore = (BdaInt16U) this.serverWrapper.findModelNode(scheduleRoot + ".srBefWd",
                Fc.CF);
        modelScheduleSensorBefore.setDefault();
        BdaInt16U modelScheduleSensorAfter = (BdaInt16U) this.serverWrapper.findModelNode(scheduleRoot + ".srAftWd",
                Fc.CF);
        modelScheduleSensorAfter.setDefault();
        // Unused
        BdaInt16U modelScheduleIgBefWd = (BdaInt16U) this.serverWrapper.findModelNode(scheduleRoot + ".igBefWd", Fc.CF);
        modelScheduleIgBefWd.setDefault();
        // Unused
        BdaInt16U modelScheduleIgAftWd = (BdaInt16U) this.serverWrapper.findModelNode(scheduleRoot + ".igAftWd", Fc.CF);
        modelScheduleIgAftWd.setDefault();
        // Unused (description)
        BdaVisibleString modelScheduleDescription = (BdaVisibleString) this.serverWrapper
                .findModelNode(scheduleRoot + ".Descr", Fc.CF);
        modelScheduleDescription.setDefault();

        List<BasicDataAttribute> bdas = new ArrayList<BasicDataAttribute>();
        bdas.add(modelScheduleEnabled);
        bdas.add(modelScheduleDay);
        bdas.add(modelScheduleTimeOn);
        bdas.add(modelScheduleTimeOnType);
        bdas.add(modelScheduleTimeOff);
        bdas.add(modelScheduleTimeOffType);
        bdas.add(modelScheduleBurningMinutes);
        bdas.add(modelScheduleMinOffPer);
        bdas.add(modelScheduleSensorBefore);
        bdas.add(modelScheduleSensorAfter);
        bdas.add(modelScheduleIgBefWd);
        bdas.add(modelScheduleIgAftWd);
        bdas.add(modelScheduleDescription);

        schedule.setEnabled(databaseSchedule.isEnabled());
        schedule.setDayInt(databaseSchedule.getDay());
        schedule.setTimeOn(databaseSchedule.getTimeOn());
        schedule.setTimeOnTypeInt(databaseSchedule.getTimeTypeOn());
        schedule.setTimeOff(databaseSchedule.getTimeOff());
        schedule.setTimeOffTypeInt(databaseSchedule.getTimeTypeOff());
        schedule.setBurningMinsOn(databaseSchedule.getBurningMinutes());

        this.serverWrapper.setValues(bdas);
    }

    public void initializeDatabaseSchedule(Schedule schedule, int relayNr) {
        // Add missing schedule to database

        int index_number = schedule.getIndexNumber();
        int day = schedule.getDayInt();
        int timeOnType = schedule.getTimeOnTypeInt();
        int timeOffType = schedule.getTimeOffTypeInt();
        LocalTime timeOn = schedule.getTimeOn();
        LocalTime timeOff = schedule.getTimeOff();
        int burningMinutes = schedule.getBurningMinsOn();
        boolean enabled = schedule.isEnabled();
        RelayEntity relayEntity = this.relayRepository.findByIndexNumber(relayNr).get(0);
        ScheduleEntity toUpdateSchedule = new ScheduleEntity(index_number, day, timeOnType, timeOffType, timeOn,
                timeOff, burningMinutes, enabled, relayEntity);
        this.scheduleRepository.save(toUpdateSchedule);
    }

    public void updateDatabaseSchedule(Schedule schedule) {
        // Updating database schedule

        int index_number = schedule.getIndexNumber();
        int relayNr = schedule.getRelayNr();

        List<ScheduleEntity> scheduleEntities = this.scheduleRepository.findByIndexNumberAndRelayId(index_number,
                relayNr);
        ScheduleEntity toUpdateSchedule = scheduleEntities.get(0);
        toUpdateSchedule.setEnabled(schedule.isEnabled());
        toUpdateSchedule.setDay(schedule.getDayInt());
        toUpdateSchedule.setTimeOn(schedule.getTimeOn());
        toUpdateSchedule.setTimeTypeOn(schedule.getTimeOnTypeInt());
        toUpdateSchedule.setTimeOff(schedule.getTimeOff());
        toUpdateSchedule.setTimeTypeOff(schedule.getTimeOffTypeInt());
        toUpdateSchedule.setBurningMinutes(schedule.getBurningMinsOn());

        this.scheduleRepository.save(toUpdateSchedule);
    }

}
