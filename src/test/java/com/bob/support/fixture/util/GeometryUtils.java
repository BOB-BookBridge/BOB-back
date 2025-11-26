package com.bob.support.fixture.util;

import static java.lang.Math.cos;
import static java.lang.Math.toRadians;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;

public final class GeometryUtils {

    private static final int SRID_WGS84 = 4326;
    private static final GeometryFactory GF = new GeometryFactory(new PrecisionModel(), SRID_WGS84);

    private GeometryUtils() {
    }

    /**
     * 중심 (lat, lon) 주변으로 meters 반경의 정사각형 Polygon 생성 (WGS84).
     * 좌표 순서: (lon, lat)
     */
    public static Polygon squareWgs84(double lat, double lon, double meters) {
        double dLat = meters / 111_320.0;
        double dLon = meters / (111_320.0 * cos(toRadians(lat)));

        Coordinate[] ring = new Coordinate[] {
            new Coordinate(lon - dLon, lat - dLat),
            new Coordinate(lon + dLon, lat - dLat),
            new Coordinate(lon + dLon, lat + dLat),
            new Coordinate(lon - dLon, lat + dLat),
            new Coordinate(lon - dLon, lat - dLat)
        };

        LinearRing shell = GF.createLinearRing(ring);
        Polygon poly = GF.createPolygon(shell, null);
        poly.setSRID(SRID_WGS84);
        return poly;
    }
}
