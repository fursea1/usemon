/**
 * Launches the GWTCompiler as an external process.
 * TODO: Verify the directory and file names
 *
 * Author: Steinar Overbeck Cook
 */
 

//Path separator in class path
pathSep = System.getProperty('path.separator')
//Separates directory components
fileSep = System.getProperty('file.separator')
 
/** Retrieves the name of the directory where GWT has been installed.  
 * This should normally be specified in the local M2_HOME/conf/settings.xml file
 */
String getGwtHome() {
	def gwtHome = project.properties['google.webtoolkit.home']
	if (!gwtHome) {
		def msg = """
	Need to know where you have installed the GWT toolkit on this machine
	Should be specified in your \$M2_HOME/conf/settings.xml file:
	....
	<profile>
		<id>gwt</id>
		<properties>
			<!-- Installation directory of GWT on this machine -->
			<google.webtoolkit.home>C:/java/gwt-windows-1.4.60</google.webtoolkit.home>
			<google.webtoolkit.extrajvmargs></google.webtoolkit.extrajvmargs>
		</properties>
	</profile>
	....
		"""
		fail(msg)
	}
	return gwtHome
}

/** Computes the fully qualified name of the .jar file which is
 * OS dependant, i.e. gwt-dev-windows.jar
 */
String getOsDependantJar() {
	//	 Computes the name of the .jar file holding the compiler etc.
	//	 This is OS dependant
	def osSuffix = '';
	if ( System.properties['os.name'].toLowerCase() =~ /windows/ ) {
		osSuffix = 'windows'
	} else if (System.properties['os.name'].toLowerCase() =~ /ux/) {
		osSuffix = 'linux'
	}
	// TODO: implement the mac specific stuff
	osDevJar = getGwtHome() + fileSep + 'gwt-dev-' + osSuffix + '.jar'
	return osDevJar
}

/** Figures out the location of the M2 repository */
File getM2Repo() {
	String propertyValue = project.properties['m2.repo']
	if (!propertyValue)
		fail('Property m2.repo required on the groovy plugin')
		
	File m2Repo = new File(project.properties['m2.repo'])
	if (!m2Repo.isDirectory()) {
		fail('Property m2.repo=${m2Repo} does not reference a directory')
	}
	return m2Repo
}


/** Retrieves a list of all the dependencies declared in the pom.xml */
List getDependencies() {
	
	def dependencies = []
	
	project.dependencies.each {

		// This is not really needed, but it reduces the size of the class path
		skip=false
		if (it.artifactId =~ /jfree/)	skip=true;
		else if (it.artifactId =~ /mock/)	skip=true;
		else if (it.artifactId =~ /jcommon/) skip=true
		else if (it.artifactId =~ /logback/)  skip= true
		else if (it.artifactId =~ /junit/) skip=true
		else if (it.artifactId =~/usemon-service/) skip=true
		else if (it.artifactId =~/gwt-servlet/) skip=true
		
		if (!skip) {
			String groupDir = it.groupId.replace('.','/')
			String artifactDir = it.artifactId + '/' + it.version + '/' + it.artifactId + '-' + it.version + '.' + it.type
			String path = groupDir + '/' + artifactDir
			File artifactFile = new File(getM2Repo(), path)
			assert artifactFile.isFile() == true
		
			dependencies << artifactFile
		}
	}
	return dependencies
}

/** Creates list of all entries required in the class path required for GWTCompiler */
List getDependenciesForGwt() {
	def sourceDir = pom.build.sourceDirectory
	// Combines the various elements into a single list
	return [ sourceDir, getOsDependantJar() ] + getDependencies()
}

/** Constructs the complete stringified class path including
 * path separators etc.
 */
String getClassPath() {
	//	 Composes the java class path
	def cp = getDependenciesForGwt()
	def classPath = cp.join(pathSep)
	
	return classPath
}

/** Retrieves the build output directory from the pom.xml */
String getOutputDirectory() {
	return pom.build.directory
}

println """
Compiling with GWTCompile
Source directory is: ${pom.build.sourceDirectory}
M2 repository      : ${m2Repo}
GWT installation directory: ${gwtHome} 
"""

	
// Obtains a reference to the JDK version of the java runtime
// The JRE will not suffice
GwtEnv gwtEnv = new GwtEnv()
def javaCmd = gwtEnv.getJavaCommand().getCanonicalPath()

// Constructs the list of arguments required in order to create a process
def procCmd = [javaCmd, '-cp', getClassPath(), 'com.google.gwt.dev.GWTCompiler','-out', getOutputDirectory(), 'org.usemon.gui.UsemonApp']
print "CMD:\n$procCmd\n"
// Launches the compiler
proc = procCmd.execute()

// Prints the results
println proc.text
