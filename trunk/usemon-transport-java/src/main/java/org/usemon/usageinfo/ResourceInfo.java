package org.usemon.usageinfo;

public class ResourceInfo extends Info {
	private static final long serialVersionUID = -6463534766557064504L+1L; // Inced version because className field was added (PRJ)

	private String resourceUri;
	private String className;
	
	public ResourceInfo(int componentType, String className, String resourceUri) {
		super(componentType);
		this.className = className;
		this.resourceUri = resourceUri;
	}
	
	public String getResourceUri() {
		return resourceUri;
	}

	public String getClassName() {
		return className;
	}

	public String getKey() {
		return createKey(resourceUri);
	}
	
	public static String createKey(String resourceUri) {
		return resourceUri;
	}
	
	public String toString() {
		return "["+TYPES[componentType]+"] "+resourceUri+" ("+className+")";
	}

}
