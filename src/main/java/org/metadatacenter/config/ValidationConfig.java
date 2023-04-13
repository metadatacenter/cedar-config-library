package org.metadatacenter.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidationConfig {

  @JsonProperty("enabled")
  private boolean enabled;

  public boolean isEnabled() {
    return enabled;
  }
}
