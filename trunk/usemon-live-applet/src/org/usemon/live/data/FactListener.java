package org.usemon.live.data;

import org.jfree.chart.JFreeChart;
import org.usemon.live.applet.Node;

public interface FactListener {

	public void factsArrived(Node node, JFreeChart chart);

}
