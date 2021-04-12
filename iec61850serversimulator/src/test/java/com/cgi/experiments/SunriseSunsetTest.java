package com.cgi.experiments;

import java.util.Calendar;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

public class SunriseSunsetTest {
    private static final Logger logger = LoggerFactory.getLogger(SunriseSunsetTest.class);

    Location location = new Location("50.84843695359427", "5.706033354270645");
    SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(this.location, "Europe/Amsterdam");

    @Test
    public void checkSunriseSunset() {
        String sunset = this.calculator.getOfficialSunriseForDate(Calendar.getInstance());
        String sunrise = this.calculator.getOfficialSunsetForDate(Calendar.getInstance());
        Calendar calender = Calendar.getInstance();

        logger.info(calender.getTime().toGMTString());
        logger.info(sunset);
        logger.info(sunrise);
    }
}
