package com.cgi.experiments;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SwitchCaseBdaTest {
    // Test for using Switch instead of many if-else's
    String[] bdaTestNames = new String[] { "boolean1", "boolean2", "int", "testString", "thisIsInvalid" };
    String[] bdaTestValues = new String[] { "true", "false", "0", "helloWorld" };

    @Test
    void SwitchCaseTest() {

        for (final String bda : this.bdaTestNames) {
            final String bdaTestName = bda;

            switch (bdaTestName) {
            case "boolean1":
                System.out.println(bdaTestName + " found!");
                System.out.println(this.bdaTestValues[0]);
                assertEquals("boolean1", bdaTestName);
                break;

            case "boolean2":
                System.out.println(bdaTestName + " found!");
                System.out.println(this.bdaTestValues[1]);
                assertEquals("boolean2", bdaTestName);
                break;

            case "int":
                System.out.println(bdaTestName + " found!");
                System.out.println(this.bdaTestValues[2]);
                assertEquals("int", bdaTestName);
                break;

            case "testString":
                System.out.println(bdaTestName + " found!");
                System.out.println(this.bdaTestValues[3]);
                assertEquals("testString", bdaTestName);
                break;

            default:
                System.out.println(bdaTestName + " not found.");
                assertEquals("thisIsInvalid", bdaTestName);
                break;
            }

        }
    }

}
