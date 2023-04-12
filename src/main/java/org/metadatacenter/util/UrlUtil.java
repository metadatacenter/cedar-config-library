package org.metadatacenter.util.http;

import java.net.URLDecoder;
import java.net.URLEncoder;
import com.google.common.base.Charsets;

public final class UrlUtil {

  private UrlUtil() {
  }

  public static String trimUrlParameters(String url) {
    if (url != null) {
      int p = url.indexOf('?');
      if (p > -1) {
        return url.substring(0, p - 1);
      }
    }
    return url;
  }

  public static String urlEncode(String value) {
    try {
      return URLEncoder.encode(value, Charsets.UTF_8.name());
    } catch (Exception e) {
      // Do nothing
    }
    return null;
  }

  public static String urlDecode(String value) {
    try {
      return URLDecoder.decode(value, Charsets.UTF_8.name());
    } catch (Exception e) {
      // Do nothing
    }
    return null;
  }
}
