package com.cgi.iec61850serversimulator;

import com.beanit.openiec61850.BdaInt16;
import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.ObjectReference;

public class BdaGenerator {
	// Methode aanmaken voor initialiseren van BdaInt16 met ObjectReference string,
	// Fc en de Value

	public BdaInt16 generateBdaInt16(String objectReferenceString, String fcString, short value) {
		ObjectReference objectReference = new ObjectReference(objectReferenceString);
		Fc fc = Fc.fromString(fcString);

		BdaInt16 bda = new BdaInt16(objectReference, fc, null, false, false);
		bda.setValue(value);

		return bda;
	}

}
