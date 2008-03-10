package org.usemon.live.applet;

import java.util.Map;

import org.usemon.live.data.DataService;
import org.usemon.live.data.InvocationListener;

import processing.core.PApplet;

/**
 * U s e m o n | L i v e
 */
public class UsemonLiveApplet extends PApplet implements InvocationListener {
	private static final long serialVersionUID = -8335379399499404695L;
	private CallGraph graph;
	private DataService dataService;

	public void setup() {
		if (frame != null)
			frame.setTitle("U s e m o n | L i v e");
//		size(1024, 768, JAVA2D);
		size(screen.width, screen.height, JAVA2D);
		dataService = new DataService(this);
		graph = new CallGraph(this, dataService);
		frameRate(30);
		smooth();
		addMouseWheelListener(new java.awt.event.MouseWheelListener() {
			public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
				mouseWheel(evt.getWheelRotation());
			}
		});
	}

	protected void mouseWheel(int wheelRotationDelta) {
		graph.zoom((wheelRotationDelta*-1)/10.0f);
	}

	public void draw() {
		graph.tick();
		graph.draw();
	}

	public void addInvocation(String source, String target, int invocationCount, Map sourceMetaData, Map targetMetaData) {
		graph.addInvocation(source, target, invocationCount, sourceMetaData, targetMetaData);
	}

	public void keyPressed() {
		/*
		 * if (key == ' ') { Node n = graph.addNode(new EjbNode("", "Test"+random(0))); if(graph.size()>1) { Node t = (Node) graph.getNode((int) random(0, graph.size()-1)); graph.addEdge(new InvocationEdge(n, t, (int) random(1, 100))); } return; }
		 */
	}

	static public void main(String args[]) {
		//PApplet.main(new String[] { "--present", "org.usemon.live.applet.UsemonLiveApplet" });
		PApplet.main(new String[] { "org.usemon.live.applet.UsemonLiveApplet" });
	}
}