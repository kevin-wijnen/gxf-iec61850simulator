package com.cgi.iec61850serversimulator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cgi.iec61850serversimulator.dataclass.Device;
import com.cgi.iec61850serversimulator.dataclass.SwitchingMoment;
import com.cgi.iec61850serversimulator.functionclass.Scheduler;
import com.cgi.iec61850serversimulator.functionclass.SwitchingMomentCalculationException;
import com.cgi.iec61850serversimulator.functionclass.SwitchingMomentCalculator;

@ExtendWith(MockitoExtension.class)
public class SchedulerTest {

    @Mock
    SwitchingMomentCalculator calculator;

    @Mock
    ScheduledThreadPoolExecutor executor;

    @Test
    void scheduleTaskTest() throws SwitchingMomentCalculationException {
        // Part 1: create a switching moment
        final int relayNr = 2;
        final LocalDateTime switchingTime = LocalDateTime.of(2021, 4, 11, 22, 27);
        final boolean triggerAction = true;
        final SwitchingMoment switchingMoment = new SwitchingMoment(relayNr, switchingTime, triggerAction);

        // Part 2: make a list of switching moments
        final List<SwitchingMoment> switchingMoments = new ArrayList<>();
        switchingMoments.add(switchingMoment);

        // Part 3: create a schedule and put a mock (= fake) calculator and
        // executor in it
        final Device device = null;
        final Scheduler scheduler = new Scheduler(device, this.calculator, this.executor);

        // Tell the make calculator what to return
        Mockito.when(this.calculator.returnSwitchingMoments(Mockito.any(), Mockito.any())).thenReturn(switchingMoments);

        // Calculate the switching moments
        final LocalDateTime scheduleForDateTime = LocalDateTime.of(2021, 4, 11, 21, 00);
        // Note: an additional parameter was added to the original
        // "calculateTasks" method, to make testing easier.
        scheduler.calculateTasksForDateTime(device, scheduleForDateTime);

        // The actual check: did the calculation trigger exactly one call to the
        // executor? And were the expected arguments passed to the executor?
        //
        // Note: the new scheduleForDateTime parameter of
        // "calculateTasksForDateTime" enables us to calculate exactly how many
        // seconds in the future the switching moment should happen.
        final long expectedSwitchingAfterSeconds = 1 * 3600 + 27 * 60;
        Mockito.verify(this.executor, Mockito.times(1))
                .schedule(Mockito.any(Runnable.class), Mockito.eq(expectedSwitchingAfterSeconds),
                        Mockito.eq(TimeUnit.SECONDS));
    }

}
