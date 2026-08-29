package br.voy.domain.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GeoCalculatorTest {

    private static final double EARTH_RADIUS_KM = 6371.0;

    @Test
    @DisplayName("Must to format haversine distance with three decimal places")
    void mustToFormatDistanceWithThreeDecimalPlaces() {
        assertEquals("1.224 km", GeoCalculator.formatDistanceKm(1.2244));
    }

    @Test
    @DisplayName("Must to compute a bounding box that contains the origin point")
    void mustToComputeBoundingBoxContainingOrigin() {
        var box = GeoCalculator.boundingBox(-29.35995, -50.84805, 10.0, EARTH_RADIUS_KM);

        assertTrue(box.getMinLat() < -29.35995);
        assertTrue(box.getMaxLat() > -29.35995);
        assertTrue(box.getMinLon() < -50.84805);
        assertTrue(box.getMaxLon() > -50.84805);
    }

    @Test
    @DisplayName("Must to return zero kilometers for identical coordinates")
    void mustToReturnZeroForIdenticalCoordinates() {
        double distance =
                GeoCalculator.haversineKm(
                        -29.35995, -50.84805, -29.35995, -50.84805, EARTH_RADIUS_KM);

        assertEquals(0.0, distance, 0.0001);
    }
}
