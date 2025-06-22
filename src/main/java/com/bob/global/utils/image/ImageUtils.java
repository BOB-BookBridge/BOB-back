package com.bob.global.utils.image;

import static com.bob.global.utils.uuid.UuidUtils.randomV7;

import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.List;

public class ImageUtils {

  public static String generateImageFileName(String contentType, ImageDirectory directory) {
    String extension = extractExtension(contentType);
    String uuid = randomV7().toString();
    return directory.getPrefix() + uuid + "." + extension;
  }

  public static List<String> generateImageFileNames(List<String> contentTypes, ImageDirectory directory) {
    return contentTypes.stream().map((type) -> generateImageFileName(type, directory)).toList();
  }

  public static String extractExtension(String contentType) {
    return switch (contentType) {
      case "image/jpeg" -> "jpg";
      case "image/png" -> "png";
      case "image/gif" -> "gif";
      default -> throw new ApplicationException(ApplicationError.UN_SUPPORTED_TYPE);
    };
  }
}
