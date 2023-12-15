package REST.client.helloworld;

        import javax.ws.rs.GET;
        import javax.ws.rs.Path;
        import javax.ws.rs.QueryParam;
        import javax.ws.rs.core.MediaType;
        import javax.ws.rs.Produces;
        import javax.ws.rs.core.Response;

@Path("/computationservice")
public interface ComputationServiceInterface {

    @GET
    @Path("/calculate")
    @Produces({ MediaType.APPLICATION_JSON })
    public ResponseClass calculate(@QueryParam("n1") int n1,  @QueryParam("n2") int n2, @QueryParam("op") String op);
}
