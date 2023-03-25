package io.hackfest.dbmodel;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record InventoryQuantity(
        Long productId,
        Long shopId,
        Size size,
        String color,
        Long quantity
) {
}
