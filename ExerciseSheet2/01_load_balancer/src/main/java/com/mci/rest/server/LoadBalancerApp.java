package com.mci.rest.server;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;


public class LoadBalancerApp extends Application {
	
	final static int SERVER_PORT = 8787;
	final static String SERVER_PATH_PREFIX = "/api";
 
    private final Set<Object> singletons = new HashSet<Object>();
 
    public LoadBalancerApp() {

        singletons.add(new LoadBalancerService());
    }
 
    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
    
    public static void main(String[] args) {
    	Server server = new Server(SERVER_PORT);
    	
    	// Setup the basic Application "context" at "/".
        // This is also known as the handler tree (in Jetty speak).
        final ServletContextHandler context = new ServletContextHandler(server, "/");

        // Setup RESTEasy's HttpServletDispatcher at "/api/*".
        final ServletHolder restEasyServlet = new ServletHolder(new HttpServletDispatcher());
        //final ServletHolder restEasyServlet = new ServletHolder(new HttpServletDispatcher());
        restEasyServlet.setInitParameter("resteasy.servlet.mapping.prefix", SERVER_PATH_PREFIX);
        restEasyServlet.setInitParameter("javax.ws.rs.Application", LoadBalancerApp.class.getCanonicalName());
        context.addServlet(restEasyServlet,  SERVER_PATH_PREFIX + "/*");

        // Setup the DefaultServlet at "/".
        final ServletHolder defaultServlet = new ServletHolder(new DefaultServlet());
        context.addServlet(defaultServlet, "/");
    	
        server.setHandler(context);
    	
        // Start server
    	try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
