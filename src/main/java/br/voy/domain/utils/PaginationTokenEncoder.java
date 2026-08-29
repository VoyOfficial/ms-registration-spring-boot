package br.voy.domain.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    /** Encodes pagination state including offset and shown place IDs */
    public static String encode(int offset, Set<String> shownPlaceIds, String googleNextPageToken) {
        long timestamp = System.currentTimeMillis();
        String salt = generateSalt();

        String placeIdsStr =
                shownPlaceIds != null && !shownPlaceIds.isEmpty()
                        ? String.join(",", shownPlaceIds)
                        : "";

        String googleToken = googleNextPageToken != null ? googleNextPageToken : "";

        String payload =
                offset + ":" + timestamp + ":" + salt + ":" + placeIdsStr + ":" + googleToken;

        String checksum = generateChecksum(payload);
        String token = payload + ":" + checksum;

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    // Legacy method for backward compatibility
    public static String encode(int offset) {
        return encode(offset, new HashSet<>(), null);
    }

    /** Decodes pagination token into PaginationState */
    public static PaginationState decode(String token) {
        if (token == null || token.isEmpty()) {
            return new PaginationState(0, new HashSet<>(), null);
        }

        try {
            String decoded =
                    new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":", -1); // -1 to keep empty strings

            if (parts.length >= 6) {
                int offset = Integer.parseInt(parts[0]);
                String receivedChecksum = parts[5];

                String payload =
                        parts[0] + ":" + parts[1] + ":" + parts[2] + ":" + parts[3] + ":"
                                + parts[4];
                String expectedChecksum = generateChecksum(payload);

                if (expectedChecksum.equals(receivedChecksum)) {
                    Set<String> placeIds = new HashSet<>();
                    if (!parts[3].isEmpty()) {
                        String[] ids = parts[3].split(",");
                        for (String id : ids) {
                            if (!id.trim().isEmpty()) {
                                placeIds.add(id.trim());
                            }
                        }
                    }

                    String googleToken = parts[4].isEmpty() ? null : parts[4];

                    return new PaginationState(offset, placeIds, googleToken);
                }
                throw new IllegalArgumentException("Invalid pagination token: checksum mismatch");
            }

            throw new IllegalArgumentException("Invalid pagination token: unrecognized format");

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid pagination token", e);
        }
    }

    private static String generateSalt() {
        byte[] saltBytes = new byte[16];
        RANDOM.nextBytes(saltBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(saltBytes);
    }

    private static String generateChecksum(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(secretKeySpec);

            byte[] checksumBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            byte[] shortChecksum = new byte[8];
            System.arraycopy(checksumBytes, 0, shortChecksum, 0, 8);

            return Base64.getUrlEncoder().withoutPadding().encodeToString(shortChecksum);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Failed to generate pagination token checksum", e);
        }
    }

    /** Class to hold pagination state */
    public static class PaginationState {
        private final int offset;
        private final Set<String> shownPlaceIds;
        private final String googleNextPageToken;

        public PaginationState(int offset, Set<String> shownPlaceIds, String googleNextPageToken) {
            this.offset = offset;
            this.shownPlaceIds = shownPlaceIds != null ? shownPlaceIds : new HashSet<>();
            this.googleNextPageToken = googleNextPageToken;
        }

        public int getOffset() {
            return offset;
        }

        public Set<String> getShownPlaceIds() {
            return shownPlaceIds;
        }

        public String getGoogleNextPageToken() {
            return googleNextPageToken;
        }
    }
}
