package com.mci.rest.server;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import java.util.*;

@Path("/computationservice")
public class LoadBalancerService {
    private static final Random rand = new Random();

    @GET
    @Path("/calculate")
    @Produces({ MediaType.APPLICATION_JSON })
    public String CalculateValue(@QueryParam("n1") String n1,@QueryParam("n2") String n2,@QueryParam("op") String op, @Context HttpServletRequest req) {
        // Input-Validierung for best practice :D
        if (n1 == null || n1.trim().isEmpty() || n2 == null || n2.trim().isEmpty() || op == null || op.trim().isEmpty()) {
            return "Invalid input: One or more parameters are empty.";
        }

        try {
            Double.parseDouble(n1);
            Double.parseDouble(n2);
        } catch (NumberFormatException e) {
            return "Invalid input: n1 and n2 must be numeric.";
        }

        List<String> validOps = Arrays.asList("+", "-", "*", "/");
        if (!validOps.contains(op)) {
            return "Invalid input: 'op' must be one of +, -, *, /.";
        }

        System.out.println(req.getRemoteAddr() + ":" + req.getRemotePort() + " called CalculateValue(" + n1 + ", " + n2 + ", " + op + ")");

    	// Get list of backend servers and randomly select one
    	BackendManager mgr = BackendManager.getInstance();
    	List<String> serverList =mgr.getBackendServices();
        if (serverList.isEmpty()) {
            System.out.println("No backend servers registered.");
            return "Error: No backend servers available.";
        }

        String restServiceUrl = serverList.get(rand.nextInt(serverList.size()));
        
        System.out.println("Redirecting request to " + restServiceUrl);
    	
        // Call backend service and return result
        ResteasyClient client = (ResteasyClient) ResteasyClientBuilder.newBuilder().build();
		//ResteasyClient client = new ResteasyClientBuilder().build();

        ResteasyWebTarget target = (ResteasyWebTarget) client.target(UriBuilder.fromPath(restServiceUrl));
		//ResteasyWebTarget target = client.target(String.valueOf(UriBuilder.fromPath(restServiceUrl)));

		ComputationServiceInterface proxy = target.proxy(ComputationServiceInterface.class);
		String result = proxy.CalculateValue(n1,n2, op);
        
        System.out.println("Returning " + result);
        
		return result;
    }

    @GET
    @Path("/registerService")
    @Produces({ MediaType.APPLICATION_JSON })
    public String registerService(@QueryParam("serverUrl") String serverUrl, @Context HttpServletRequest req) {
    	BackendManager mgr = BackendManager.getInstance();
    	
        System.out.println(req.getRemoteAddr() + ":" + req.getRemotePort() + " wants to register service url " + serverUrl);
    	
        // Add service url to backend service list
    	if (serverUrl != null && !serverUrl.isEmpty() && mgr.addBackendService(serverUrl)) {
            System.out.println("Success");
    		return "\"OK\"";
    	} else {
            System.out.println("Error");
    		return "\"ERROR\"";
    	}
    }

}
