

class GwtEnvTest extends GroovyTestCase {

	void testSomething() {
	
		GwtEnv g = new GwtEnv();
		File f = g.getJdkHome();
		assertTrue(f.exists());
		println f
		File jreDir = new File(f,"jre");
		assertTrue(jreDir.isDirectory() == true);
		assertTrue(g.getJavaCommand().isFile());
	}

}
