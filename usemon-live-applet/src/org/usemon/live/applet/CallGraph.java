package org.usemon.live.applet;

import java.util.Map;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.usemon.live.applet.edges.InvocationEdge;
import org.usemon.live.applet.nodes.BeanNode;
import org.usemon.live.applet.nodes.HttpNode;
import org.usemon.live.applet.nodes.QueueNode;
import org.usemon.live.data.FactListener;
import org.usemon.live.data.FactService;
import org.usemon.live.data.LiveListener;
import org.usemon.live.data.LiveService;

import processing.core.PApplet;
import processing.core.PImage;

public class CallGraph implements LiveListener, FactListener {
	private Graph graph;
	private PApplet ctx;
	private Node currentStatNode;
	private Node oldStatNode;
	private LiveService liveService;
	private FactService factService;
	private int factsOrdered;
	private PImage logo;
	
	public CallGraph(PApplet ctx) {
		this.ctx = ctx;
		logo = ctx.loadImage("logo.png");
		graph = new Graph(ctx);
		liveService = new LiveService(this);
		factService = new FactService(this);
	}
	
	public void tick() {
		graph.tick();
		if(graph.getMouseSelectedNode()!=null) {
			currentStatNode = graph.getMouseSelectedNode();
		}
		if(currentStatNode!=oldStatNode) {
			oldStatNode = currentStatNode;
			if(factsOrdered==0) {
				factService.orderFacts(oldStatNode);
				factsOrdered=1;
			}
		}
	}
	
	public void draw() {
		graph.draw();
		ctx.image(logo, -logo.width/2, -logo.height/2, logo.width/2, logo.height/2);
		if(factsOrdered>0) {
			ctx.rectMode(PApplet.LEFT);
			ctx.textAlign(PApplet.CENTER);
			ctx.textSize(32);
			ctx.noStroke();
			ctx.fill(0, 255, 0);
			ctx.text("Fetching Facts..", graph.screenToWorldX(ctx.width/2), graph.screenToWorldY(64));
		}
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
		} else if(str.startsWith("queue://")) {
			return new QueueNode(metaData, str, str);
		} else {
			return new BeanNode(metaData, str, str);
		}
	}

	public void factsArrived(Node node, JFreeChart chart) {
		factsOrdered=0;
		if(chart!=null) {
			ChartFrame frame = new ChartFrame("Facts for the last 24 hours", chart);
			frame.pack();
			frame.setVisible(true);
			frame.setLocationRelativeTo(ctx);
		}
	}
}
	