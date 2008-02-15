/**
 * Created 14. nov.. 2007 17.42.16 by Steinar Overbeck Cook
 */
package org.usemon.domain.graph;

import java.io.IOException;
import java.io.Writer;


/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class GMLWriterVisitor implements GraphVisitor {

	public GMLWriterVisitor() {}
	
	public void generateMarkup(Writer writer, Graph graph) {
		try {
			writer.append((CharSequence) graph.accept(this));
		} catch (IOException e) {
			throw new IllegalStateException("Error while writing graph; " + e.getMessage(),e);
		}
	}
	
	public Object visit(Edge edge) {
		StringBuilder sb = new StringBuilder();
		sb.append("\t\nedge [");
		sb.append("    source "+edge.getSource().getUid());		
		sb.append("    target "+edge.getTarget().getUid());		
		sb.append("    graphics [");
		sb.append("      fill \"#000000\"");		
		sb.append("      targetArrow \"standard\"");		
		sb.append("    ]");		
		sb.append("  ]");
		return sb;
	}

	public Object visit(Graph graph) {
		StringBuilder sb = new StringBuilder("Creator \"Usemon\"\n");
		sb.append("Version \"2.0\"\n");
		sb.append("graph [");
		sb.append("\t\nhierarchic 1");
		sb.append("\t\nlabel \"\"");
		sb.append("\t\ndirected 1");
		
		for (Node node : graph.getNodes()) {
			sb.append( node.accept(this));
		}
		
		for (Edge edge : graph.getEdges()) {
			sb.append( edge.accept(this));
		}
		
		sb.append("]\n");
		return sb;
	}

	public Object visit(Node node) {
		StringBuilder sb = new StringBuilder();
		sb.append("\t\nnode [\n");
		sb.append("\t\t\nid ")
			.append(node.getUid());
		sb.append("\t\t\nlabel \""
				+ node.getUid()
				+"\"");
		sb.append("\t\t\ngraphics [");
		sb.append("\t\t\t\ntype \"rectangle\"");
		sb.append("\t\t\t\nfill \"#FF0000\"");
		sb.append("\t\t\t\noutline \"#000000\"");
		sb.append("\t\t\n]");
		sb.append("    labelgraphics [");
		sb.append("      text \""
				+ node.getUid()
				+"\"");
		sb.append("      fontSize 13");
		sb.append("      fontName \"Dialog\"");
		sb.append("      anchor \"c\"");
		sb.append("    ]");
		sb.append("  ]");
		return sb;
	}

	public Object visit(InstanceNode node) {
		StringBuilder sb = new StringBuilder();
		sb.append("\t\nnode [\n");
		sb.append("\t\t\nid ")
			.append(node.getUid());
		
		StringBuilder nodeLabel = new StringBuilder("");
		if (node.getInstanceName() != null )
			nodeLabel.append(node.getInstanceName()).append("\n");
		nodeLabel.append(':').append(node.getClassName());
		
		sb.append("\t\t\nlabel \""
				+ nodeLabel
				+"\"");
		sb.append("\t\t\ngraphics [");
		sb.append("\t\t\t\ntype \"rectangle\"");
		sb.append("\t\t\t\nfill \"#FF0000\"");
		sb.append("\t\t\t\noutline \"#000000\"");
		sb.append("\t\t\n]");
		sb.append("    labelgraphics [");
		sb.append("      text \""
				+ nodeLabel
				+"\"");
		sb.append("      fontSize 13");
		sb.append("      fontName \"Dialog\"");
		sb.append("      anchor \"c\"");
		sb.append("    ]");
		sb.append("  ]");
		return sb;	}

}
