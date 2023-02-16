package com.example.step4.octopus;

public class TimeUtil {

	private TimeUtil() {
	}

	public static void sleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
		}
	}
}
