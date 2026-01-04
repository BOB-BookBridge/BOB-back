package com.bob.security.application.port.out.infra;

import java.util.Optional;

public interface AuthCachePort {

    void setRefreshKey(String current, String value);

    void updateRefreshKey(String old, String current, String value);

    Optional<String> get(String key);
}
