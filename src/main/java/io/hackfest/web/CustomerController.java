package io.hackfest.web;

import io.hackfest.db.CustomerEntity;
import io.hackfest.db.ReceiptEntity;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import java.util.List;

@Path("/pos/customers")
public class CustomerController {
    @Inject
    EdgeDeviceVerifier edgeDeviceVerifier;

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
