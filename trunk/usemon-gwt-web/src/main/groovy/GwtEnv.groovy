

class GwtEnv {

	File getJdkHome() {
		// Retreives the path of the JRE, not the JDK
		String javaHome = System.properties['java.home'];
		File f = new File(javaHome);
		f.getParentFile()
	}

	File getJavaCommand() {
		File jdk = new File(getJdkHome(),"bin"); 
		File java

		jdk.eachFileMatch(~/(java)|(java\.exe)/) { 
			java=it 
		}
		return java
	}

}
