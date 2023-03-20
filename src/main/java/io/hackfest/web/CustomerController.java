package io.hackfest.web;

import io.hackfest.dbmodel.CustomerEntity;
import io.hackfest.dbmodel.EmployeeRepository;
import io.hackfest.dbmodel.ReceiptEntity;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/pos/customers")
public class CustomerController {
    @GET
    @Path("/{customerId}")
    public CustomerEntity getCustomer(
            @PathParam("customerId") Long customerId
    ) {
        return CustomerEntity.<CustomerEntity>findByIdOptional(customerId)
                .orElseThrow(() -> new WebApplicationException(Response.status(404).build()));
    }

    @GET
    @Path("/{customerId}/receipts")
    public List<ReceiptEntity> getCustomerReceipts(
            @PathParam("customerId") Long customerId
    ) {
        return List.of();
    }


}
