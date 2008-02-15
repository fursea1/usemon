<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
 <%
 	/*
 		System.err.println("requestURL() :- " + request.getRequestURL().toString());
 		System.err.println("contextPath() :- " + request.getContextPath().toString());
 		System.err.println("requestURI() :- " + request.getRequestURI().toString());
 	*/
		// Assumes that this page was loaded automagically as the welcome page,
		// in which case the URL will be the root of this application.
        String redirectURL = request.getRequestURL().toString() + "org.usemon.gui.UsemonApp/UsemonApp.html";
        
        // Redirect to ensure that all references to JavaScript, CSS etc. are kept
        response.sendRedirect(redirectURL);
 %>

   