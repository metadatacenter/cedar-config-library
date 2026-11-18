package org.metadatacenter.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class ServerConfig {

  private int httpPort;

  private int adminPort;

  private String base;

  /**
   * What this hop changes about the class of call that reaches it, if anything.
   *
   * <p>A hop whose work is unlike the others' is why this exists: a large instance write that the
   * artifact server validates before answering is the plausible outlier among calls that are
   * otherwise a lookup and a reply.
   */
  @JsonProperty @Valid
  private OutboundTimeoutOverride timeouts = new OutboundTimeoutOverride();

  public int getHttpPort() {
    return httpPort;
  }

  public int getAdminPort() {
    return adminPort;
  }

  public String getBase() {
    return base;
  }

  public OutboundTimeoutOverride getTimeouts() {
    return timeouts;
  }
}
