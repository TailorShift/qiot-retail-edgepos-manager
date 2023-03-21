package io.hackfest.web;

import javax.ws.rs.core.HttpHeaders;
import java.util.Base64;
import java.util.Optional;

public record CryptoHeaders(
        String deviceId,
        long timestamp,
        byte[] signature
) {
    public static CryptoHeaders from(HttpHeaders httpHeaders) {
        String deviceId = httpHeaders.getHeaderString("pos-device-id");
        long timestamp = Optional.ofNullable(httpHeaders.getHeaderString("pos-sign-timestamp"))
                .map(Long::valueOf).orElse(0L);
        byte[] signature = Optional.ofNullable(httpHeaders.getHeaderString("pos-signature"))
                .map(baee64string -> Base64.getDecoder().decode(baee64string))
                .orElse(null);

        return new CryptoHeaders(deviceId, timestamp, signature);
    }
}
