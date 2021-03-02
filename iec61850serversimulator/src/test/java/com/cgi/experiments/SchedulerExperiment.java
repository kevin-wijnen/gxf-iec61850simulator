package com.cgi.experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

public class SchedulerExperiment {
	ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	List<Future> futureList = new ArrayList<Future>();
	Runnable runnableTask = () -> {
		try {
			System.out.println("Test");
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	/*
	 * @Test public void scheduleTask() { Future<?> resultFuture =
	 * this.executor.scheduleWithFixedDelay(this.runnableTask, 0, 5,
	 * TimeUnit.SECONDS); }
	 */

	@Test
	public void scheduleMultipleTasks() {
		// TODO: Fix output? No tasks being ran.

		int timeDelay = 5;
		for (int i = 0; i < 5; i++) {
			Future<?> resultFuture = this.executor.scheduleWithFixedDelay(this.runnableTask, 0, timeDelay + i,
					TimeUnit.SECONDS);
			this.futureList.add(resultFuture);
		}

		System.out.println(this.futureList.toString());

	}
}
