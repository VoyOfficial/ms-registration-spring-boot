package br.voy.domain.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class GeoCalculator {

    private GeoCalculator() {}

    public static BoundingBox boundingBox(
            double latitude, double longitude, double radiusKm, double earthRadiusKm) {
        double latRad = Math.toRadians(latitude);
        double lonRad = Math.toRadians(longitude);
        double deltaLat = radiusKm / earthRadiusKm;
        double deltaLon = radiusKm / (earthRadiusKm * Math.cos(latRad));

        return new BoundingBox(
                Math.toDegrees(latRad - deltaLat),
                Math.toDegrees(latRad + deltaLat),
                Math.toDegrees(lonRad - deltaLon),
                Math.toDegrees(lonRad + deltaLon));
    }

    public static double haversineKm(
            double fromLatitude,
            double fromLongitude,
            double toLatitude,
            double toLongitude,
            double earthRadiusKm) {
        double fromLatRad = Math.toRadians(fromLatitude);
        double fromLonRad = Math.toRadians(fromLongitude);
        double toLatRad = Math.toRadians(toLatitude);
        double toLonRad = Math.toRadians(toLongitude);

        double deltaLat = toLatRad - fromLatRad;
        double deltaLon = toLonRad - fromLonRad;

        double a =
                Math.pow(Math.sin(deltaLat / 2), 2)
                        + Math.cos(fromLatRad)
                                * Math.cos(toLatRad)
                                * Math.pow(Math.sin(deltaLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }

    public static String formatDistanceKm(double distanceKm) {
        return BigDecimal.valueOf(distanceKm).setScale(3, RoundingMode.HALF_UP) + " km";
    }
}
