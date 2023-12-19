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
    private static Random rand = new Random();

    @GET
    @Path("/calculate")
    @Produces({ MediaType.APPLICATION_JSON })
    public String CalculateValue(@QueryParam("n1") String n1,@QueryParam("n2") String n2,@QueryParam("op") String op, @Context HttpServletRequest req) {
    	
        System.out.println(req.getRemoteAddr() + ":" + req.getRemotePort() + " called CalculateValue(" + n1 + ", " + n2 + ", " + op + ")");

    	// Get list of backend servers and randomly select one
    	BackendManager mgr = BackendManager.getInstance();
    	List<String> serverList =mgr.getBackendServices();
        String restServiceUrl = serverList.get(rand.nextInt(serverList.size()));
        
        System.out.println("Redirecting request to " + restServiceUrl);
    	
        // Call backend service and return result
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target(UriBuilder.fromPath(restServiceUrl));
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
