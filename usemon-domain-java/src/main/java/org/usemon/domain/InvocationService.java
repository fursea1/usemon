/**
 * Created 14. nov. 2007 16.56.03 by Steinar Overbeck Cook
 */
package org.usemon.domain;

import org.usemon.domain.graph.Graph;
import org.usemon.domain.graph.InvocationDetailLevel;

/**
 * Services related to analyzing method invocations, which are typically found
 * in the <code>invocation_fact</code> table in the database.
 * 
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public interface InvocationService {

	Graph loadGraph(InvocationDetailLevel invocationDetailLevel);
}
