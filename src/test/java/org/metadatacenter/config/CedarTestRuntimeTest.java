package org.metadatacenter.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CedarTestRuntimeTest {

  @Test
  void mavenTestProcessCarriesTheShortDependencyTimeout() {
    assertEquals(1000, CedarTestRuntime.dependencyTimeoutMillis().orElseThrow());
  }
}
