package org.acme;

import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    @GET
    @Path("/user")
    @Produces(MediaType.TEXT_PLAIN)
    @APIResponses(
            value = {
                    @APIResponse(responseCode = "200", content =  @Content(
                            mediaType = "*/*",
                            examples = {
                                    @ExampleObject(name = "User",
                                            summary = "User Example Summary",
                                            ref = "userExample")
                            }

                    ))
            }
    )
    public String helloUser() {
        return "Hello User";
    }



    @GET
    @Path("/customer")
    @Produces(MediaType.TEXT_PLAIN)
    @APIResponses(
            value = {
                    @APIResponse(responseCode = "200", content =  @Content(
                            mediaType = "*/*",
                            examples = {
                                    @ExampleObject(name = "Customer",
                                            summary = "Customer Example Summary",
                                            ref = "customerExample")
                            }

                    ))
            }
    )
    public String helloCustomer() {
        return "Hello Customer";
    }
}