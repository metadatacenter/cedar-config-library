package org.metadatacenter.server.url;

import org.metadatacenter.config.OutboundTimeoutOverride;

/**
 * The URLs of one hop, and what that hop changes about the timeouts a call to it runs under.
 *
 * <p>The two travel together so that a call site cannot obtain the address of a dependency without
 * also obtaining the bound on reaching it.
 */
public abstract class MicroserviceUrlProvider {

  protected final String base;
  private final OutboundTimeoutOverride timeouts;

  public MicroserviceUrlProvider(String base, OutboundTimeoutOverride timeouts) {
    this.base = base;
    this.timeouts = timeouts == null ? new OutboundTimeoutOverride() : timeouts;
  }

  public OutboundTimeoutOverride getTimeouts() {
    return timeouts;
  }
}
