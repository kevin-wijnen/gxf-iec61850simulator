package com.cgi.iec61850serversimulator.functionclass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgi.iec61850serversimulator.dataclass.Device;
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

    public Scheduler(Device device) {
        this.executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

        // Should remove cancelled futures, still included .purge when calculating new
        // switching moments
        this.executor.setRemoveOnCancelPolicy(true);
        this.switchingMomentCalculator = new SwitchingMomentCalculator();
        this.device = device;
        this.scheduledFutures = new ArrayList<ScheduledFuture<?>>();
    }

    // Base it on feature/example-scheduling code! Triggering at certain
    // times instead of a continuous check!

    // TODO: Build switching moment calculation functionality
    // Read schedules from device's relays (1 * 4 * 50)
    // Checks on and off times along with burning minutes to see which switching
    // moments should be made for X hours
    // Scheduling said tasks by calculating the relative time and using said
    // relative time to set the task with ScheduledExecutorService from Java

    public void calculateTasks(Device device) throws SwitchingMomentCalculationException {
        // Steps:
        // Calculate the SwitchingMoments by the Calculator
        // Use schedulingTasks to schedule them
        logger.info("Creating switching moments out of schedules...");
        List<SwitchingMoment> switchingMoments = this.switchingMomentCalculator.returnSwitchingMoments(device);
        logger.info("Switching moments created! Scheduling switching moments...");
        this.schedulingTasks(switchingMoments);
    }

    public void schedulingTasks(List<SwitchingMoment> switchingMoments) {
        // Check future if it is empty. If it is not empty, then cancel > purge > clear
        // it.
        // Calculate relative time for each task
        // Schedule it, with future put into list
        this.clearScheduledSwitchingMoments(this.scheduledFutures);
        for (int i = 0; i < switchingMoments.size(); i++) {
            SwitchingMoment switchingMoment = switchingMoments.get(i);
            LocalDateTime currentTime = LocalDateTime.now();

            // Calculate relative time
            int relativeTime = TimeCalculator.calculateSecondsUntil(currentTime, switchingMoment.getTriggerTime());

            if (relativeTime > 0) {
                int relayNr = switchingMoment.getRelayNr();
                if (switchingMoment.isTriggerAction()) {
                    Runnable runOn = this.onRunnableCreator(relayNr);
                    this.scheduledFutures.add(this.executor.schedule(runOn, relativeTime, TimeUnit.SECONDS));
                    logger.info("Switching Moment for On action created! Relative time: {} ", relativeTime);
                } else {
                    Runnable runOff = this.offRunnableCreator(relayNr);
                    this.scheduledFutures.add(this.executor.schedule(runOff, relativeTime, TimeUnit.SECONDS));
                    logger.info("Switching Moment for Off action created! Relative time: {} ", relativeTime);
                }
            }
        }
        logger.info("Schedules planned!");
    }

    // Runnable to turn on relay light
    private Runnable onRunnableCreator(final int relayNr) {
        Runnable onRun = new Runnable() {
            @Override
            public void run() {
                Scheduler.this.device.getRelay(relayNr).setLight(true);
            }
        };
        return onRun;
    }

    // Runnable to turn off relay light
    private Runnable offRunnableCreator(final int relayNr) {
        Runnable offRun = new Runnable() {
            @Override
            public void run() {
                Scheduler.this.device.getRelay(relayNr).setLight(false);
            }
        };
        return offRun;
    }

    private void clearScheduledSwitchingMoments(List<ScheduledFuture<?>> scheduledSwitchingMoments) {
        if (!scheduledSwitchingMoments.isEmpty()) {
            for (int i = 0; i < scheduledSwitchingMoments.size(); i++) {
                ScheduledFuture<?> future = scheduledSwitchingMoments.get(i);
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
