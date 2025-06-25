package com.bob.global.utils.image;

import static com.bob.global.utils.uuid.UuidUtils.randomV7;

import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.List;
import java.util.Locale;

public class ImageUtils {

  public static String generateImageFileName(ImageDirectory directory, String contentType) {
    String extension = extractExtension(contentType);
    String uuid = randomV7().toString();
    return directory.getPrefix() + uuid + "." + extension;
  }

  public static List<String> generateImageFileNames(ImageDirectory directory, List<String> contentTypes) {
    return contentTypes.stream()
        .map((type) -> generateImageFileName(directory, type))
        .toList();
  }

  public static String extractExtension(String contentType) {
    return switch (contentType.toLowerCase(Locale.ROOT)) {
      case "image/jpeg" -> "jpg";
      case "image/png" -> "png";
      case "image/gif" -> "gif";
      default -> throw new ApplicationException(ApplicationError.UN_SUPPORTED_TYPE);
    };
  }
}
