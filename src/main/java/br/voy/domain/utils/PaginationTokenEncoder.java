package br.voy.domain.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class PaginationTokenEncoder {

    private static String secretKey = "voy-pagination-secret-2024";
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Value("${voy.security.pagination.secret-key}")
    public void setSecretKey(String key) {
        if (key != null && !key.isEmpty()) {
            PaginationTokenEncoder.secretKey = key;
        }
    }

    public static String encode(int offset) {
        try {
            long timestamp = System.currentTimeMillis();
            String salt = generateSalt();

            String payload = offset + ":" + timestamp + ":" + salt;

            String checksum = generateChecksum(payload);

            String token = payload + ":" + checksum;

            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(token.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(String.valueOf(offset).getBytes(StandardCharsets.UTF_8));
        }
    }

    public static int decode(String token) {
        if (token == null || token.isEmpty()) {
            return 0;
        }

        try {
            String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);

            String[] parts = decoded.split(":");

            if (parts.length >= 4) {
                int offset = Integer.parseInt(parts[0]);
                String receivedChecksum = parts[3];

                String payload = parts[0] + ":" + parts[1] + ":" + parts[2];
                String expectedChecksum = generateChecksum(payload);

                if (expectedChecksum.equals(receivedChecksum)) {
                    return offset;
                }
            } else if (parts.length == 1) {
                return Integer.parseInt(parts[0]);
            }

            return 0;

        } catch (Exception e) {
            return 0;
        }
    }

    private static String generateSalt() {
        byte[] saltBytes = new byte[16];
        RANDOM.nextBytes(saltBytes);
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(saltBytes);
    }

    private static String generateChecksum(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secretKey.getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            );
            mac.init(secretKeySpec);

            byte[] checksumBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            byte[] shortChecksum = new byte[8];
            System.arraycopy(checksumBytes, 0, shortChecksum, 0, 8);

            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(shortChecksum);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return String.valueOf(payload.hashCode());
        }
    }
}
