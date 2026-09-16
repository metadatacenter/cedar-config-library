package org.metadatacenter.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * How far every outbound HTTP call is allowed to get, by the kind of call it is.
 *
 * <p>Three kinds are distinguished. An interactive call is one CEDAR service reaching the next
 * while a user waits. A batch call comes from a job nobody waits on, so minutes of legitimate work
 * per call is normal and failing it early is worse than letting it run. An external call leaves the
 * estate for a registry CEDAR does not operate, where the network is the internet rather than a
 * loopback and no value chosen for the other two kinds describes it.
 *
 * <p>The defaults below are the values the estate ran on before any of this was configurable, with
 * one exception. External calls used the interactive connect timeout of one second, which is
 * generous for the next process on the same host and mean for a cold TLS handshake to a
 * transatlantic one, so a slow registry was reported to the user as an unavailable one. Their
 * response timeout is deliberately left where it was: choosing it properly needs latency data that
 * the request log does not yet carry, and a configurable value is what lets the measured one land
 * without a code change.
 */
public class OutboundHttpConfig {

  @JsonProperty @Valid @NotNull
  private OutboundCallClassConfig interactive = new OutboundCallClassConfig(1_000, 1_000, 20_000, 100, 200);

  @JsonProperty @Valid @NotNull
  private OutboundCallClassConfig batch = new OutboundCallClassConfig(3_000, 5_000, 120_000, 10, 20);

  @JsonProperty @Valid @NotNull
  private OutboundCallClassConfig external = new OutboundCallClassConfig(5_000, 2_000, 20_000, 20, 40);

  public OutboundCallClassConfig getInteractive() {
    return interactive;
  }

  public OutboundCallClassConfig getBatch() {
    return batch;
  }

  public OutboundCallClassConfig getExternal() {
    return external;
  }
}
