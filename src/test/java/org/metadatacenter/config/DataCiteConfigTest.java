package org.metadatacenter.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataCiteConfigTest {

  @Test
  void hasBoundedHttpTimeoutDefaults() {
    DataCiteConfig config = new DataCiteConfig();

    assertEquals(5000, config.getConnectTimeout());
    assertEquals(20000, config.getRequestTimeout());
  }
}
