package org.metadatacenter.config;

public class CacheConfig {

  private CacheServerPersistent persistent;

  private CacheServerNonPersistent nonPersistent;

  private TerminologyCache terminology;

  public CacheServerPersistent getPersistent() {
    return persistent;
  }

  public CacheServerNonPersistent getNonPersistent() {
    return nonPersistent;
  }

  public TerminologyCache getTerminology() {
    return terminology;
  }
}
