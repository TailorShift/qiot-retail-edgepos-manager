package io.hackfest.web;

import io.hackfest.dbmodel.PosDeviceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
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
            String certificate = PosDeviceEntity.findByDeviceId(deviceId)
                    .map(device -> device.iotCertificate)
                    .orElseThrow(() -> new WebApplicationException("Unknown deviceId " + deviceId, 401));

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

        try {
            PublicKey publicKey = getPublicKey(cryptoHeaders.deviceId());
            var signingTime = LocalDateTime.ofEpochSecond(cryptoHeaders.timestamp(), 0, ZoneOffset.UTC);

            // accept 10s time difference in any direction
            if (abs(Duration.between(signingTime, ZonedDateTime.now(ZoneOffset.UTC)).getSeconds()) > 10) {
                logger.error("Signing time {} deviates more than 10 seconds from local time", signingTime);
                throw new WebApplicationException("Signing time deviates more than 10 seconds from local time", 401);
            }

            String signatureBase = cryptoHeaders.deviceId() + "::" + cryptoHeaders.timestamp();

            try {
                sig.initVerify(publicKey);
                sig.update(signatureBase.getBytes(StandardCharsets.UTF_8));
                if(!sig.verify(cryptoHeaders.signature())) {
                    logger.error("Signature mismatch");
                    throw new WebApplicationException("Signature mismatch", 401);
                }

            } catch (SignatureException e) {
                logger.error("Signature validation failed", e);
                throw new WebApplicationException("Signature validation failed", 401);
            }

            return cryptoHeaders.deviceId();
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | CertificateException e) {
            logger.error("Error on verifying signature", e);
            throw new WebApplicationException(e, Response.Status.UNAUTHORIZED);
        }
    }
}
