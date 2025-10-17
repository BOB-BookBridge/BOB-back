package com.bob.global.utils.uuid;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.UUID;

public class UuidUtils {

  public static final String UUID_V7_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-7[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}";

  private static final SecureRandom random = new SecureRandom();

  public static UUID randomV7() {
    ByteBuffer buf = ByteBuffer.wrap(randomBytes());
    long high = buf.getLong();
    long low = buf.getLong();
    return new UUID(high, low);
  }

  private static byte[] randomBytes() {
    byte[] value = new byte[16];
    random.nextBytes(value);

    ByteBuffer timestamp = ByteBuffer.allocate(Long.BYTES);
    timestamp.putLong(System.currentTimeMillis());

    System.arraycopy(timestamp.array(), 2, value, 0, 6);

    value[6] = (byte) ((value[6] & 0x0F) | 0x70);
    value[8] = (byte) ((value[8] & 0x3F) | 0x80);

    return value;
  }
}
