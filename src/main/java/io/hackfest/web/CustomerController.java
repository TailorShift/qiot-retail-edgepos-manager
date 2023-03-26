package io.hackfest.web;

import io.hackfest.dbmodel.CustomerEntity;
import io.hackfest.dbmodel.ReceiptEntity;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/pos/customers")
public class CustomerController {
    @Inject
    private EdgeDeviceVerifier edgeDeviceVerifier;

    @GET
    @Path("/{customerId}")
    public CustomerEntity getCustomer(
            @PathParam("customerId") Long customerId,
            HttpHeaders headers
    ) {
        edgeDeviceVerifier.verifyRequest(headers);
        return CustomerEntity.<CustomerEntity>findByIdOptional(customerId)
                .orElseThrow(NotFoundException::new);
    }

    @GET
    @Path("/{customerId}/receipts")
    public List<ReceiptEntity> getCustomerReceipts(
            @PathParam("customerId") Long customerId,
            HttpHeaders headers
    ) {
        edgeDeviceVerifier.verifyRequest(headers);
        return List.of();
    }


}
