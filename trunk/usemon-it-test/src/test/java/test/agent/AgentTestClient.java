package test.agent;

import java.util.Random;

public class AgentTestClient {

	private static Random rnd;

	public static void main(String[] args) throws InterruptedException {
		rnd = new Random();
		for(int n=0;n<5000;n++) {
			doFirstBigThing();
		}
		System.out.println("Ende");
	}

	public static void doFirstBigThing() throws InterruptedException {
		Thread.sleep((long) (rnd.nextDouble()*140));
		doSecondBigThing();
		Thread.sleep((long) (rnd.nextDouble()*60));
		doSecondThirdThing();
	}

	public static void doSecondBigThing() throws InterruptedException {
		Thread.sleep((long) (rnd.nextDouble()*125));
	}

	public static void doSecondThirdThing() throws InterruptedException {
		Thread.sleep((long) (rnd.nextDouble()*100));
	}

}