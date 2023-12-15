package REST.server.helloworld;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/computationservice")
public class ComputationService {

    @GET
    @Path("/calculate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculate(@QueryParam("n1") double n1,
                              @QueryParam("n2") double n2,
                              @QueryParam("op") String op) {
        double result;
        switch (op) {
            case "add" -> result = n1 + n2;
            case "subtract" -> result = n1 - n2;
            case "multiply" -> result = n1 * n2;
            case "divide" -> result = n1 / n2;
            default -> {
                return Response.status(Response.Status.BAD_REQUEST).entity("Ungültige Operation").build();
            }
        }
        return Response.ok("{\"result\": " + result + "}").build();
    }
}
