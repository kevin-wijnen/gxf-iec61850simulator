package com.cgi.iec61850serversimulator;

import com.beanit.openiec61850.BasicDataAttribute;

public final class BdaProcessorFactory {

    private BdaProcessorFactory() {

    }

    public static BdaProcessor createProcessorFrom(final Device device, final BasicDataAttribute bda) {
        if ("tZ".equals(bda.getName())) {
            return new BdaTimeZoneProcessor(device, bda);
        } else {
            return null;
        }
    }

}
