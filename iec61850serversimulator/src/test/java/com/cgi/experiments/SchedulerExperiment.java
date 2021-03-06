package com.cgi.experiments;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

public class SchedulerExperiment {
	// ScheduledExecutorService schedulerService =
	// Executors.newSingleThreadScheduledExecutor();
	ScheduledThreadPoolExecutor schedulerService = new ScheduledThreadPoolExecutor(1);
	List<ScheduledFuture> futureList = new ArrayList<ScheduledFuture>();

	// @Test
	public void scheduleSingleTask() {
		int relayNr = 1;

		try {
			ScheduledFuture<?> resultFuture = this.schedulerService.schedule(new onTask(relayNr), 1, TimeUnit.SECONDS);
			System.out.println("Test");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// @Test
	public void scheduleMultipleTasks() {

		int timeDelay = 5;

		for (int i = 0; i < 3; i++) {
			ScheduledFuture<?> resultFuture = this.schedulerService.schedule(new onTask(i + 1), 2 + i,
					TimeUnit.SECONDS);
			this.futureList.add(resultFuture);
		}

		for (int i = 0; i < 3; i++) {
			ScheduledFuture<?> resultFuture = this.schedulerService.schedule(new offTask(i + 1), 4 + i,
					TimeUnit.SECONDS);
			this.futureList.add(resultFuture);
		}
		// Delay set to go after the scheduled tasks
		try {
			Thread.sleep(10000);
			System.out.println(this.futureList.toString());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	// @Test
	public void checkDoneStatus() {

		int timeDelay = 10;

		for (int i = 0; i < 3; i++) {
			ScheduledFuture<?> resultFuture = this.schedulerService.schedule(new onTask(i + 1), 2 + i,
					TimeUnit.SECONDS);
			this.futureList.add(resultFuture);
			assertFalse(resultFuture.isDone());
		}

		try {
			Thread.sleep(10000);
			for (int i = 0; i < this.futureList.size(); i++) {
				assertTrue(this.futureList.get(i).isDone());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void queuePurge() {
		int timeDelay = 10;
		// Steps @ EventDataListener modified:
		// Check through every task to get it cancelled
		// Purge queue after tasks are cancelled (needed before purging)
		//

		for (int i = 0; i < 3; i++) {
			ScheduledFuture<?> resultFuture = this.schedulerService.schedule(new onTask(i + 1), 2 + i,
					TimeUnit.SECONDS);
			this.futureList.add(resultFuture);
			assertFalse(resultFuture.isDone());
		}
		try {
			Thread.sleep(3000);
			this.schedulerService.purge();

		} catch (Exception e) {
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
