package io.hackfest.web;

import io.hackfest.dbmodel.EmployeeEntity;
import io.hackfest.dbmodel.EmployeeRepository;
import org.jboss.resteasy.reactive.ResponseStatus;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Path("/pos")
public class AuthorizeController {
    @Inject
    public EmployeeRepository employeeRepository;

    @POST
    @Path("authorize-employee")
    @ResponseStatus(200)
    public EmployeeEntity authorizeEmployee(
            @FormParam("card-id") Long cardId
    ) {
        return EmployeeEntity.findByCardId(cardId)
                .orElseThrow(() -> new WebApplicationException(Response.status(401).build()));
    }
}
