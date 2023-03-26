package io.hackfest.web;

import io.hackfest.db.*;
import io.hackfest.web.error.ApiException;
import io.hackfest.web.error.ErrorCode;
import io.hypersistence.tsid.TSID;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.ResponseStatus;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;

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
                .orElseThrow(NotFoundException::new);
    }

    @POST
    @Path("/")
    @ResponseStatus(201)
    @Transactional
    public ReceiptEntity postReceipt(
            ReceiptEntity receiptEntity,
            HttpHeaders headers
    ) {
        String deviceId = "shop1-dev1";
        PosDeviceEntity device = PosDeviceEntity.findByDeviceId(deviceId)
                .orElseThrow(() -> new WebApplicationException("Unknown deviceId " + deviceId, 401));

        // generate an ID that is better than a UUID (better for sorting / partitioning)
        // https://vladmihalcea.com/tsid-identifier-jpa-hibernate/
        receiptEntity.id = TSID.Factory.getTsid().toLong();
        receiptEntity.shopId = shopId;
        receiptEntity.posDeviceId = device.id;

        receiptEntity.persistAndFlush();

        long positionId = receiptEntity.id;
        for (var position : receiptEntity.positions) {
            verifyPosition(position);

            position.id = positionId++;

            var movement = new InventoryMovementEntity();
            movement.id = position.id;
            movement.shopId = shopId;
            movement.productId = position.productId;
            movement.receiptId = receiptEntity.id;
            movement.color = position.color;
            movement.size = position.size;
            movement.quantity = -position.quantity;

            InventoryMovementEntity.persist(movement);
        }

        return receiptEntity;
    }

    private void verifyPosition(ReceiptPositionEntity position) {
        // verify product exists
        var product = ProductEntity.<ProductEntity>findByIdOptional(position.productId)
                .orElseThrow(() -> ApiException.badRequest(ErrorCode.UNKNOWN_PRODUCT_ID, position.productId));

        // verify color
        if (!product.colors.contains(position.color)) {
            throw ApiException.badRequest(ErrorCode.INVALID_PRODUCT_COLOR, position.color, position.productId);
        }

        // verify in stock
        // attention : we ignore the case if the same product/size/color appears multiple times on the same receipt
        Long inStock = InventoryMovementEntity.getCurrentQuantity(
                        position.productId,
                        shopId,
                        position.size,
                        position.color
                ).map(InventoryQuantity::quantity)
                .orElse(0L);

        if (inStock < position.quantity) {
            throw ApiException.badRequest(ErrorCode.INSUFFICIENT_STOCK, position.productId, position.size, position.color);
        }

    }
}
