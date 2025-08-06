package com.bob.infra.auth.filter.port;

public interface AuthRedisPort {

  String updateRefreshKey(String oldKey, String newKey, String value);

  void removeRefreshKey(String key);
}
