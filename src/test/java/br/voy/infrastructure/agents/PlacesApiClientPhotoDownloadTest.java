package br.voy.infrastructure.agents;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.ServerSocket;
import java.net.Socket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlacesApiClientPhotoDownloadTest {

    @Test
    @DisplayName("List photos request at most 400 pixels")
    void listPhotosRequestAtMost400Pixels() {
        PlacesApiClient client = new PlacesApiClient("test-key");
        try {
            String url = client.buildPhotoUrl("ref", 1400, PlacesApiClient.LIST_PHOTO_MAX_WIDTH);
            assertTrue(url.contains("maxwidth=400"));
            assertFalse(url.contains("maxwidth=1400"));
        } finally {
            client.shutdown();
        }
    }

    @Test
    @DisplayName("Photo download stops when the server stalls")
    void photoDownloadStopsWhenTheServerStalls() throws Exception {
        try (ServerSocket server = new ServerSocket(0)) {
            Thread accept =
                    new Thread(
                            () -> {
                                try (Socket socket = server.accept()) {
                                    Thread.sleep(5_000);
                                } catch (Exception ignored) {
                                    // The client closes the stalled download.
                                }
                            });
            accept.setDaemon(true);
            accept.start();

            PlacesApiClient client = new PlacesApiClient("test-key");
            try {
                long started = System.nanoTime();
                assertThrows(
                        Exception.class,
                        () ->
                                client.downloadPhoto(
                                        "http://127.0.0.1:" + server.getLocalPort() + "/photo",
                                        200,
                                        200));
                long elapsedMs = (System.nanoTime() - started) / 1_000_000;
                assertTrue(elapsedMs < 2_000);
            } finally {
                client.shutdown();
            }
        }
    }
}
