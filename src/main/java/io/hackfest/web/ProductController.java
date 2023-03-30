package io.hackfest.web;

import io.hackfest.db.AvailableItem;
import io.hackfest.db.InventoryMovementEntity;
import io.hackfest.db.ProductEntity;
import io.hackfest.web.error.ApiException;
import io.hackfest.web.error.ErrorCode;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.ext.web.client.WebClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Path("/pos/products")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    @Inject
    private EdgeDeviceVerifier edgeDeviceVerifier;

    @ConfigProperty(name = "tailorshift.shop.id")
    private Long shopId;

    @ConfigProperty(name = "tailorshift.datacenter.inventoryManagerUrl")
    private String inventoryManagerUrl;

    private final WebClient webClient = WebClient.create(Vertx.vertx());

    @GET
    @Path("/{productId}")
    @ResponseStatus(200)
    public ProductEntity getProduct(
            @PathParam("productId") Long productId,
            @QueryParam("allShops") Boolean allShops,
            HttpHeaders headers
    ) {
        edgeDeviceVerifier.verifyRequest(headers);
        ProductEntity product = ProductEntity.<ProductEntity>findByIdOptional(productId)
                .orElseThrow(NotFoundException::new);

        if (allShops == Boolean.TRUE) {
            logger.info("Querying available items from inventory manager for productId {}", productId);

            // Send request
            try {
                var options = new RequestOptions().setAbsoluteURI(inventoryManagerUrl + "/availableItems/" + productId);
                var result = webClient.request(HttpMethod.GET, options)
                        .send()
                        .toCompletionStage()
                        .toCompletableFuture()
                        .get(5, TimeUnit.SECONDS)
                        .bodyAsJson(AvailableItem[].class);

                product.available = Arrays.asList(result);
            } catch (InterruptedException | TimeoutException | ExecutionException e) {
                throw ApiException.serverError(ErrorCode.NO_RESPONSE_FROM_DATACENTER);
            }
        } else {
            logger.info("Querying available items from local stock for productId {}", productId);
            product.available = InventoryMovementEntity.getAvailableItems(productId, shopId);
        }

        return product;
    }
}
