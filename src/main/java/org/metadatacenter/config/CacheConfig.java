package org.metadatacenter.config;

public class CacheConfig {

  private CacheServerPersistent persistent;

  private CacheServerNonPersistent nonPersistent;

  public CacheServerPersistent getPersistent() {
    return persistent;
  }

  public CacheServerNonPersistent getNonPersistent() {
    return nonPersistent;
  }
}
