package test;


public class ClTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(System.getProperties());
		new ClTest().arrr();
	}


	public void arrr() {
		for(int n=0;n<10;n++) {
			purr();
		}
	}
	
	public void purr() {
		System.out.println("Arr purr!");
	}
}
