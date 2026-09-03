package org.metadatacenter.config;

/**
 * The Redis CEDAR uses.
 *
 * <p>There was a second, non-persistent server here alongside the persistent one. Nothing ever called
 * its getter, and the two variables behind it were declared by no component, so the block resolved to
 * a literal {@code ${CEDAR_REDIS_NONPERSISTENT_HOST}} and a port of zero in every service — a
 * connection that would have failed had anything opened it. It was removed rather than repaired
 * because there was nothing to repair it for.
 */
public class CacheConfig {

  private CacheServerPersistent persistent;

  public CacheServerPersistent getPersistent() {
    return persistent;
  }
}
