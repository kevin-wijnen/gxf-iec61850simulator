package com.cgi.iec61850serversimulator;

import java.util.List;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.ServerEventListener;
import com.beanit.openiec61850.ServerSap;
import com.beanit.openiec61850.ServiceError;

class EventListener implements ServerEventListener {

    @Override
    public void serverStoppedListening(ServerSap serverSap) {
      System.out.println("The SAP stopped listening");
    }

    @Override
    public List<ServiceError> write(List<BasicDataAttribute> bdas) {
      for (BasicDataAttribute bda : bdas) {
        System.out.println("got a write request: " + bda);
      }
      return null;
    }
  }
