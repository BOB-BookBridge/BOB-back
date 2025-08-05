package com.bob.infra.auth.filter.port;

public interface AuthRedisPort {

  void updateRefreshKey(String oldKey, String newKey, String value, int expireDays);

  void removeRefreshKey(String key);
}
