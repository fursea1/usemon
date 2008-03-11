package test.usemon.compliance.servlet;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class TestServletConfigImpl implements ServletConfig {
	public String getInitParameter(String arg0) {
		return null;
	}
	public Enumeration getInitParameterNames() {
		return null;
	}
	public ServletContext getServletContext() {
		return new ServletContext() {
			public Object getAttribute(String s) {
				return null;
			}

			@Override
			public Enumeration getAttributeNames() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ServletContext getContext(String s) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getInitParameter(String s) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Enumeration getInitParameterNames() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getMajorVersion() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public String getMimeType(String s) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getMinorVersion() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public RequestDispatcher getNamedDispatcher(String s) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getRealPath(String s) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public RequestDispatcher getRequestDispatcher(String s) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public URL getResource(String s) throws MalformedURLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public InputStream getResourceAsStream(String s) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set getResourcePaths(String s) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getServerInfo() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Servlet getServlet(String s) throws ServletException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getServletContextName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Enumeration getServletNames() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Enumeration getServlets() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void log(String s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void log(Exception exception, String s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void log(String s, Throwable throwable) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void removeAttribute(String s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setAttribute(String s, Object obj) {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
	public String getServletName() {
		return null;
	}

}
