package com.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Util {
  private static void log(String msg) {
    System.out.println(msg);
  }

  public static String getDomain(String url) {
    try {
      return new URI(url).getHost();
    } catch (URISyntaxException e) {
      log("Invalid url: " + url);
      return "";
    }
  }

  public static boolean isSameDomain(String url, String baseDomain) {
    String domain = getDomain(url);
    return domain != null && domain.equals(baseDomain);
  }

  public static boolean isSamePage(String baseUrl, String url) {
    return url.startsWith(baseUrl + "#");
  }

  public static String normalizeUrl(String baseUrl, String href) {
    try {
      if(href.endsWith("/")) {
        href = href.substring(0, href.length()-1);
      }
      if (!isValidUri(href)) {
        href = URLEncoder.encode(href, StandardCharsets.UTF_8.toString());
      }
      URI baseUri = new URI(baseUrl);
      URI hrefUri = new URI(href);

      URI fullUri = baseUri.resolve(hrefUri).normalize();
      if(!isValid((fullUri))) return "";
      String uriStr = fullUri.toString();
      if(uriStr.indexOf("#") >= 0) {
        return uriStr.substring(0, uriStr.indexOf("#"));
      } else {
        return uriStr;
      }
    } catch (Exception e) {
      System.err.println("normalizeUrl error: " + baseUrl + ", " + href + ", " + e.getMessage());
      e.printStackTrace();
      return href;
    }
  }
  public static boolean isValidUri(String uriString) {
    try {
      new URI(uriString);
      return true;
    } catch (java.net.URISyntaxException e) {
      return false;
    }
  }
  public static boolean isValid(URI uri) {
    if (!uri.isAbsolute()) {
      return false;
    }
    String scheme = uri.getScheme();
    String host = uri.getHost();
    return scheme != null && host != null;
  }
}
