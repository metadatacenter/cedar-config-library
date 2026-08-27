package org.metadatacenter.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CedarTestRuntimeTest {

  @Test
  void readsAConfiguredDependencyTimeout() {
    String property = CedarTestRuntime.DEPENDENCY_TIMEOUT_MILLIS_PROPERTY;
    String previous = System.getProperty(property);
    try {
      System.setProperty(property, "1000");
      assertEquals(1000, CedarTestRuntime.dependencyTimeoutMillis().orElseThrow());
    } finally {
      restore(property, previous);
    }
  }

  @Test
  void leavesProductionTimeoutsUnchangedWhenNoOverrideExists() {
    String property = CedarTestRuntime.DEPENDENCY_TIMEOUT_MILLIS_PROPERTY;
    String previous = System.getProperty(property);
    try {
      System.clearProperty(property);
      assertTrue(CedarTestRuntime.dependencyTimeoutMillis().isEmpty());
    } finally {
      restore(property, previous);
    }
  }

  private static void restore(String property, String previous) {
    if (previous == null) {
      System.clearProperty(property);
    } else {
      System.setProperty(property, previous);
    }
  }
}
