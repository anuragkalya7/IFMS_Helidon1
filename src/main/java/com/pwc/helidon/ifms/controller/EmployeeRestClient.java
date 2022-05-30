package com.pwc.helidon.ifms.controller;

import javax.enterprise.context.Dependent;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "http://localhost:8180/jwt")
@Path("/employee")
@Dependent
public interface EmployeeRestClient {
    @GET
    @Path("/getData")
    public Response getDetails();
    
}