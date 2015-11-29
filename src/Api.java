import com.google.gson.Gson;
import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.MacProvider;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.Key;

/**
 * Created by ADI on 29-11-2015.
 */
@Path("/jwt")
public class Api {

    private final Key key = MacProvider.generateKey();
    private String jwtString;

    @Path("/welcome")
    @GET
    @Produces("application/json")
    public Response welcome(){

        return Response
                .status(200)
                .entity("{\"data\":\"Welcome to web token app\"}")
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Path("/login")
    @POST
    @Produces("application/json")
    public Response login(String data) {

        User user = new Gson().fromJson(data, User.class);

        int statusCode = 200;
        if (user.getUsername().equals("siad14ab") && user.getPassword().equals("123")) {
            jwtString = Jwts.builder().setSubject("{\"userId\":\"1\"}").signWith(SignatureAlgorithm.HS256, key).compact();
            System.out.println(jwtString);
        }

        return Response
                .status(statusCode)
                .entity("{\"response\":\"login successful\"}")
                .header("Access-Control-Allow-Origin", "*")
                .header("authorization", jwtString)
                .build();

    }

    @Path("/key/")
    @GET
    @Produces("application/json")
    public Response getKey(@HeaderParam("authorization") String authorization){

        int statusCode;
        String data;
        Jwt jwt = null;
        System.out.println(authorization);
        try {
            //throws Signature exception if signature validation fails
            assert Jwts.parser().setSigningKey("").parseClaimsJwt(authorization).getBody().getSubject().equals("fail");
            //assert Jwts.parser().setSigningKey(key).parseClaimsJwt(authorization).getBody().getSubject().equals("{\"userId\":\"1\"}");

            //Jwts.parser().setSigningKey(key).parseClaimsJws(authorization);
            //jwt = Jwts.parser().setSigningKey(key).parse(authorization);

            statusCode = 200;

            data = "{\"response\":\"success\"}";

        } catch (SignatureException e) {

            data = "{\"response\":\"failure\"}";
            statusCode = 401;
        }
        return Response
                .status(statusCode)
                .entity(data)
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServerFactory.create("http://localhost:9999/");
        server.start();
        System.out.println("Server running");
        System.out.println("Visit: http://localhost:9999/jwt/welcome");
        System.out.println("Hit return to confirm to stop...");
        System.in.read();
        System.out.println("Stopping server");
        server.stop(0);
        System.out.println("Server stopped");
    }
}
