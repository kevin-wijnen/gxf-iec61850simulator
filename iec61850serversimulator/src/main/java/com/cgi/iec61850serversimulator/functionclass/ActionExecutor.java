package com.cgi.iec61850serversimulator.functionclass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.ServerSap;
import com.beanit.openiec61850.internal.cli.ActionException;
import com.beanit.openiec61850.internal.cli.ActionListener;
import com.cgi.iec61850serversimulator.dataclass.Device;

/**
 * Class which takes certain button inputs for debug features during runtime.
 */

public class ActionExecutor implements ActionListener {

    private static final Logger logger = LoggerFactory.getLogger(ServerSimulator.class);
    private static final String PRINT_SERVER_MODEL_KEY = "p";
    private static final String DEVICE_SHOW_MODEL = "d";
    private ServerSap serverSap = null;
    private Device device = null;
    private Scheduler scheduler = null;

    public ActionExecutor(final ServerSap serverSap, final Device device, Scheduler scheduler) {
        this.serverSap = serverSap;
        this.device = device;
        this.scheduler = scheduler;
    }

    @Override
    public void actionCalled(final String actionKey) throws ActionException {
        try {
            switch (actionKey) {
            case PRINT_SERVER_MODEL_KEY:
                logger.info("** Printing model.");
                logger.info("Server model:" + this.serverSap.getModelCopy());
                break;

            case DEVICE_SHOW_MODEL:
                logger.info(this.device.toString());

                break;
            }
        } catch (final Exception e) {
            throw new ActionException(e);
        }
    }

    @Override
    public void quit() {
        logger.info("Shutting down application...");
        this.serverSap.stop();
        this.scheduler.shutdownScheduler();
        return;

    }

}
