package io.hackfest.web;

import io.hackfest.dbmodel.ReceiptEntity;
import io.hypersistence.tsid.TSID;
import org.jboss.resteasy.reactive.ResponseStatus;

import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Path("/pos/receipts")
public class ReceiptController {
    @GET
    @Path("/{receiptId}")
    @ResponseStatus(200)
    public ReceiptEntity getProduct(
            @PathParam("receiptId") Long receiptId
    ) {
        return ReceiptEntity.<ReceiptEntity>findByIdOptional(receiptId)
                .orElseThrow(() -> new WebApplicationException(Response.status(404).build()));
    }

    @POST
    @Path("/")
    @ResponseStatus(201)
    @Transactional
    public ReceiptEntity postReceipt(
            ReceiptEntity receiptEntity
    ) {
        // generate an ID that is better than a UUID (better for sorting / partitioning)
        // https://vladmihalcea.com/tsid-identifier-jpa-hibernate/
        long receiptId = TSID.Factory.getTsid().toLong();
        receiptEntity.id = receiptId;

        for (var position : receiptEntity.positions) {
            position.id = receiptId++;
        }

        ReceiptEntity.persist(receiptEntity);

        return receiptEntity;
    }
}
