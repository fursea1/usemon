/**
 * 
 */
package org.usemon.domain;

/**
 * @author t514257 Class describing observations made on a JVM
 *         (heap)
 */
public class HeapObservation extends AbstractObservation {
	
	// TODO: Rename class to appropriate name and complete the implementation
	
	// Facts
	private Long free;
	private Long total;
	private Long maxMem;

	public Long getMaxMem() {
		return maxMem;
	}

	public void setMaxMem(Long maxMem) {
		this.maxMem = maxMem;
	}

	/** {@inheritDoc}
	 * 
	 * @see org.usemon.domain.Observation#getTypeName()
	 */
	public String getTypeName() {
		return ObservationType.HEAP.name();
	}

	public Long getFree() {
		return free;
	}

	public void setFree(Long free) {
		this.free = free;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

}
