package com.cgi.iec61850serversimulator.functionclass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgi.iec61850serversimulator.dataclass.Relay;
import com.cgi.iec61850serversimulator.datamodel.RelayEntity;
import com.cgi.iec61850serversimulator.datarepository.RelayRepository;

public class DatabaseUtils {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);

    private RelayRepository relayRepository;

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
        // Create Bda of CtlVal (only needed to sync up with, relayNr etc. is
        // explanatory)
        logger.info("Updating model relay from database: Not implemented yet");

    }

    public void updateDatabaseRelay(Relay relay) {
        // Update relay entity in database
        int index_number = relay.getIndexNumber();
        boolean light_status = relay.getLight();
        this.relayRepository.save(new RelayEntity(index_number, light_status));
    }

}
