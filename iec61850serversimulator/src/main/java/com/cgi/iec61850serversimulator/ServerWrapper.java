package com.cgi.iec61850serversimulator;

import java.util.List;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.ServerSap;

/**
 * Class which wraps a ServerSap object.
 */
public class ServerWrapper {

    private ServerSap serverSap;

    public ServerWrapper(final ServerSap serverSap) {
        this.serverSap = serverSap;
    }

    public void setValues(final List<BasicDataAttribute> bdas) {
        this.serverSap.setValues(bdas);
    }

    public ModelNode findModelNode(final String objectReference, final Fc fc) {
        return this.serverSap.getModelCopy().findModelNode(objectReference, fc);
    }

}
