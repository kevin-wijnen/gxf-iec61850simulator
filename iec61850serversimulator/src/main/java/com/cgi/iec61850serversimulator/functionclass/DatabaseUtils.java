package com.cgi.iec61850serversimulator.functionclass;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.Fc;
import com.cgi.iec61850serversimulator.dataclass.Relay;
import com.cgi.iec61850serversimulator.datamodel.RelayEntity;
import com.cgi.iec61850serversimulator.datarepository.RelayRepository;

public class DatabaseUtils {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    private static final String SWITCH_ROOT = "SWDeviceGenericIO/XSWC";

    private RelayRepository relayRepository;
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
            this.updateDatabaseRelay(relay);
        }
    }

    public void updateModelRelay(Relay relay) {
        // Update relay model with BDA
        logger.info("Updating model relay  database.");
        int relayNr = relay.getIndexNumber();
        List<RelayEntity> databaseRelay = this.relayRepository.findByIndexNumber(relayNr);
        final BdaBoolean modelRelayLightStatus = (BdaBoolean) this.serverWrapper
                .findModelNode(SWITCH_ROOT + relayNr + ".Pos.Oper.ctlVal", Fc.CO);
        modelRelayLightStatus.setValue(databaseRelay.get(0).isLightStatus());
        List<BasicDataAttribute> bdas = new ArrayList<BasicDataAttribute>();
        bdas.add(modelRelayLightStatus);
        this.serverWrapper.setValues(bdas);
        relay.setLight(databaseRelay.get(0).isLightStatus());

    }

    public void updateDatabaseRelay(Relay relay) {
        // Update relay entity in database

        int index_number = relay.getIndexNumber();
        List<RelayEntity> relayEntities = this.relayRepository.findByIndexNumber(index_number);
        RelayEntity toUpdateRelay = relayEntities.get(0);
        boolean light_status = relay.getLight();
        toUpdateRelay.setLightStatus(light_status);
        this.relayRepository.save(toUpdateRelay);
    }

}
