package com.cgi.experiments;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

public class SchedulerExperiment {
	ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	List<ScheduledFuture> futureList = new ArrayList<ScheduledFuture>();

	public void scheduleSingleTask() {
		// TODO: Fix output? No task being run.
		int relayNr = 1;

		try {
			ScheduledFuture<?> resultFuture = this.executor.schedule(new onTask(relayNr), 1, TimeUnit.SECONDS);
			System.out.println("Test");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void scheduleMultipleTasks() {

		int timeDelay = 5;
		for (int i = 0; i < 3; i++) {
			ScheduledFuture<?> resultFuture = this.executor.schedule(new offTask(i + 1), 3 + i, TimeUnit.SECONDS);
			this.futureList.add(resultFuture);
		}
		for (int i = 0; i < 3; i++) {
			ScheduledFuture<?> resultFuture = this.executor.schedule(new onTask(i + 1), 2 + i, TimeUnit.SECONDS);
			this.futureList.add(resultFuture);
		}

		// Delay set to go after the scheduled tasks
		try {
			Thread.sleep(20000);
			System.out.println(this.futureList.toString());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private class onTask implements Runnable {

		private int relayNr;

		public onTask(int relayNr) {
			this.relayNr = relayNr;

		}

		@Override
		public void run() {
			try {
				System.out.println(LocalTime.now() + " - Test - Aanzetten van relay: " + this.relayNr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class offTask implements Runnable {

		private int relayNr;

		public offTask(int relayNr) {
			this.relayNr = relayNr;
		}

		@Override
		public void run() {
			try {
				System.out.println(LocalTime.now() + " - Test - Uitzetten van relay: " + this.relayNr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
