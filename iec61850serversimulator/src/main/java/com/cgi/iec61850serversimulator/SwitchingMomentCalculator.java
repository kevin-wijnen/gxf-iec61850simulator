package com.cgi.iec61850serversimulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchingMomentCalculator implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SwitchingMomentCalculator.class);

    private Device device;

    public SwitchingMomentCalculator(final Device device) {
        this.device = device;
    }

    @Override
    public void run() {
        logger.info("Geen nieuwe wijzigingen ontvangen gedurende de laatste paar"
                + " seconden. De client lijkt dus alle schemawijzigingen weggeschreven te hebben.");
        logger.info(
                "Bereken hier de schakelmomenten voor de komende .. uur van device '{}' en schedule acties voor de berekende tijdstippen",
                this.device.toString());
    }

}
