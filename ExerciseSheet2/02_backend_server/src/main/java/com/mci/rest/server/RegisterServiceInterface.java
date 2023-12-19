package com.mci.rest.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/computationservice")
public interface RegisterServiceInterface {

    @GET
    @Path("/registerService")
    @Produces({ MediaType.APPLICATION_JSON })
    public String registerService(@QueryParam("serverUrl") String serverUrl);
}

