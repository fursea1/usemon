/**
 * Created 14. nov.. 2007 18.36.15 by Steinar Overbeck Cook
 */
package org.usemon.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usemon.domain.InvocationService;
import org.usemon.domain.graph.Graph;
import org.usemon.domain.graph.InstanceNode;
import org.usemon.domain.graph.InvocationDetailLevel;
import org.usemon.domain.graph.InvocationEdge;

import com.google.inject.Inject;

/**
 * @author t547116 (Steinar Overbeck Cook)
 *
 */
public class InvocationServiceImpl implements InvocationService {

	Logger log = LoggerFactory.getLogger(InvocationServiceImpl.class);
	DataSource dataSource;
	
	@Inject
	public InvocationServiceImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	/** Loads a graph of all invocations from the database at the given 
	 * level of detail.
	 * 
	 * @see org.usemon.domain.InvocationService#loadGraph(org.usemon.domain.graph.InvocationDetailLevel)
	 */
	public Graph loadGraph(InvocationDetailLevel invocationDetailLevel) {
		Connection con = null;
		Graph graph = new Graph();
		try {
			String sql = createSql(invocationDetailLevel);
			con = dataSource.getConnection();
			log.debug("Executing sql:\n" + sql);
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			long nodeCounter=0;
			while (rs.next()) {
				// If the object instance is "unknown", use the numeric id of the object instance
				// otherwise use the numeric id of the class
				
				InstanceNode source = new InstanceNode(rs.getString("src_package"), rs.getString("src_class"), rs.getString("src_instance") );
				InstanceNode target = new InstanceNode(rs.getString("target_package"), rs.getString("target_class"), rs.getString("target_instance") );
				InvocationEdge edge = new InvocationEdge(rs.getLong("invocation_count"), source, target);
				graph.addEdge(edge);
				nodeCounter +=2;
			}
			log.info("Loaded " + nodeCounter + " nodes");
		} catch (Exception e) {
			throw new IllegalStateException("Unable to load graph data for invocation graph  " +e,e);
		} finally {
			DbmsHelper.close(con);
		}
		return graph;
	}

	private String createSql(InvocationDetailLevel invocationDetailLevel) {
		String sql = "select "
			+ "\n  src_instance, "
			+ "\n  src_package, "
			+ "\n  src_class, "
			+ "\n  target_instance, "
			+ "\n  target_package, "
			+ "\n  target_class, "
			+ "\n  sum(invocation_count) as \"invocation_count\" "
			+ "\nfrom "
			+ "\n  invocation_fact as i "
			+ "\n  join src_instance on i.src_instance_id=src_instance.id "
			+ "\n  join target_instance on i.target_instance_id=target_instance.id "
			+ "\n  join src_package on i.src_package_id=src_package.id "
			+ "\n  join target_package on i.target_package_id=target_package.id "
			+ "\n  join src_class on i.src_class_id=src_class.id "
			+ "\n  join target_class on i.target_class_id=target_class.id "
			+ "\n  join d_date on d_date.id=i.d_date_id "
			+ "\n  join location on location.id=i.location_id "
			+ "\nwhere "
			+ "\n  1=1 "
			+ "\n  /* and d_date.date_v between date_sub(curdate(), interval 3 day) and curdate() */ "
			+ "\n and src_class not like 'Sample%' "
			+ "\n and src_class <> 'WSJdbcConnection' "
			+ "\ngroup by "
			+ "\n  1,2,3,4,5,6 "
			+ "\norder by "
			+ "\n  7 desc ";

		return sql;
	}
}
