package org.usemon.stresstest.agent;

import java.util.Random;


public class SleepExperiment {

	private static Random rnd;

//	@org.junit.Test
	public void mainTest() throws InterruptedException {
		rnd = new Random();
		for(int n=0;n<5000;n++) {
			a();
		}
		System.out.println("Ende");
	}

	public static void a() throws InterruptedException {
		Thread.sleep((long) (rnd.nextDouble()*140));
		b();
		Thread.sleep((long) (rnd.nextDouble()*60));
		c();
	}

	public static void b() throws InterruptedException {
		Thread.sleep((long) (rnd.nextDouble()*125));
	}

	public static void c() throws InterruptedException {
		Thread.sleep((long) (rnd.nextDouble()*100));
	}

}
