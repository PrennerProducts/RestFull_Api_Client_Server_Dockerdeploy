package com.mci.rest.server;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.io.File;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.FileOutputStream;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;



public class ComputationApp extends Application {
	
	final static int SERVER_PORT = 8989;
	final static String SERVER_PATH_PREFIX = "/api";
 
    private final Set<Object> singletons = new HashSet<Object>();
 
    public ComputationApp() {

        singletons.add(new ComputationService());
    }
 
    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
    
    public static void main(String[] args) {
    
        // Redirecting any output to a logfile
//        try {
//            File logfile = new File("/var/log/computationservice/service.log");
//            FileOutputStream logstream = new FileOutputStream(logfile);
//            PrintStream logprintstream = new PrintStream(logstream);
//            System.setOut(logprintstream);
//            System.setErr(logprintstream);
//        } catch (Exception e) {
//            System.err.println("Could not create log file");
//            e.printStackTrace();
//        }
    
        Server server = new Server(SERVER_PORT);
        
        // Setup the basic Application "context" at "/".
        // This is also known as the handler tree (in Jetty speak).
        final ServletContextHandler context = new ServletContextHandler(server, "/");

        // Setup RESTEasy's HttpServletDispatcher at "/api/*".
        final ServletHolder restEasyServlet = new ServletHolder(new HttpServletDispatcher());
        restEasyServlet.setInitParameter("resteasy.servlet.mapping.prefix", SERVER_PATH_PREFIX);
        restEasyServlet.setInitParameter("javax.ws.rs.Application", ComputationApp.class.getCanonicalName());
        context.addServlet(restEasyServlet,  SERVER_PATH_PREFIX + "/*");

        // Setup the DefaultServlet at "/".
        final ServletHolder defaultServlet = new ServletHolder(new DefaultServlet());
        context.addServlet(defaultServlet, "/");
    	
        server.setHandler(context);

        try {
	        // Retrieve environment variable LOAD_BALANCER_URL
	        Map<String, String> env = System.getenv();
	        String load_balancer_url =  env.get("LOAD_BALANCER_URL");
	        InetAddress ip;
	        // LOAD_BALANCER_URL contains a valid value
	        if(load_balancer_url != null && !load_balancer_url.isEmpty()) {
	        	// Get local ip-address and construct service url
					ip = InetAddress.getLocalHost();
	            String hostAddress = ip.getHostAddress();
	            String serverUrl = "http://" + hostAddress + ":" + SERVER_PORT + SERVER_PATH_PREFIX;
	
	            // Register service url at load balancer
	    		ResteasyClient client = new ResteasyClientBuilder().build();
                ResteasyWebTarget target = client.target(load_balancer_url);
                RegisterServiceInterface proxy = target.proxy(RegisterServiceInterface.class);
	    		String result = proxy.registerService(serverUrl);
	    		
	    		if (Objects.equals(result, "\"OK\"")) {
	    			System.out.println("Sucessfully registered service at load-balancer.");
	    		} else {
	    			System.out.println("Could not register service at load-balancer.");
	    		}
	        }
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
    	
        // Start server
    	try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
