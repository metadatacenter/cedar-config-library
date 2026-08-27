package org.metadatacenter.config;

import java.util.OptionalLong;

/** Test-process overrides that are absent from every normal CEDAR runtime. */
public final class CedarTestRuntime {

  public static final String DEPENDENCY_TIMEOUT_MILLIS_PROPERTY =
      "cedar.test.dependencyTimeoutMillis";

  private CedarTestRuntime() {
  }

  public static OptionalLong dependencyTimeoutMillis() {
    return positiveMillis(DEPENDENCY_TIMEOUT_MILLIS_PROPERTY);
  }

  private static OptionalLong positiveMillis(String property) {
    String value = System.getProperty(property);
    if (value == null || value.isBlank()) {
      return OptionalLong.empty();
    }
    long timeout = Long.parseLong(value);
    if (timeout <= 0) {
      throw new IllegalArgumentException(property + " must be a positive integer");
    }
    return OptionalLong.of(timeout);
  }
}
