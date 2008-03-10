package org.usemon.live.applet;

import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.usemon.live.applet.edges.InvocationEdge;
import org.usemon.live.applet.nodes.BeanNode;
import org.usemon.live.applet.nodes.HttpNode;
import org.usemon.live.data.DataService;

import processing.core.PApplet;

public class CallGraph {
	private Graph graph;
	private PApplet ctx;
	private Node currentStatNode;
	private Node oldStatNode;
	private DataService dataService;
	
	public CallGraph(PApplet ctx, DataService dataService) {
		graph = new Graph(ctx);
		this.dataService = dataService;
		this.ctx = ctx;
	}
	
	public void tick() {
		graph.tick();
		if(graph.getMouseSelectedNode()!=null) {
			currentStatNode = graph.getMouseSelectedNode();
		}
		if(currentStatNode!=oldStatNode) {
			oldStatNode = currentStatNode;
			TimeSeriesCollection tsc = dataService.getTest(oldStatNode);
			JFreeChart chart = ChartFactory.createTimeSeriesChart("", "", "", tsc, true, true, false);
			ChartFrame frame = new ChartFrame(oldStatNode.getUuid()+" statistics last 24 hours", chart);
			frame.pack();
			frame.setVisible(true);		
		}
	}
	
	public void draw() {
		graph.draw();
	}
	
	public void zoom(float delta) {
		graph.zoom(delta);
	}

	public void addInvocation(String source, String target, int invocationCount, Map sourceMetaData, Map targetMetaData) {
		Node s = graph.getNode(source);
		if(s==null) {
			s = graph.addNode(createNode(source, sourceMetaData));
			
		}
		Node t = graph.getNode(target);
		if(t==null) {
			t = graph.addNode(createNode(target, targetMetaData));
		}
		Edge e = graph.getEdge(source, target);
		if(e!=null) {
			if(e instanceof InvocationEdge) {
				((InvocationEdge) e).addInvocationCount(invocationCount);
			}
		} else {
			e = graph.addEdge(new InvocationEdge(s, t, invocationCount));
		}
	}

	private Node createNode(String str, Map metaData) {
		if(str.startsWith("http://") || str.startsWith("https://")) {
			return new HttpNode(metaData, str, str);
		} else {
			return new BeanNode(metaData, str, str);
		}
	}
}
