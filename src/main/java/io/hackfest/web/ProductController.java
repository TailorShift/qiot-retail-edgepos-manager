package io.hackfest.web;

import io.hackfest.dbmodel.ProductEntity;
import org.jboss.resteasy.reactive.ResponseStatus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Path("/pos/products")
public class ProductController {
    @GET
    @Path("/{productId}")
    @ResponseStatus(200)
    public ProductEntity getProduct(
            @PathParam("productId") Long productId
    ) {
        return ProductEntity.<ProductEntity>findByIdOptional(productId)
                .orElseThrow(() -> new WebApplicationException(Response.status(404).build()));
    }
}
