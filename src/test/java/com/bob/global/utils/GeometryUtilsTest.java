package com.bob.global.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;

import com.bob.global.utils.geo.GeometryUtils;

@DisplayName("GeometryUtils 테스트")
class GeometryUtilsTest {

    @Test
    void 위도_경도_기반_Point_생성() throws NoSuchFieldException {
        double latitude = 37.123456;
        double longitude = 127.654321;

        Point point = GeometryUtils.createPoint(latitude, longitude);

        assertThat(point).isNotNull();
        assertThat(point.getX()).isEqualTo(longitude);
        assertThat(point.getY()).isEqualTo(latitude);
        assertThat(point.getSRID()).isEqualTo(4326);
        assertThat(GeometryUtils.class.getDeclaredField("geometryFactory")).isNotNull();
    }

    @Test
    void 위도_경도_추출() {
        double latitude = 35.0;
        double longitude = 128.0;
        Point point = GeometryUtils.createPoint(latitude, longitude);

        double lat = GeometryUtils.getLatitude(point);
        double lon = GeometryUtils.getLongitude(point);

        assertThat(lat).isEqualTo(latitude);
        assertThat(lon).isEqualTo(longitude);
    }
}
