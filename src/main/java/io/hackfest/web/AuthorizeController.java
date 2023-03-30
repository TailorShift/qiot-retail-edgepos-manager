package io.hackfest.web;

import io.hackfest.db.EmployeeEntity;
import org.jboss.resteasy.reactive.ResponseStatus;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Path("/pos")
public class AuthorizeController {

    @Inject
    EdgeDeviceVerifier edgeDeviceVerifier;

    @POST
    @Path("authorize-employee")
    @ResponseStatus(200)
    public EmployeeEntity authorizeEmployee(
            @FormParam("card-id") Long cardId,
            HttpHeaders headers
    ) {
        edgeDeviceVerifier.verifyRequest(headers);
        return EmployeeEntity.findByCardId(cardId)
                .orElseThrow(() -> new WebApplicationException(Response.status(401).build()));
    }
}
