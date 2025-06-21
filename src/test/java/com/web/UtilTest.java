package com.web;

import org.junit.jupiter.api.Test;
import java.net.URI;
import static org.junit.jupiter.api.Assertions.*;


class UtilTest {

  @Test
  void testGetDomain() {
    assertEquals("www.example.com", Util.getDomain("http://www.example.com"));
    assertEquals("en.wikipedia.org", Util.getDomain("https://en.wikipedia.org"));
    assertNull(Util.getDomain("invalid-url"));
  }

  @Test
  void testIsSameDomain() {
    assertTrue(Util.isSameDomain("https://en.wikipedia.org/wiki/Main_Page", "en.wikipedia.org"));
    assertFalse(Util.isSameDomain("https://www.example.com", "en.wikipedia.org"));
    assertFalse(Util.isSameDomain("invalid-url", "en.wikipedia.org"));
  }

  @Test
  void testNormalizedUrl() {
    String baseUrl = "https://en.wikipedia.org/";
    testNormalizeUrl(baseUrl);
    baseUrl = "https://en.wikipedia.org";
    testNormalizeUrl(baseUrl);
  }
  void testNormalizeUrl(String baseUrl) {
    // different domain
    assertEquals("http://www.example.com/default.htm", Util.normalizeUrl(baseUrl, "http://www.example.com/default.htm"));
    assertEquals("ftp://www.ftpserver.com", Util.normalizeUrl(baseUrl, "ftp://www.ftpserver.com"));
    // same domain
    assertEquals("https://en.wikipedia.org/default.htm", Util.normalizeUrl(baseUrl, "default.htm"));
    assertTrue(Util.normalizeUrl(baseUrl, "/ /default.htm").startsWith(baseUrl));
    // invalid href
    assertEquals("", Util.normalizeUrl(baseUrl, "javascript:alert('Hello');"));

    // url contains section
    assertEquals(baseUrl, Util.normalizeUrl(baseUrl, "#"));
    assertEquals(baseUrl, Util.normalizeUrl(baseUrl, "#section1"));
  }

  @Test
  void testIsValid() {
    assertTrue(Util.isValid(URI.create("https://en.wikipedia.org")));
    assertFalse(Util.isValid(URI.create("invalid-url")));
    assertFalse(Util.isValid(URI.create("mailto:example@example.com")));
  }

  @Test
  void testIsSamePage() {
    String baseUrl = "https://en.wikipedia.org";
    assertTrue(Util.isSamePage(baseUrl, "https://en.wikipedia.org#section1"));
    assertFalse(Util.isSamePage(baseUrl, "https://en.wikipedia.org/default.htm"));
    assertFalse(Util.isSamePage(baseUrl, "http://www.example.com#section1"));
  }
}