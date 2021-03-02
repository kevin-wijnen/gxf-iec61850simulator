package com.cgi.experiments;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

public class SchedulerExperiment {
	ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	Runnable runnableTask = () -> {
		try {
			System.out.println("Test");
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	@Test
	public void scheduleTask() {
		Future<?> resultFuture = this.executor.scheduleWithFixedDelay(this.runnableTask, 0, 5, TimeUnit.SECONDS);
	}
}
