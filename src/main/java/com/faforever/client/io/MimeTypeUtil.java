package com.faforever.client.io;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MimeTypeUtil {
  public String getMimeType(String path) {
    if (path.endsWith(".html")) {
      return "text/html";
    }
    if (path.endsWith(".css")) {
      return "text/css";
    }
    if (path.endsWith(".js")) {
      return "text/javascript";
    }
    return "text/html";
  }
}
