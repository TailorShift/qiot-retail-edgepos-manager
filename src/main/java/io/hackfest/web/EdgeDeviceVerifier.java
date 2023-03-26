package io.hackfest.web;

import io.hackfest.db.PosDeviceEntity;
import io.hackfest.web.error.ApiException;
import io.hackfest.web.error.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.HttpHeaders;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.abs;

@ApplicationScoped
public class EdgeDeviceVerifier {
    private static final Logger logger = LoggerFactory.getLogger(EdgeDeviceVerifier.class);

    private final Signature sig = Signature.getInstance("SHA256withRSA");

    private final Map<String, PublicKey> publicKeyByDevice = new HashMap<>();

    public EdgeDeviceVerifier() throws NoSuchAlgorithmException {
    }

    private PublicKey getPublicKey(String deviceId) throws NoSuchAlgorithmException, InvalidKeySpecException, CertificateException {
        PublicKey publicKey = publicKeyByDevice.get(deviceId);

        if (publicKey == null) {
            logger.info("Looking up public key for device id {}", deviceId);

            String certificate = PosDeviceEntity.findByDeviceId(deviceId)
                    .map(device -> device.iotCertificate)
                    .orElseThrow(() -> ApiException.unauthorized(ErrorCode.UNKNOWN_DEVICE_ID, deviceId));

            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            try (var is = new ByteArrayInputStream(certificate.getBytes(StandardCharsets.UTF_8))) {
                Certificate cert = factory.generateCertificate(is);
                publicKey = cert.getPublicKey();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            publicKeyByDevice.put(deviceId, publicKey);
        }

        return publicKey;
    }

    public String verifyRequest(HttpHeaders headers) {
        CryptoHeaders cryptoHeaders = CryptoHeaders.from(headers);
        logger.debug("Verifying crypto headers: {}", cryptoHeaders);

        try {
            PublicKey publicKey = getPublicKey(cryptoHeaders.deviceId());
            var signingTime = LocalDateTime.ofEpochSecond(cryptoHeaders.timestamp(), 0, ZoneOffset.UTC);

            // accept 10s time difference in any direction
            if (abs(Duration.between(signingTime, ZonedDateTime.now(ZoneOffset.UTC)).getSeconds()) > 10) {
                logger.error("Signing time {} deviates more than 10 seconds from local time", signingTime);
                throw ApiException.unauthorized(ErrorCode.SIGNATURE_INVALID_TIMESTAMP);
            }

            String signatureBase = cryptoHeaders.deviceId() + "::" + cryptoHeaders.timestamp();

            try {
                sig.initVerify(publicKey);
                sig.update(signatureBase.getBytes(StandardCharsets.UTF_8));
                if(!sig.verify(cryptoHeaders.signature())) {
                    throw ApiException.unauthorized(ErrorCode.SIGNATURE_MISMATCH);
                }

            } catch (SignatureException e) {
                throw ApiException.serverError(ErrorCode.SIGNATURE_VERIFICATION_FAILED);
            }

            return cryptoHeaders.deviceId();
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | CertificateException e) {
            throw ApiException.serverError(ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        }
    }
}
