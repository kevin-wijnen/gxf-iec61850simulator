package com.cgi.iec61850serversimulator.functionclass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgi.iec61850serversimulator.dataclass.Device;
import com.cgi.iec61850serversimulator.dataclass.Relay;
import com.cgi.iec61850serversimulator.dataclass.SwitchingMoment;

/**
 * Class which schedules autonomous switching moments for future activation.
 */
public class Scheduler {

    private ScheduledThreadPoolExecutor executor;
    private List<ScheduledFuture<?>> scheduledFutures;
    private SwitchingMomentCalculator switchingMomentCalculator;
    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);
    private Device device;
    private DatabaseUtils databaseUtils;

    public Scheduler(final Device device, final SwitchingMomentCalculator switchingMomentCalculator,
            final ScheduledThreadPoolExecutor executor, DatabaseUtils databaseUtils) {
        this.executor = executor;

        // Should remove cancelled futures, still included .purge when
        // calculating new switching moments
        this.executor.setRemoveOnCancelPolicy(true);

        this.switchingMomentCalculator = switchingMomentCalculator;
        this.device = device;
        this.scheduledFutures = new ArrayList<>();
        this.databaseUtils = databaseUtils;
    }

    // Base it on feature/example-scheduling code! Triggering at certain
    // times instead of a continuous check!

    // TODO: Build switching moment calculation functionality
    // Read schedules from device's relays (1 * 4 * 50)
    // Checks on and off times along with burning minutes to see which switching
    // moments should be made for X hours
    // Scheduling said tasks by calculating the relative time and using said
    // relative time to set the task with ScheduledExecutorService from Java

    public void calculateTasksForDateTime(final Device device, final LocalDateTime dateTime)
            throws SwitchingMomentCalculationException {
        // Steps:
        // Calculate the SwitchingMoments by the Calculator
        // Use schedulingTasks to schedule them
        logger.info("Creating switching moments out of schedules...");
        final List<SwitchingMoment> switchingMoments = this.switchingMomentCalculator.returnSwitchingMoments(device,
                dateTime);
        logger.info("Switching moments created! Scheduling switching moments...");
        this.schedulingTasks(switchingMoments, dateTime);
    }

    public void schedulingTasks(final List<SwitchingMoment> switchingMoments, final LocalDateTime dateTime) {
        // Check future if it is empty. If it is not empty, then cancel > purge
        // > clear it.
        // Calculate relative time for each task
        // Schedule it, with future put into list
        this.clearScheduledSwitchingMoments(this.scheduledFutures);
        for (int i = 0; i < switchingMoments.size(); i++) {
            final SwitchingMoment switchingMoment = switchingMoments.get(i);

            // Calculate relative time
            final int relativeTime = TimeCalculator.calculateSecondsUntil(dateTime, switchingMoment.getTriggerTime());

            if (relativeTime > 0) {
                final int relayNr = switchingMoment.getRelayNr();
                if (switchingMoment.isTriggerAction()) {
                    final Runnable runOn = this.switchRunnableCreator(relayNr, true);
                    this.scheduledFutures.add(this.executor.schedule(runOn, relativeTime, TimeUnit.SECONDS));
                    logger.info("Switching Moment for On action created! Relative time: {} ", relativeTime);
                } else {
                    final Runnable runOff = this.switchRunnableCreator(relayNr, false);
                    this.scheduledFutures.add(this.executor.schedule(runOff, relativeTime, TimeUnit.SECONDS));
                    logger.info("Switching Moment for Off action created! Relative time: {} ", relativeTime);
                }
            }
        }
        logger.info("Schedules planned!");
    }

    // Runnable to turn relay light on or off
    private Runnable switchRunnableCreator(final int relayNr, final boolean switchOn) {
        return () -> {
            logger.info("Switching relay {} to {}", relayNr, switchOn ? "on" : "off");
            Scheduler.this.device.getRelay(relayNr).setLight(switchOn);
            Relay updatedRelay = Scheduler.this.device.getRelay(relayNr);
            Scheduler.this.databaseUtils.updateDatabaseRelay(updatedRelay);
        };
    }

    private void clearScheduledSwitchingMoments(final List<ScheduledFuture<?>> scheduledSwitchingMoments) {
        if (!scheduledSwitchingMoments.isEmpty()) {
            for (int i = 0; i < scheduledSwitchingMoments.size(); i++) {
                final ScheduledFuture<?> future = scheduledSwitchingMoments.get(i);
                if (!future.isDone()) {
                    future.cancel(true);
                }
            }
            scheduledSwitchingMoments.clear();
            // To prevent memory leak: Purging executor's queue
            this.executor.purge();
        }
    }

    public void shutdownScheduler() {
        this.executor.shutdownNow();
    }
}
