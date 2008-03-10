package org.usemon.live.applet.edges;

import org.usemon.live.applet.Edge;
import org.usemon.live.applet.Graph;
import org.usemon.live.applet.Node;

import processing.core.PApplet;
import traer.physics.Vector3D;

public class InvocationEdge extends Edge {

	private int totalInvocationCount;
	private int invocationsLeftToAnimate;

	public InvocationEdge(Node source, Node target, int invocationCount) {
		super(source, target);
		addInvocationCount(invocationCount);
	}

	public void addInvocationCount(int invocationCount) {
		totalInvocationCount += invocationCount;
		invocationsLeftToAnimate += invocationCount;
	}

	public void tick() {
		if (invocationsLeftToAnimate > 0) {
			invocationsLeftToAnimate--;
			attention = true;
			// Prevent nodes from dying while we still are animating
			source.touch();
			target.touch();
		} else {
			attention = false;
		}
	}

	public void draw(PApplet g) {
		g.strokeWeight(2);
		float arrowPosition = 0.4f;
		if (invocationsLeftToAnimate > 0) {
			arrowPosition = (invocationsLeftToAnimate % 10) / 10.0f;
			g.stroke(0, 255, 0);
			/*
			 * if(invocationsLeftToAnimate % 2 == 0) { g.strokeWeight(2.5f); g.stroke(128, 255, 128); }
			 */
		} else {
			g.stroke(255, 255, 255);
		}

		float sx = source.getParticle().position().x();
		float sy = source.getParticle().position().y();
		float tx = target.getParticle().position().x();
		float ty = target.getParticle().position().y();
		g.beginShape(PApplet.LINES);
		g.vertex(sx, sy);
		g.vertex(tx, ty);
		g.endShape();

		g.strokeWeight(1.0f);
		float len = Graph.EDGE_LENGTH / 5;
		Vector3D s = source.getParticle().position();
		Vector3D t = target.getParticle().position();
		float angle = PApplet.degrees(angle(s, t));
		g.pushMatrix();
		g.translate(PApplet.lerp(t.x(), s.x(), arrowPosition), PApplet.lerp(t.y(), s.y(), arrowPosition));
		g.line(0, 0, PApplet.cos(PApplet.radians(angle + 35)) * len, PApplet.sin(PApplet.radians(angle + 35)) * len);
		g.line(0, 0, PApplet.cos(PApplet.radians(angle - 35)) * len, PApplet.sin(PApplet.radians(angle - 35)) * len);
		g.popMatrix();
	}

	private float angle(Vector3D s, Vector3D t) {
		float x = s.x() - t.x();
		float y = s.y() - t.y();
		return PApplet.atan2(y, x);
	}

}
