package io.hackfest.web;

import io.hackfest.dbmodel.InventoryMovementEntity;
import io.hackfest.dbmodel.ProductEntity;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.ResponseStatus;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

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
                .orElseThrow(() -> new WebApplicationException(Response.status(404).build()));

        product.available = InventoryMovementEntity.getAvailableItems(productId, shopId);

        return product;
    }
}
