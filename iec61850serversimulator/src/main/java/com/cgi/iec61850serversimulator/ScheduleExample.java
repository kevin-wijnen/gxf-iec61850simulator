package com.cgi.iec61850serversimulator;

import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

public class ScheduleExample {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleExample.class);

    private static final int CALCULATION_DELAY = 3;
    private static int counter = 0;

    private TaskScheduler scheduler;
    private ScheduledFuture<?> future;

    public static void main(final String[] args) {

        logger.info("Start main");
        final ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
        final TaskScheduler newScheduler = new ConcurrentTaskScheduler(localExecutor);

        final Device someDevice = new Device();

        final ScheduleExample example = new ScheduleExample(newScheduler);

        example.scheduleSwitchingMomentCalculation(someDevice);
        example.scheduleSwitchingMomentCalculation(someDevice);

        logger.info("Einde main");
    }

    public ScheduleExample(final TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void scheduleSwitchingMomentCalculation(final Device someDevice) {

        counter++;

        logger.info(
                "Wijziging nummer {} ontvangen in het schema, wacht {} seconden en ga dan schakelmomenten berekenen.",
                counter, CALCULATION_DELAY);

        if (this.future != null && !this.future.isDone()) {
            logger.info("Er was recent een andere schemawijziging. "
                    + "Cancel de berekening daarvan, omdat het schema nu weer wordt gewijzigd.");
            this.future.cancel(true);
        }

        final SwitchingMomentCalculator calculator = new SwitchingMomentCalculator(someDevice);

        this.future = this.scheduler.schedule(calculator,
                ZonedDateTime.now().plusSeconds(CALCULATION_DELAY).toInstant());

    }

}
