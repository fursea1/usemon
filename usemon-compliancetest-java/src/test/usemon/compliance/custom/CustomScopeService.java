package test.usemon.compliance.custom;

import java.util.Random;

public class CustomScopeService {
	
	private static final Random rnd = new Random();

	public String sayHello(String name) throws InterruptedException {
		Thread.sleep(rnd.nextInt(250));
		return "Hello, "+name;
	}

}
