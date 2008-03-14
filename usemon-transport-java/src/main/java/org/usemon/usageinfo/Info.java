package org.usemon.usageinfo;

import java.io.Serializable;

/**
 * Holds information about object methods which are similar for each invocation.
 * 
 * @author Paul Rene Jørgensen
 *
 */
public abstract class Info implements Serializable{
	private static final long serialVersionUID = -2334112916099623206L;
	
	public static final int COMPONENT_UNKNOWN = 0x0;
	public static final int COMPONENT_SESSIONBEAN = 0x1;
	public static final int COMPONENT_ENTITYBEAN = 0x2;
    public static final int COMPONENT_MESSAGEDRIVENBEAN = 0x3;
    public static final int COMPONENT_SERVLET = 0x4;
    public static final int COMPONENT_SINGLETON = 0x5;
    public static final int COMPONENT_DATASOURCE = 0x6;
    public static final int COMPONENT_CUSTOM = 0x7;
    public static final int COMPONENT_QUEUESENDER = 0x8;
    public static final int COMPONENT_TOPICPUBLISHER = 0x9;
    public static final int COMPONENT_SQLSTATEMENT = 0xA;
	public static final int COMPONENT_SQLCONNECTION = 0xB;
    public static final String[] TYPES = new String[] { "Unknown", "SessionBean", "EntityBean", "MessageDrivenBean", "Servlet", "Singleton", "DataSource", "Custom", "QueueSender", "TopicPublisher", "SQLStatement", "SQLConnection" };
	
	protected int componentType;
	protected int count;
	
	public Info() {
	}
	
	public Info(int componentType) {
		this();
		this.componentType = componentType;
	}
	
	public abstract String getKey();
	
	public int getComponentType() {
		return componentType;
	}
	
	public int hashCode() {
		return System.identityHashCode(getKey());
	}

	public void incCount() {
		count++;
	}
	
	public int getCount() {
		return count;
	}

	public void setComponentType(int componentType) {
		this.componentType = componentType;
	}

	public void setCount(int count) {
		this.count = count;
	}

}