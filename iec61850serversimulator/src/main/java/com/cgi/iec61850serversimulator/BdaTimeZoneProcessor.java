package com.cgi.iec61850serversimulator;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaInt16;

public class BdaTimeZoneProcessor implements BdaProcessor {

    private Device device;
    private BasicDataAttribute bda;

    public BdaTimeZoneProcessor(final Device device, final BasicDataAttribute bda) {
        this.device = device;
        this.bda = bda;
    }

    @Override
    public void process() {
        this.device.getClock().setTimeZoneOffset(((BdaInt16) this.bda).getValue());
    }

}
