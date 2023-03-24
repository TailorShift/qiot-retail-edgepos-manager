package io.hackfest.web;

import io.hackfest.dbmodel.PosDeviceEntity;
import io.hackfest.dbmodel.ReceiptEntity;
import io.hypersistence.tsid.TSID;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.ResponseStatus;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Path("/pos/receipts")
public class ReceiptController {
    @Inject
    private EdgeDeviceVerifier edgeDeviceVerifier;

    @ConfigProperty(name = "tailorshift.shop.id")
    private Long shopId;

    @GET
    @Path("/{receiptId}")
    @ResponseStatus(200)
    public ReceiptEntity getProduct(
            @PathParam("receiptId") Long receiptId,
            HttpHeaders headers
    ) {
        edgeDeviceVerifier.verifyRequest(headers);
        return ReceiptEntity.<ReceiptEntity>findByIdOptional(receiptId)
                .orElseThrow(() -> new WebApplicationException(Response.status(404).build()));
    }

    @POST
    @Path("/")
    @ResponseStatus(201)
    @Transactional
    public ReceiptEntity postReceipt(
            ReceiptEntity receiptEntity,
            HttpHeaders headers
    ) {
        String deviceId = edgeDeviceVerifier.verifyRequest(headers);
        PosDeviceEntity device = PosDeviceEntity.findByDeviceId(deviceId)
                .orElseThrow(() -> new WebApplicationException("Unknown deviceId " + deviceId, 401));

        // generate an ID that is better than a UUID (better for sorting / partitioning)
        // https://vladmihalcea.com/tsid-identifier-jpa-hibernate/
        long receiptId = TSID.Factory.getTsid().toLong();
        receiptEntity.id = receiptId;
        receiptEntity.shopId = shopId;
        receiptEntity.posDeviceId = device.id;

        for (var position : receiptEntity.positions) {
            position.id = receiptId++;
        }

        ReceiptEntity.persist(receiptEntity);

        return receiptEntity;
    }
}
