package com.mci.rest.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import javax.servlet.http.HttpServletRequest;

@Path("/computationservice")
public class ComputationService {

    @GET
    @Path("/gethello")
    @Produces({ MediaType.APPLICATION_JSON })
    public String getHelloMessage() {
        return "\"Hello World!\"";
    }


    @GET
    @Path("/calculate")
    @Produces({ MediaType.APPLICATION_JSON })
    public String CalculateValue(@QueryParam("n1") String n1,@QueryParam("n2") String n2,@QueryParam("op") String op, @Context HttpServletRequest req) {
        double output = 0.0;
        double input1;
        double input2;
        
        System.out.println(req.getRemoteAddr() + ":" + req.getRemotePort() + " called CalculateValue(" + n1 + ", " + n2 + ", " + op + ")");
        
        try {
             input1 = Double.parseDouble(n1);
             input2 = Double.parseDouble(n2);
        }catch (NumberFormatException e){
            return "\"Please send valid numbers\"";
        }
        if(op.equalsIgnoreCase("Add")) {
            output = input1 + input2 ;
        }else if (op.equalsIgnoreCase("Sub")){
            output = input1 - input2 ;
        }else if(op.equalsIgnoreCase("Multiply")){
            output = input1 * input2 ;
        }else if(op.equalsIgnoreCase("Mod")){
            output = input1 % input2 ;
        }else if(op.equalsIgnoreCase("Div")){
            output = input1 / input2 ;
        }else {
            return "\"Invalid Operator. Valid operators are ADD,SUB,MULTIPLY,MOD,DIV\"" ;
        }
        String str = String.valueOf(output);
        String result = "\"Answer is: " + str+ "\"";
        
        System.out.println("Returning " + result);
        
        return result;
    }
}
