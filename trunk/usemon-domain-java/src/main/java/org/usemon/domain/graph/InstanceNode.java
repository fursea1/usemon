/**
 * Created 15. nov.. 2007 16.53.59 by Steinar Overbeck Cook
 */
package org.usemon.domain.graph;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class InstanceNode extends Node {

	private String packageName;
	private String className;
	private String instanceName;
	
	/**
	 * 
	 */
	public InstanceNode() {
	}

	public InstanceNode(String packageName, String className, String instanceName) {
		this((Long)null, packageName, className, instanceName);
	}
	
	public InstanceNode(Long uid, String packageName, String className, String instanceName) {
		super(uid);
		this.className = className;
		this.packageName = packageName;
		this.instanceName = instanceName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	public Object accept(GraphVisitor visitor) {
		return visitor.visit(this);
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((instanceName == null) ? 0 : instanceName.hashCode());
		result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof InstanceNode))
			return false;
		final InstanceNode other = (InstanceNode) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (instanceName == null) {
			if (other.instanceName != null)
				return false;
		} else if (!instanceName.equals(other.instanceName))
			return false;
		if (packageName == null) {
			if (other.packageName != null)
				return false;
		} else if (!packageName.equals(other.packageName))
			return false;
		return true;
	}
}
