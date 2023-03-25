package io.hackfest.dbmodel;

public record AvailableItem(
        Long shopId,
        Size size,
        String color,
        Long quantity,
        Integer shopDiscount
) {
}
