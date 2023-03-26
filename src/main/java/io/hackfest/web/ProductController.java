package io.hackfest.web;

import io.hackfest.db.InventoryMovementEntity;
import io.hackfest.db.ProductEntity;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.ResponseStatus;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;

@Path("/pos/products")
public class ProductController {
    @Inject
    private EdgeDeviceVerifier edgeDeviceVerifier;

    @ConfigProperty(name = "tailorshift.shop.id")
    private Long shopId;

    @GET
    @Path("/{productId}")
    @ResponseStatus(200)
    public ProductEntity getProduct(
            @PathParam("productId") Long productId,
            HttpHeaders headers
    ) {
        edgeDeviceVerifier.verifyRequest(headers);
        ProductEntity product = ProductEntity.<ProductEntity>findByIdOptional(productId)
                .orElseThrow(NotFoundException::new);

        product.available = InventoryMovementEntity.getAvailableItems(productId, shopId);

        return product;
    }
}
