package org.metadatacenter.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.OptionalInt;

/**
 * What one hop or one external authority changes about the class of call that reaches it.
 *
 * <p>Only the connect and response timeouts can be overridden here. Those two bound a single
 * request and can be set on it. The third timeout, the wait for a connection out of the pool, is a
 * property of the pool rather than of a request, so a per-hop value for it would mean a pool per
 * hop; a hop that genuinely needs its own pool belongs in its own class of call instead.
 *
 * <p>An absent value means the class's own, which is why the fields are boxed: zero is a value a
 * configuration file can state and an omission is not.
 */
public class OutboundTimeoutOverride {

  @JsonProperty @Min(100) @Max(120_000) private Integer connectMillis;
  @JsonProperty @Min(100) @Max(600_000) private Integer responseMillis;

  public OutboundTimeoutOverride() {
  }

  public OutboundTimeoutOverride(Integer connectMillis, Integer responseMillis) {
    this.connectMillis = connectMillis;
    this.responseMillis = responseMillis;
  }

  public OptionalInt getConnectMillis() {
    return connectMillis == null ? OptionalInt.empty() : OptionalInt.of(connectMillis);
  }

  public OptionalInt getResponseMillis() {
    return responseMillis == null ? OptionalInt.empty() : OptionalInt.of(responseMillis);
  }

  public boolean isEmpty() {
    return connectMillis == null && responseMillis == null;
  }
}
