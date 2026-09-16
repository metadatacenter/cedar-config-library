package org.metadatacenter.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * One class of outbound HTTP call: the three timeouts that bound it and the pool it runs under.
 *
 * <p>The lease timeout is the wait for a connection out of the pool, and it is consulted before
 * either of the other two, so a hop nominally bounded by its connect and response timeouts can
 * hold a worker thread for the lease on top of them.
 */
public class OutboundCallClassConfig {

  @JsonProperty @Min(100) @Max(120_000) private int connectMillis;
  @JsonProperty @Min(100) @Max(120_000) private int leaseMillis;
  @JsonProperty @Min(100) @Max(600_000) private int responseMillis;
  @JsonProperty @Min(1) @Max(1_000) private int maxConnectionsPerRoute;
  @JsonProperty @Min(1) @Max(2_000) private int maxConnectionsTotal;

  public OutboundCallClassConfig() {
  }

  public OutboundCallClassConfig(int connectMillis, int leaseMillis, int responseMillis,
                                 int maxConnectionsPerRoute, int maxConnectionsTotal) {
    this.connectMillis = connectMillis;
    this.leaseMillis = leaseMillis;
    this.responseMillis = responseMillis;
    this.maxConnectionsPerRoute = maxConnectionsPerRoute;
    this.maxConnectionsTotal = maxConnectionsTotal;
  }

  public int getConnectMillis() {
    return connectMillis;
  }

  public int getLeaseMillis() {
    return leaseMillis;
  }

  public int getResponseMillis() {
    return responseMillis;
  }

  public int getMaxConnectionsPerRoute() {
    return maxConnectionsPerRoute;
  }

  public int getMaxConnectionsTotal() {
    return maxConnectionsTotal;
  }
}
