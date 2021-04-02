package com.cgi.iec61850serversimulator.functionclass;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBoolean;
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
        boolean found = this.scheduleRepository.existsByIndexNumber(schedule.getIndexNumber());

        if (found) {
            logger.info("Schedule found in database. Updating model. NOT IMPLEMENTED AS OF NOW.");
            // this.updateModelSchedule(schedule);
        } else {
            logger.info("Schedule not found in database, add it to database.");
            this.initializeDatabaseSchedule(schedule, relayNr);
        }
    }

    public void updateModelSchedule(Schedule schedule) {
        // Update relay model with BDA
        logger.info("Updating model schedule with database.");
        int scheduleNr = schedule.getIndexNumber();
        List<ScheduleEntity> databaseSchedule = this.scheduleRepository.findByIndexNumber(scheduleNr);

        // Creating BDAs for the values
        // final BdaBoolean modelRelayLightStatus = (BdaBoolean) this.serverWrapper
        // .findModelNode(SWITCH_ROOT + relayNr + ".Pos.Oper.ctlVal", Fc.CO);
        // modelRelayLightStatus.setValue(databaseRelay.get(0).isLightStatus());
        // List<BasicDataAttribute> bdas = new ArrayList<BasicDataAttribute>();
        // bdas.add(modelRelayLightStatus);

        // Update Schedule object
        // schedule.setLight(databaseRelay.get(0).isLightStatus());

        // Send model update
        // this.serverWrapper.setValues(bdas);

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

        // ScheduleEntity toUpdateSchedule = new ScheduleEntity(index_number,
        // index_number, index_number, index_number, index_number, index_number,
        // index_number, index_number, false)
        // RelayEntity toUpdateRelay = new RelayEntity(index_number, light_status);
        // this.relayRepository.save(toUpdateRelay);
    }

    public void updateDatabaseSchedule(Relay relay) {
        // Updating database relay

        int index_number = relay.getIndexNumber();
        boolean light_status = relay.getLight();
        List<RelayEntity> relayEntities = this.relayRepository.findByIndexNumber(index_number);
        RelayEntity toUpdateRelay = relayEntities.get(0);
        toUpdateRelay.setLightStatus(light_status);
        this.relayRepository.save(toUpdateRelay);
    }

}
