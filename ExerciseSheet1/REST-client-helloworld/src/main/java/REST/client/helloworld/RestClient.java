package REST.client.helloworld;

import javax.ws.rs.core.UriBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import java.util.Scanner;
public class RestClient {

	final static String SERVER_ADDRESS = "localhost";
	final static int SERVER_PORT = 8989; // Change this to your server's port
	final static String SERVER_PATH_PREFIX = "/api";

	public static void main(String[] args) {
		// Input Scanner
		Scanner inputScanner = new Scanner(System.in);

		final String urlbase = "http://" + SERVER_ADDRESS + ":" + SERVER_PORT + SERVER_PATH_PREFIX;

		// Create client
		ResteasyClient client = new ResteasyClientBuilder()
				.register(JacksonJsonProvider.class)  // Register Jackson provider
				.build();

		ResteasyWebTarget target = client.target(UriBuilder.fromPath(urlbase));
		ComputationServiceInterface proxy = target.proxy(ComputationServiceInterface.class);

		// Get an Response from the Server to Test that $'####'!
//		ResponseClass response = proxy.calculate(5,5, "multiply") ;
//		System.out.println("Received from server: " + response.getResult());

		// Test Computation
		System.out.println("Testing Computation service");
		System.out.println("Please enter the first number: ");
		int n1 = inputScanner.nextInt();
		System.out.println("Please enter the second number: ");
		int n2 = inputScanner.nextInt();
		System.out.println("Please enter the operation (add, subtract, multiply, divide): ");
		String op = inputScanner.next();

		ResponseClass result = proxy.calculate(n1, n2, op);
		System.out.println("Result received from server: " + result.getResult());

	}
}
